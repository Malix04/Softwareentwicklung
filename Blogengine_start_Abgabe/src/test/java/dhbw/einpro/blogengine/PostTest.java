package dhbw.einpro.blogengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IBlogEngine;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;

class PostTest {

    private IBlogEngine engine;
    private IUser author;
    private IUser voter;
    private IPost post;

    @BeforeEach
    void setUp() throws DuplicateEmailException, DuplicateUserException, UserNotFoundException {
        // Arrange
        engine = BlogEngineHelper.createBlogEngine();

        author = BlogEngineHelper.createUser("Iyed", "Author", "author@mail.de");
        voter  = BlogEngineHelper.createUser("Mac", "Voter", "voter@mail.de");

        engine.addUser(author);
        engine.addUser(voter);

        post = BlogEngineHelper.createPost("Titel", "Inhalt", author, engine);
        engine.addPost(post);
    }

    @Test
    @DisplayName("Score = (likes - dislikes) * 10 und Wechsel like/disLike funktioniert")
    void scoreAndSwitchingShouldWork() throws IllegalOperationException, UserNotFoundException {
        // Act
        post.like(voter);        // likes=1 dislikes=0 => 10
        int scoreAfterLike = post.getScore();

        post.disLike(voter);     // likes=0 dislikes=1 => -10
        int scoreAfterDislike = post.getScore();

        // Assert
        assertEquals(10, scoreAfterLike);
        assertEquals(-10, scoreAfterDislike);
        assertEquals(0, post.getLikes().size());
        assertEquals(1, post.getDisLikes().size());
    }
}
