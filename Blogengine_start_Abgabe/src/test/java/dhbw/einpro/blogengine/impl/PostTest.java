package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IComment;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;
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
        engine = new BlogEngine();

        author = new User("Max", "Mustermann", "max@mustermann.de");
        user = new User("Erika", "Muster", "erika@muster.de");

        engine.addUser(author);
        engine.addUser(user);

        post = new Post("Titel", "Inhalt", author, engine);
    }

    private static String repeatChar(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }


    private static final class StubComment implements IComment {
        private static final long serialVersionUID = 1L;

        private String content;
        private IUser author;
        @SuppressWarnings("unused")
        private IPost post;

        private StubComment(String content, IUser author) {
            this.content = content;
            this.author = author;
        }

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public void setContent(String p_content) {
            // absichtlich keine 256-Prüfung -> soll Post.addComment testen
            this.content = p_content;
        }

        @Override
        public IUser getAuthor() {
            return author;
        }

        @Override
        public void setAuthor(IUser p_author) {
            this.author = p_author;
        }

        @Override
        public void setPost(IPost p_post) {
            this.post = p_post;
        }
    }


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
    @DisplayName("addComment: nicht registrierter Autor -> UserNotFoundException")
    void addComment_sollUserNotFoundWerfenWennAutorNichtRegistriert() throws IllegalOperationException {
        // Arrange
        User stranger = new User("Anna", "NichtDa", "anna@nichts.de"); // nicht registriert
        Comment comment = new Comment("ok", stranger);

        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> post.addComment(comment));
    }

    @Test
    @DisplayName("addComment: Kommentarautor = Postautor -> IllegalOperationException")
    void addComment_sollIllegalOperationWerfenWennKommentarAutorGleichPostAutor() throws Exception {
        // Arrange
        Comment comment = new Comment("ok", author);

        // Act + Assert
        assertThrows(IllegalOperationException.class, () -> post.addComment(comment));
    }

    @Test
    @DisplayName("addComment: Kommentarinhalt > 256 Zeichen -> IllegalOperationException")
    void addComment_sollIllegalOperationWerfenWennKommentarZuLangIst() {
        // Arrange
        String tooLong = repeatChar('x', 257);
        IComment comment = new StubComment(tooLong, user);

        // Act + Assert
        assertThrows(IllegalOperationException.class, () -> post.addComment(comment));
    }

    @Test
    @DisplayName("removeComment: Nur der Kommentarautor darf löschen -> sonst IllegalOperationException")
    void removeComment_sollIllegalOperationWerfenWennNichtAutorLoescht() throws Exception {
        // Arrange
        Comment comment = new Comment("ok", user);
        post.addComment(comment);

        // Act + Assert
        assertThrows(IllegalOperationException.class, () -> post.removeComment(author, comment));
    }

    @Test
    @DisplayName("like: entfernt disLike und setzt like (Score korrekt)")
    void like_sollDislikeEntfernenUndScoreAktualisieren() throws Exception {
        // Arrange
        post.disLike(user);

        // Act
        post.like(user);

        // Assert
        assertEquals(1, post.getLikes().size());
        assertEquals(0, post.getDisLikes().size());
        assertEquals(10, post.getScore());
    }



    @Test
    @DisplayName("ROBUST: like mit FakeUser (gleiche Daten) -> kein Duplikat")
    void like_robust_sollNichtDoppeltLikenAuchMitFakeUser() throws Exception {
        // Arrange
        IUser u1 = new FakeUser("Erika", "Muster", "erika@muster.de");
        IUser u2 = new FakeUser("Erika", "Muster", "erika@muster.de");

        // Act
        post.like(u1);
        post.like(u2);

        // Assert
        assertEquals(1, post.getLikes().size());
        assertEquals(0, post.getDisLikes().size());
        assertEquals(10, post.getScore());
    }

    @Test
    @DisplayName("ROBUST: disLike entfernt vorherigen like auch mit FakeUser")
    void disLike_robust_sollLikeEntfernenAuchBeiFakeUser() throws Exception {
        // Arrange
        IUser u1 = new FakeUser("Erika", "Muster", "erika@muster.de");
        IUser u2 = new FakeUser("Erika", "Muster", "erika@muster.de");

        // Act
        post.like(u1);
        post.disLike(u2);

        // Assert
        assertEquals(0, post.getLikes().size());
        assertEquals(1, post.getDisLikes().size());
        assertEquals(-10, post.getScore());
    }

    @Test
    @DisplayName("ROBUST: Kommentarautor (FakeUser) == Postautor (gleiche Daten) -> IllegalOperationException")
    void addComment_robust_sollWerfenWennKommentarAutorSpezifikationsgleichPostAutor() {
        // Arrange
        IUser sameAsAuthorButNotUser = new FakeUser("Max", "Mustermann", "max@mustermann.de");
        IComment comment = new StubComment("ok", sameAsAuthorButNotUser);

        // Act + Assert
        assertThrows(IllegalOperationException.class, () -> post.addComment(comment));
    }

    @Test
    @DisplayName("ROBUST: Kommentar löschen mit FakeUser, der dem echten Autor entspricht")
    void removeComment_robust_sollMitFakeUserAlsAutorLoeschenDuerfen() throws Exception {
        // Arrange
        Comment comment = new Comment("ok", user);
        post.addComment(comment);

        IUser sameAsUserButNotUser = new FakeUser("Erika", "Muster", "erika@muster.de");

        // Act
        post.removeComment(sameAsUserButNotUser, comment);

        // Assert
        assertEquals(0, post.getComments().size());
    }
}