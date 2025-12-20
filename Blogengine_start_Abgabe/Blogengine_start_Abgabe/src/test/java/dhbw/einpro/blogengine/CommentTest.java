package dhbw.einpro.blogengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.impl.Comment;
import dhbw.einpro.blogengine.impl.User;
import dhbw.einpro.blogengine.interfaces.IUser;

class CommentTest {

    private IUser author;

    @BeforeEach
    void setUp() {
        author = new User("Max", "Mustermann", "max@demo.de");
    }

    @Test
    @DisplayName("setContent wirft IllegalOperationException bei mehr als 256 Zeichen")
    void setContentTooLongShouldThrow() throws IllegalOperationException {
        // Arrange
        Comment c = new Comment("ok", author);
        String tooLong = "a".repeat(257);

        // Act + Assert
        assertThrows(IllegalOperationException.class, () -> c.setContent(tooLong));
    }
}
