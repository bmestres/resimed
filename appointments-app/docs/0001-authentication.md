# ADR 0001: Authentication and session mechanism for people

- **Status:** Proposed (accepted once the author confirms the proposed values marked "proposal")
- **Date:** 2026-11-18
- **Scope:** Staff, Referents and administrators. Origins (client systems) are out of scope: they use opaque API keys, see "Origins" below.

## Context

The backend is a stateless Spring Boot service (containers on ECS Fargate). The frontend is a separate React SPA served from S3/CloudFront. Permissions are computed on the server on every request, and deactivating a Care Home, a staff member or a connection must take effect immediately. The stack table already fixes "Spring Security + JWT for people". This ADR fixes the details.

## Decision

1. **Access token: JWT, lifetime 1 hour.**
   - Signed with HS256 (single service, one secret held in the environment secrets store). Revisit if a second service ever validates tokens.
   - Claims: `sub` (user UUID), `role`, `iat`, `exp`, `jti`. No names, emails, patient data or Care Home data.
   - The token is sent as `Authorization: Bearer ...` and kept **in memory only** in the SPA (never `localStorage`).
2. **Refresh token: opaque, random, rotated on every use.**
   - 256-bit random value. Only its SHA-256 hash is stored (table `refresh_token`: user, hash, family id, issued at, expires at, used at, revoked at).
   - Lifetime: 14 days (proposal), absolute per family.
   - **Rotation with reuse detection:** using a refresh token returns a new one and marks the old one as used. If an already-used token is presented again, the whole family is revoked.
   - Sent as an `HttpOnly`, `Secure` cookie, `Path=/auth/refresh`, `SameSite=Strict` (or `Lax`). This requires the SPA and the API to share the same registrable domain (for example `app.example.com` and `api.example.com`). The refresh endpoint also requires a custom header, as a second CSRF defence.
3. **Revocation: the database is checked on every request.**
   - A JWT cannot be revoked before it expires, so the 1-hour lifetime is **not** the revocation window. A request filter loads the user and checks: user active, Care Home active (staff), role unchanged, connection state (for Care Home access to appointments).
   - Deactivating a user, a Care Home, or ending a connection takes effect on the next request. It also revokes the user's refresh token families.
   - Cost: one cheap indexed lookup per request. Acceptable at this scale; a cache with explicit invalidation can be added later if needed.
4. **Password hashing: bcrypt**, through Spring Security's `DelegatingPasswordEncoder`.
   - Strength (cost) 12 (proposal). Passwords are limited to 72 bytes by bcrypt: validate the maximum length and reject longer ones instead of silently truncating.
   - Stored with the `{bcrypt}` prefix, so migrating to Argon2id later is possible without breaking existing passwords (re-hash on next successful login).
5. **Single-use links** (staff activation, Referent invitation, password recovery, email verification): random 256-bit token, only the hash stored, short expiry, single use, generic responses that do not reveal whether an account exists.

## Origins (not people)

Origins authenticate with an opaque API key (`Authorization: Bearer ...`). The origin is deduced from the key. Only a **SHA-256 hash** is stored (the keys are long random values, so a slow hash is unnecessary). Keys can be rotated and revoked by an administrator. The event endpoint always answers `202`.

## Alternatives considered

- **Server-side sessions:** simple revocation, but needs shared session storage across containers and sticky behaviour with a separate SPA origin. Rejected.
- **Access token of 15 minutes:** smaller exposure if a token leaks, but since every request already checks the database, the extra security gain is small and the user would refresh more often. We chose 1 hour; reduce it if the threat model changes.
- **Refresh token in `localStorage`:** readable by any injected script. Rejected.
- **Argon2id:** stronger against GPU attacks; needs Bouncy Castle. Deferred, not rejected: the delegating encoder keeps the door open.

## Consequences

- One extra table (`refresh_token`) and a request filter that hits the database.
- The SPA must call `/auth/refresh` when the access token expires and handle a failed refresh by sending the user to login.
- Frontend and API must share a registrable domain in every environment (local development included, using a proxy or a shared host name).
- Rotation with reuse detection means two tabs refreshing at the same time must be handled carefully (short grace window or a single refresh coordinator in the SPA).

## Open points

- Refresh lifetime (14 days) and bcrypt cost (12) are proposals.
- Account lockout / rate limiting on login and refresh endpoints: define in the authentication issue.
- Multi-factor authentication: out of scope for the MVP.
