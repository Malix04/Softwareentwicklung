package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.PostNotFoundException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class BlogEngineTest {

    private BlogEngine blogEngine;

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

    @BeforeEach
    void setUp() {
        blogEngine = new BlogEngine();
    }

    /**
     * Helfer, falls die Reihenfolge der Validierungen nicht spezifiziert ist
     * (z.B. DuplicateUser vs DuplicateEmail).
     * Kein try/catch im Testkörper nötig.
     */
    private static void assertThrowsAnyOf(Class<? extends Throwable> a,
                                          Class<? extends Throwable> b,
                                          Executable executable) {
        Throwable t = assertThrows(Throwable.class, executable);
        if (!a.isInstance(t) && !b.isInstance(t)) {
            fail("Expected " + a.getSimpleName() + " or " + b.getSimpleName()
                    + " but got " + t.getClass().getSimpleName() + ": " + t.getMessage(), t);
        }
    }

    @Test
    void addUser_sollUserSpeichernUndSizeErhoehen() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
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
        User user1 = new User("Max", "Meier", "max@example.com");
        User user2 = new User("Lisa", "Muster", "max@example.com"); // gleiche Email (aber anderer Name)

        blogEngine.addUser(user1);

        // Act + Assert
        assertThrows(DuplicateEmailException.class, () -> blogEngine.addUser(user2));
    }

    @Test
    void addUser_sollDuplicateUserOderDuplicateEmailWerfenWennGleicherUserNochmalHinzugefuegtWird()
            throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        User user = new User("Max", "Meier", "max@example.com");
        blogEngine.addUser(user);

        // Act + Assert
        assertThrowsAnyOf(DuplicateUserException.class, DuplicateEmailException.class,
                () -> blogEngine.addUser(user));
    }

    @Test
    void removePost_sollAuchMitFremdemIUserObjektAlsAutorLoeschenDuerfen()
            throws DuplicateEmailException, DuplicateUserException, UserNotFoundException,
            PostNotFoundException, IllegalOperationException {

        // Arrange
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