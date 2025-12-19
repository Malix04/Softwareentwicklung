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
    private User author;      // Autor des Posts (registriert)
    private User user;        // normaler User (registriert)
    private Post post;        // Test-Post

    @BeforeEach
    void setUp() throws DuplicateEmailException, DuplicateUserException {
        // Arrange (gemeinsames Setup; jeder Test startet frisch)
        engine = new BlogEngine();

        author = new User("Max", "Mustermann", "max@mustermann.de");
        user = new User("Erika", "Muster", "erika@muster.de");

        engine.addUser(author);
        engine.addUser(user);

        post = new Post("Titel", "Inhalt", author, engine);
    }

    // --- Helper: Java-8/11 kompatibles "repeat" ---
    private static String repeatChar(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    // --- Stub-Comment, damit wir >256 Zeichen testen können,
    //     ohne dass der echte Comment-Konstruktor vorher schon abbricht.
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
            // absichtlich KEINE 256-Prüfung (damit Post.addComment die Regel testen kann)
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

    @Test
    @DisplayName("addComment: nicht registrierter Autor -> UserNotFoundException")
    void addComment_sollUserNotFoundWerfenWennAutorNichtRegistriert() throws IllegalOperationException {
        // Arrange
        User stranger = new User("Anna", "NichtDa", "anna@nichts.de"); // NICHT registriert
        Comment comment = new Comment("ok", stranger);

        // Act + Assert
        assertThrows(UserNotFoundException.class,
                () -> post.addComment(comment),
                "Nicht registrierter Kommentar-Autor muss UserNotFoundException auslösen");
    }

    @Test
    @DisplayName("addComment: Kommentarautor = Postautor -> IllegalOperationException")
    void addComment_sollIllegalOperationWerfenWennKommentarAutorGleichPostAutor() throws Exception {
        // Arrange
        Comment comment = new Comment("ok", author); // author ist registriert

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> post.addComment(comment),
                "Kommentarautor darf nicht gleichzeitig Postautor sein");
    }

    @Test
    @DisplayName("addComment: Kommentarinhalt > 256 Zeichen -> IllegalOperationException")
    void addComment_sollIllegalOperationWerfenWennKommentarZuLangIst() {
        // Arrange
        String tooLong = repeatChar('x', 257);
        IComment comment = new StubComment(tooLong, user); // user ist registriert

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> post.addComment(comment),
                "Kommentarinhalt > 256 Zeichen muss IllegalOperationException auslösen");
    }

    @Test
    @DisplayName("removeComment: Nur der Kommentarautor darf löschen -> sonst IllegalOperationException")
    void removeComment_sollIllegalOperationWerfenWennNichtAutorLoescht() throws Exception {
        // Arrange
        Comment comment = new Comment("ok", user);
        post.addComment(comment);

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> post.removeComment(author, comment),
                "Nur der Autor des Kommentars darf entfernen");
    }

    @Test
    @DisplayName("like: entfernt disLike und setzt like (Score korrekt)")
    void like_sollDislikeEntfernenUndScoreAktualisieren() throws Exception {
        // Arrange (Precondition)
        post.disLike(user);

        // Act
        post.like(user);

        // Assert
        assertEquals(1, post.getLikes().size(), "User soll genau 1x in Likes stehen");
        assertEquals(0, post.getDisLikes().size(), "Dislikes sollen entfernt worden sein");
        assertEquals(10, post.getScore(), "Score muss (1 - 0) * 10 = 10 sein");
    }

    @Test
    @DisplayName("ROBUST: addComment mit mehreren Fehlerbedingungen -> IllegalOperation ODER UserNotFound")
    void addComment_robust_sollIllegalOperationOderUserNotFoundWerfenWennMehrereRegelnVerletzt() {
        // Arrange (hier werden absichtlich 2 Regeln verletzt)
        String tooLong = repeatChar('y', 257);
        User stranger = new User("Tim", "Unregistered", "tim@none.de"); // nicht registriert
        IComment comment = new StubComment(tooLong, stranger);

        // Act + Assert (robust gegen Prüf-Reihenfolge)
        try {
            post.addComment(comment);
            fail("Es hätte IllegalOperationException oder UserNotFoundException geworfen werden müssen.");
        } catch (IllegalOperationException | UserNotFoundException expected) {
            // ok: Reihenfolge der Checks ist nicht spezifiziert -> beides zulässig
        }
    }
}
