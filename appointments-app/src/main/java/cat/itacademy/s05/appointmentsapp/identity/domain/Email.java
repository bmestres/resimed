package cat.itacademy.s05.appointmentsapp.identity.domain;


import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final int MAX_EMAIL_LENGTH = 254;
    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

        public Email {
            if(value == null) {
                throw new IllegalArgumentException("Email value must not be NULL");
            }

            value = value.strip().toLowerCase(Locale.ROOT);

            if(value.isBlank()) {
                throw new IllegalArgumentException("Email value must not be empty");
            }
            if(value.length() > MAX_EMAIL_LENGTH) {
                throw new IllegalArgumentException(String.format("Email length must not be greater than %d characters", MAX_EMAIL_LENGTH));
            }
            if(!EMAIL_FORMAT.matcher(value).matches()) {
                throw new IllegalArgumentException("Incorrect email format");
            }
        }
 }
