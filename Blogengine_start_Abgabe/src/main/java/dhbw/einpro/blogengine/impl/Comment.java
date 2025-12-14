package dhbw.einpro.blogengine.impl;

import java.time.LocalDateTime;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.interfaces.IComment;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;

/**
 * Klasse implementiert einen Kommentar zu einem Post.
 */
public class Comment implements IComment
{
    private static final long serialVersionUID = 1L;

    /** Inhalt des Kommentars */
    private String content;

    /** Autor des Kommentars */
    private IUser author;

    /** Zugehöriger Post (wird über setPost gesetzt) */
    private IPost post;

    /** Zeitpunkt der Erstellung des Kommentars */
    private final LocalDateTime createdAt;

    /**
     * Erzeugt einen neuen Kommentar.
     *
     * @param content Inhalt des Kommentars
     * @param author  Autor des Kommentars
     * @throws IllegalOperationException falls der Inhalt mehr als 256 Zeichen hat
     */
    public Comment(String content, IUser author) throws IllegalOperationException {
        // nutzt direkt die Setter-Logik inkl. Längenprüfung
        setContent(content);
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    // --- IComment-Methoden ---

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

    // Optional: falls du das Erstellungsdatum später brauchst (nicht im Interface)
    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Optional: falls du intern an den Post kommen willst (nicht im Interface)
    IPost getPost() {
        return post;
    }
}
