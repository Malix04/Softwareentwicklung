package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.impl.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    /**
     * Testet, ob compareTo die Benutzer in der Reihenfolge
     * Nachname -> Vorname -> Email-Adresse vergleicht.
     *
     * AAA-Schema:
     *  - Arrange: Testdaten anlegen
     *  - Act: compareTo aufrufen
     *  - Assert: Ergebnis prüfen
     */
    @Test
    void compareTo_sollNachNameVornameEmailSortieren() {
        // Arrange
        User user1 = new User("Anna", "Meier", "anna@example.com");
        User user2 = new User("Bernd", "Meier", "bernd@example.com");   // gleicher Nachname, anderer Vorname
        User user3 = new User("Anna", "Meier", "zeta@example.com");     // gleicher Nachname & Vorname, andere Email
        User user4 = new User("Anna", "Meier", "anna@example.com");     // komplett identisch zu user1

        // Act + Assert

        // 1) Nachname gleich, Vorname unterschiedlich -> "Anna" < "Bernd"
        assertTrue(user1.compareTo(user2) < 0,
                "user1 (Anna Meier) sollte vor user2 (Bernd Meier) kommen");
        assertTrue(user2.compareTo(user1) > 0,
                "user2 (Bernd Meier) sollte nach user1 (Anna Meier) kommen");

        // 2) Nachname & Vorname gleich, Email unterschiedlich -> "anna@" < "zeta@"
        assertTrue(user1.compareTo(user3) < 0,
                "Bei gleichem Namen sollte die Email die Reihenfolge bestimmen");

        // 3) Vollständig gleiche Daten -> compareTo == 0
        assertEquals(0, user1.compareTo(user4),
                "Zwei identische Benutzer sollten compareTo == 0 liefern");
    }
}
