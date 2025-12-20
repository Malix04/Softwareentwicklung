package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.PostNotFoundException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlogEngineTest {

    // FakeUser: zweite IUser-Implementierung (nicht User),
    // damit wir Spezifikations-Gleichheit (Email+Vorname+Nachname) testen.
    private static final class FakeUser implements IUser {
        private String firstName;
        private String lastName;
        private String email;

        private FakeUser(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        @Override public String getEmail() { return email; }
        @Override public void setEmail(String p_email) { this.email = p_email; }
        @Override public String getFirstName() { return firstName; }
        @Override public void setFirstName(String p_firstName) { this.firstName = p_firstName; }
        @Override public String getLastName() { return lastName; }
        @Override public void setLastName(String p_lastName) { this.lastName = p_lastName; }
    }

    @Test
    void addUser_sollUserSpeichernUndSizeErhoehen() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user = new User("Max", "Meier", "max@example.com");

        // Act
        boolean added = blogEngine.addUser(user);

        // Assert
        assertTrue(added);
        assertEquals(1, blogEngine.size());
        assertTrue(blogEngine.containsUser(user));
    }

    @Test
    void addUser_sollDuplicateEmailExceptionBeiGleicherEmailWerfen() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User user1 = new User("Max", "Meier", "max@example.com");
        User user2 = new User("Lisa", "Muster", "max@example.com"); // gleiche Email

        blogEngine.addUser(user1);

        // Act + Assert
        assertThrows(DuplicateEmailException.class, () -> blogEngine.addUser(user2));
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

    @Test
    void removePost_sollAuchMitFremdemIUserObjektAlsAutorLoeschenDuerfen()
            throws DuplicateEmailException, DuplicateUserException, UserNotFoundException, PostNotFoundException, IllegalOperationException {

        // Arrange
        BlogEngine blogEngine = new BlogEngine();
        User author = new User("Max", "Meier", "max@example.com");
        blogEngine.addUser(author);

        Post post = new Post("Titel", "Inhalt", author, blogEngine);
        int id = blogEngine.addPost(post);

        IUser sameButNotUser = new FakeUser("Max", "Meier", "max@example.com");

        // Act
        blogEngine.removePost(sameButNotUser, id);

        // Assert
        assertFalse(blogEngine.containsPost(id));
    }
}