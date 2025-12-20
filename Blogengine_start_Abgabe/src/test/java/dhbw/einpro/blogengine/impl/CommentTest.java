package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    private User author;
    private Comment comment;

    @BeforeEach
    void setUp() throws IllegalOperationException {
        author = new User("Max", "Meier", "max@example.com");
        comment = new Comment("ok", author);
    }

    private static String repeatChar(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    @Test
    @DisplayName("Constructor: >256 Zeichen -> IllegalOperationException")
    void constructor_sollIllegalOperationWerfenWennContentLaengerAls256() {
        // Arrange
        String tooLong = repeatChar('a', 257);

        // Act + Assert
        assertThrows(IllegalOperationException.class, () -> new Comment(tooLong, author));
    }

    @Test
    @DisplayName("setContent: >256 Zeichen -> IllegalOperationException")
    void setContent_sollIllegalOperationWerfenWennContentLaengerAls256() {
        // Arrange
        String tooLong = repeatChar('b', 257);

        // Act + Assert
        assertThrows(IllegalOperationException.class, () -> comment.setContent(tooLong));
    }

    @Test
    @DisplayName("setContent: genau 256 Zeichen erlaubt")
    void setContent_sollContentSetzenWenn256OderWeniger() throws IllegalOperationException {
        // Arrange
        String maxAllowed = repeatChar('c', 256);

        // Act
        comment.setContent(maxAllowed);

        // Assert
        assertEquals(maxAllowed, comment.getContent());
    }

    @Test
    @DisplayName("getAuthor: liefert den Autor zurück")
    void getAuthor_sollAutorZurueckgeben() {
        // Arrange (author aus setUp)

        // Act
        User returned = (User) comment.getAuthor();

        // Assert
        assertEquals(author, returned);
    }
}