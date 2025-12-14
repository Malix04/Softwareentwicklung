package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void constructor_sollIllegalOperationWerfenWennContentLaengerAls256() {
        // Arrange
        User author = new User("Max", "Meier", "max@example.com");

        // 257 Zeichen erzeugen
        String tooLong = "a".repeat(257);

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> new Comment(tooLong, author),
                "Kommentar mit >256 Zeichen muss IllegalOperationException auslösen");
    }

    @Test
    void setContent_sollIllegalOperationWerfenWennContentLaengerAls256() throws IllegalOperationException {
        // Arrange
        User author = new User("Max", "Meier", "max@example.com");
        Comment comment = new Comment("ok", author);

        String tooLong = "b".repeat(257);

        // Act + Assert
        assertThrows(IllegalOperationException.class,
                () -> comment.setContent(tooLong),
                "setContent mit >256 Zeichen muss IllegalOperationException auslösen");
    }

    @Test
    void setContent_sollContentSetzenWenn256OderWeniger() throws IllegalOperationException {
        // Arrange
        User author = new User("Max", "Meier", "max@example.com");
        Comment comment = new Comment("start", author);

        String maxAllowed = "c".repeat(256);

        // Act
        comment.setContent(maxAllowed);

        // Assert
        assertEquals(maxAllowed, comment.getContent(), "Content mit genau 256 Zeichen muss gesetzt werden können");
    }
}