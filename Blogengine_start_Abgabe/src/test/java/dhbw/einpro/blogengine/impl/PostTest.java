package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void like_sollUserNotFoundWerfenWennUserNichtRegistriert() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine engine = new BlogEngine();
        User author = new User("Max", "Mustermann", "max@mustermann.de");
        engine.addUser(author);

        Post post = new Post("Titel", "Inhalt", author, engine);

        User stranger = new User("Anna", "NichtDa", "anna@nichts.de"); // NICHT registriert

        // Act + Assert
        assertThrows(UserNotFoundException.class,
                () -> post.like(stranger),
                "Nicht registrierte User müssen UserNotFoundException auslösen");
    }

    @Test
    void like_sollIllegalOperationWerfenWennAutorSeinenEigenenPostLiket() throws DuplicateEmailException, DuplicateUserException {
        // Arrange
        BlogEngine engine = new BlogEngine();
        User author = new User("Max", "Mustermann", "max@mustermann.de");
        engine.addUser(author);

        Post post = new Post("Titel", "Inhalt", author, engine);

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> post.like(author),
                "Autor darf eigenen Post nicht liken (IllegalOperationException)");
    }

    @Test
    void like_sollDislikeEntfernenUndScoreAktualisieren() throws Exception {
        // Arrange
        BlogEngine engine = new BlogEngine();
        User author = new User("Max", "Mustermann", "max@mustermann.de");
        User user = new User("Erika", "Muster", "erika@muster.de");

        engine.addUser(author);
        engine.addUser(user);

        Post post = new Post("Titel", "Inhalt", author, engine);

        // Act
        post.disLike(user);   // erst Dislike
        post.like(user);      // dann Like -> muss Dislike entfernen und Like setzen

        // Assert
        assertEquals(1, post.getLikes().size(), "User sollte genau 1x in Likes stehen");
        assertEquals(0, post.getDisLikes().size(), "Dislike muss entfernt werden, wenn geliked wird");
        assertEquals(10, post.getScore(), "Score sollte (1 - 0) * 10 = 10 sein");

        // optional: Like doppelt darf nicht doppelt zählen
        post.like(user);
        assertEquals(1, post.getLikes().size(), "Doppeltes Like darf nicht doppelt zählen");
    }
}
