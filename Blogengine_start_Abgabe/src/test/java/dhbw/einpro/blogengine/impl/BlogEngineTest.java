package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlogEngineTest {

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

    @Test
    void addUser_sollDuplicateUserOderDuplicateEmailWerfenWennGleicherUserNochmalHinzugefuegtWird()
            throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user = new User("Max", "Meier", "max@example.com");
        blogEngine.addUser(user);

        // Act + Assert
        try {
            blogEngine.addUser(user);
            fail("Es hätte eine DuplicateUserException oder DuplicateEmailException geworfen werden müssen.");
        } catch (DuplicateUserException | DuplicateEmailException expected) {
            // ok: Javadoc legt keine Priorität/Reihenfolge fest
        }
    }
}