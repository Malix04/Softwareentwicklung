package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("Constructor: >256 Zeichen -> IllegalOperationException")
    void constructor_sollIllegalOperationWerfenWennContentLaengerAls256() {
        // Arrange
        User author = new User("Max", "Meier", "max@example.com");
        String tooLong = "a".repeat(257);

        // Act + Assert (bei Exceptions ist assertThrows der Act)
        assertThrows(IllegalOperationException.class,
                () -> new Comment(tooLong, author));
    }

    @Test
    @DisplayName("setContent: >256 Zeichen -> IllegalOperationException")
    void setContent_sollIllegalOperationWerfenWennContentLaengerAls256() throws IllegalOperationException {
        // Arrange
        User author = new User("Max", "Meier", "max@example.com");
        Comment comment = new Comment("ok", author);
        String tooLong = "b".repeat(257);

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> comment.setContent(tooLong));
    }

    @Test
    @DisplayName("setContent: genau 256 Zeichen erlaubt")
    void setContent_sollContentSetzenWenn256OderWeniger() throws IllegalOperationException {
        // Arrange
        User author = new User("Max", "Meier", "max@example.com");
        Comment comment = new Comment("start", author);
        String maxAllowed = "c".repeat(256);

        // Act
        comment.setContent(maxAllowed);

        // Assert
        assertEquals(maxAllowed, comment.getContent());
    }
}