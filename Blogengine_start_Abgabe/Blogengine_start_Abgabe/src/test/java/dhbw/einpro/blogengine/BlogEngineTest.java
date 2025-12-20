package dhbw.einpro.blogengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.interfaces.IBlogEngine;
import dhbw.einpro.blogengine.interfaces.IUser;

class BlogEngineTest {

    private IBlogEngine engine;

    @BeforeEach
    void setUp() {
        engine = BlogEngineHelper.createBlogEngine();
    }

    @Test
    @DisplayName("addUser wirft DuplicateEmailException, wenn Email bereits existiert")
    void addUserDuplicateEmailShouldThrow() throws DuplicateEmailException, DuplicateUserException {
        IUser u1 = BlogEngineHelper.createUser("Valentin", "A", "dup@mail.de");
        IUser u2 = BlogEngineHelper.createUser("Michael", "B", "dup@mail.de");

        engine.addUser(u1);

        assertThrows(DuplicateEmailException.class, () -> engine.addUser(u2));
    }

    @Test
    @DisplayName("addUser wirft DuplicateUserException, wenn gleicher User erneut hinzugefügt wird")
    void addUserDuplicateUserShouldThrow() throws DuplicateEmailException, DuplicateUserException {
        IUser u1 = BlogEngineHelper.createUser("Max", "Mustermann", "max@mail.de");

        engine.addUser(u1);

        assertThrows(DuplicateUserException.class, () -> engine.addUser(u1));
    }
}


