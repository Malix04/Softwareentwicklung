package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    private BlogEngine engine;
    private User author;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() throws DuplicateEmailException, DuplicateUserException {
        // Arrange (gemeinsames Setup für alle Tests)
        engine = new BlogEngine();

        author = new User("Max", "Mustermann", "max@mustermann.de");
        user = new User("Erika", "Muster", "erika@muster.de");

        engine.addUser(author);
        engine.addUser(user);

        post = new Post("Titel", "Inhalt", author, engine);
    }

    @Test
    @DisplayName("like: nicht registrierter User -> UserNotFoundException")
    void like_sollUserNotFoundWerfenWennUserNichtRegistriert() {
        // Arrange
        User stranger = new User("Anna", "NichtDa", "anna@nichts.de"); // nicht in engine registriert

        // Act + Assert (bei Exceptions ist assertThrows der 'Act')
        assertThrows(UserNotFoundException.class,
                () -> post.like(stranger),
                "Nicht registrierte User müssen UserNotFoundException auslösen");
    }

    @Test
    @DisplayName("like: Autor liket eigenen Post -> IllegalOperationException")
    void like_sollIllegalOperationWerfenWennAutorEigenenPostLiket() {
        // Arrange (author & post kommen aus setUp)

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> post.like(author),
                "Autor darf eigenen Post nicht liken (IllegalOperationException)");
    }

    @Test
    @DisplayName("like: nach disLike wird DisLike entfernt und Like gesetzt")
    void like_sollDislikeEntfernenWennUserVorherDislikedHat() throws Exception {
        // Arrange
        post.disLike(user); // Precondition: User steht in Dislikes

        // Act
        post.like(user);

        // Assert
        assertEquals(1, post.getLikes().size(), "User sollte genau 1x in Likes stehen");
        assertEquals(0, post.getDisLikes().size(), "Dislike muss entfernt werden, wenn geliked wird");
        assertEquals(10, post.getScore(), "Score sollte (1 - 0) * 10 = 10 sein");
    }

    @Test
    @DisplayName("like: zweiter Like zählt nicht doppelt")
    void like_sollNichtDoppeltZaehlenWennMehrfachGelikedWird() throws Exception {
        // Arrange
        post.like(user); // Precondition: User hat bereits geliked
        assertEquals(1, post.getLikes().size(), "Vorbedingung: User soll 1x geliked haben");

        // Act
        post.like(user); // zweiter Like

        // Assert
        assertEquals(1, post.getLikes().size(), "Doppeltes Like darf nicht doppelt zählen");
        assertEquals(0, post.getDisLikes().size(), "Dislikes sollen dabei unverändert bleiben");
        assertEquals(10, post.getScore(), "Score bleibt (1 - 0) * 10 = 10");
    }
}