package dhbw.einpro.blogengine.impl;

import java.time.LocalDateTime;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.interfaces.IComment;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;


public class Comment implements IComment
{
    private static final long serialVersionUID = 1L;


    private String content;


    private IUser author;


    private IPost post;


    private final LocalDateTime createdAt;


    public Comment(String content, IUser author) throws IllegalOperationException {

        setContent(content);
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }



    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String p_content) throws IllegalOperationException {
        // Nur prüfen, wenn nicht null
        if (p_content != null && p_content.length() > 256) {
            throw new IllegalOperationException(
                    "Der Kommentar darf maximal 256 Zeichen umfassen.");
        }
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


    LocalDateTime getCreatedAt() {
        return createdAt;
    }


    IPost getPost() {
        return post;
    }
}
