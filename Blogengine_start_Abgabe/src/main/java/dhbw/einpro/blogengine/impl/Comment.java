package dhbw.einpro.blogengine.impl;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.interfaces.IComment;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;

/**
 * Klasse implementiert einen Kommentar zu einem Post.
 */
public class Comment implements IComment {

    private static final long serialVersionUID = 1L;

    private String content;
    private IUser author;
    private IPost post; // Referenz auf den Post (gemäß Interface)

    // Pflicht-Konstruktor (wird vom BlogEngineHelper verwendet)
    public Comment(String comment, IUser author) throws IllegalOperationException {
        setContent(comment);
        this.author = author;
    }

    @Override
    public String getContent() {
        return content;
    }

    /**
     * Inhalt darf maximal 256 Zeichen haben (gemäß Interface).
     */
    @Override
    public void setContent(String p_content) throws IllegalOperationException {
        if (p_content != null && p_content.length() > 256) {
            throw new IllegalOperationException("Der Kommentar darf maximal 256 Zeichen umfassen.");
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

    // Optional (nicht im Interface gefordert), aber manchmal hilfreich:
    public IPost getPost() {
        return post;
    }
}
