CREATE TABLE user_account (
    id UUID NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    active BOOL DEFAULT false NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_user_account PRIMARY KEY (id),
    CONSTRAINT uk_user_account_email UNIQUE (email),
    CONSTRAINT ck_user_account_role CHECK (role IN('ADMIN', 'STAFF', 'REFERENT')),
    CONSTRAINT ck_user_account_email_lowercase CHECK (email = lower(email))
);
