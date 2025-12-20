package dhbw.einpro.blogengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dhbw.einpro.blogengine.impl.User;

class UserTest {

    @Test
    @DisplayName("User equality basiert auf Email + Vorname + Nachname (Case/Trim-insensitive)")
    void userEqualityShouldWork() {
        // Arrange
        User u1 = new User("Valentin", "Muster", "Test@Mail.de ");
        User u2 = new User("valentin", "muster", " test@mail.de");

        // Act + Assert
        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }
}
