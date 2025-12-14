package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlogEngineTest {

    /**
     * Testet, dass ein neuer Benutzer erfolgreich hinzugefügt wird.
     * AAA: Arrange – Act – Assert
     */
    @Test
    void addUser_sollUserSpeichernUndSizeErhoehen() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user = new User("Max", "Meier", "max@example.com");

        // Act
        boolean result = blogEngine.addUser(user);

        // Assert
        assertTrue(result, "addUser sollte true liefern, wenn der User hinzugefügt werden konnte");
        assertEquals(1, blogEngine.size(), "Nach dem ersten User sollte size() == 1 sein");
        assertTrue(blogEngine.containsUser(user), "containsUser sollte den hinzugefügten User finden");
    }

    /**
     * Testet, dass bei zwei verschiedenen Benutzern mit gleicher Email
     * eine DuplicateEmailException geworfen wird.
     */
    @Test
    void addUser_sollDuplicateEmailExceptionBeiGleicherEmailWerfen()
            throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user1 = new User("Max", "Meier", "max@example.com");
        User user2 = new User("Anna", "Schmidt", "max@example.com"); // gleiche Email, anderer Name

        blogEngine.addUser(user1);

        // Act + Assert
        assertThrows(DuplicateEmailException.class,
                () -> blogEngine.addUser(user2),
                "Zwei unterschiedliche User mit gleicher Email müssen DuplicateEmailException auslösen");
    }

    /**
     * Testet, dass beim erneuten Hinzufügen desselben Users
     * eine DuplicateUserException geworfen wird.
     */
    @Test
    void addUser_sollDuplicateUserExceptionBeiGleichemUserWerfen()
            throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user = new User("Max", "Meier", "max@example.com");

        blogEngine.addUser(user);

        // Act + Assert
        assertThrows(DuplicateUserException.class,
                () -> blogEngine.addUser(user),
                "Der gleiche User zweimal muss DuplicateUserException auslösen");
    }
}