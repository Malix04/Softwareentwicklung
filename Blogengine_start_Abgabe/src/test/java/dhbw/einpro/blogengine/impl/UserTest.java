package dhbw.einpro.blogengine.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("compareTo sortiert nach Nachname -> Vorname -> Email")
    void compareTo_sollNachNameVornameEmailSortieren() {
        // Arrange
        User user1 = new User("Anna", "Meier", "anna@example.com");
        User user2 = new User("Bernd", "Meier", "bernd@example.com");
        User user3 = new User("Anna", "Meier", "zeta@example.com");
        User user4 = new User("Anna", "Meier", "anna@example.com");

        // Act
        int result1 = user1.compareTo(user2);
        int result2 = user2.compareTo(user1);
        int result3 = user1.compareTo(user3);
        int result4 = user1.compareTo(user4);

        // Assert
        assertTrue(result1 < 0, "Anna Meier sollte vor Bernd Meier kommen");
        assertTrue(result2 > 0, "Bernd Meier sollte nach Anna Meier kommen");
        assertTrue(result3 < 0, "Bei gleichen Namen entscheidet die Email");
        assertEquals(0, result4, "Identische User sollten compareTo == 0 liefern");
    }
}
