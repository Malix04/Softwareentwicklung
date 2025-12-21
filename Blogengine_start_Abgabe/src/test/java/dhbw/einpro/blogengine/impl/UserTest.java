package dhbw.einpro.blogengine.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    void setUp() {
        user1 = new User("Anna", "Meier", "anna@example.com");
        user2 = new User("Bernd", "Meier", "bernd@example.com");
        user3 = new User("Anna", "Meier", "zeta@example.com");
        user4 = new User("Anna", "Meier", "anna@example.com"); // identisch zu user1
    }

    @Test
    @DisplayName("compareTo sortiert nach Nachname -> Vorname -> Email")
    void compareTo_sollNachNameVornameEmailSortieren() {
        // Arrange (Users aus setUp)

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

    @Test
    @DisplayName("equals/hashCode: gleiche Daten => equals true und gleicher hashCode")
    void equalsHashCode_sollBeiGleichenDatenGleichSein() {
        // Arrange
        User sameAsUser1 = new User("Anna", "Meier", "anna@example.com");

        // Act
        boolean equals = user1.equals(sameAsUser1);
        int h1 = user1.hashCode();
        int h2 = sameAsUser1.hashCode();

        // Assert
        assertTrue(equals, "User mit gleichen Daten müssen equals == true sein");
        assertEquals(h1, h2, "Gleiche User müssen gleichen hashCode haben");
        assertEquals(0, user1.compareTo(sameAsUser1), "compareTo muss bei gleichen Daten 0 liefern");
    }

    @Test
    @DisplayName("Setter/Getter: Werte können gesetzt und gelesen werden")
    void setterGetter_sollWerteAktualisieren() {
        // Arrange
        User u = new User("A", "B", "a@b.de");

        // Act
        u.setFirstName("Max");
        u.setLastName("Mustermann");
        u.setEmail("max@mustermann.de");

        // Assert
        assertEquals("Max", u.getFirstName());
        assertEquals("Mustermann", u.getLastName());
        assertEquals("max@mustermann.de", u.getEmail());
    }
}
