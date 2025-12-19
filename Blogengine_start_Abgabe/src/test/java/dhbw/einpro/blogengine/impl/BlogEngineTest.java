package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlogEngineTest {

    @Test
    @DisplayName("addUser speichert User und erhöht size")
    void addUser_sollUserSpeichernUndSizeErhoehen() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user = new User("Max", "Meier", "max@example.com");

        // Act
        boolean result = blogEngine.addUser(user);

        // Assert
        assertTrue(result);
        assertEquals(1, blogEngine.size());
        assertTrue(blogEngine.containsUser(user));
    }

    @Test
    @DisplayName("addUser: gleiche Email -> DuplicateEmailException")
    void addUser_sollDuplicateEmailExceptionBeiGleicherEmailWerfen() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user1 = new User("Max", "Meier", "max@example.com");
        User user2 = new User("Anna", "Schmidt", "max@example.com");

        blogEngine.addUser(user1);

        // Act + Assert
        assertThrows(DuplicateEmailException.class,
                () -> blogEngine.addUser(user2));
    }

    @Test
    @DisplayName("addUser: gleicher User -> DuplicateUserException")
    void addUser_sollDuplicateUserExceptionBeiGleichemUserWerfen() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user = new User("Max", "Meier", "max@example.com");
        blogEngine.addUser(user);

        // Act + Assert
        assertThrows(DuplicateUserException.class,
                () -> blogEngine.addUser(user));
    }
}