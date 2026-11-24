package cat.itacademy.s05.appointmentsapp.identity.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void emailWithValidParameterShouldInitializeCorrectly() {
        Email testEmail = new Email("test@test.com");

        assertEquals("test@test.com", testEmail.value());
    }

    @Test
    void nullEmailValueShouldThrowException() {
        assertThrows(IllegalArgumentException.class, ()-> new Email(null));
    }


    @Test
    void emptyEmailValueShouldThrowException() {
        assertThrows(IllegalArgumentException.class, ()-> new Email(""));
    }

    @Test
    void emailValueLongerThanMaximumAllowedThrowsException() {


        String longValue = "a".repeat(255);
        assertThrows(IllegalArgumentException.class, ()-> new Email(String.format("%s@test.com", longValue)));
    }

    // Wrong format
    @Test
    void invalidFormatShouldThrowException() {
        assertThrows(IllegalArgumentException.class, ()-> new Email("wrongFormatEmail"));
    }



}