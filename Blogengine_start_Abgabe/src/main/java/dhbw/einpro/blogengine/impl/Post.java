package dhbw.einpro.blogengine.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IBlogEngine;
import dhbw.einpro.blogengine.interfaces.IComment;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;

/**
 * Die Klasse implementiert einen Post im Blog-System.
 */
public class Post implements IPost {

    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String content;
    private IUser author;
    private final IBlogEngine blogEngine;

    private final List<IComment> comments = new ArrayList<>();
    private final Set<IUser> likes = new HashSet<>();
    private final Set<IUser> dislikes = new HashSet<>();

    // Pflicht-Konstruktor (wird vom BlogEngineHelper verwendet)
    public Post(String titel, String content, IUser user, IBlogEngine blogEngine) {
        this.title = titel;
        this.content = content;
        this.author = user;
        this.blogEngine = blogEngine;
    }

    // ===== Basisdaten =====

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int p_id) {
        this.id = p_id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String p_title) {
        this.title = p_title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String p_content) {
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

    // ===== Kommentare =====

    @Override
    public List<IComment> getComments() {
        return new ArrayList<>(comments);
    }

    @Override
    public void addComment(IComment p_comment) throws IllegalOperationException, UserNotFoundException {
        if (p_comment == null) {
            return;
        }

        // Autor des Kommentars darf nicht Autor des Posts sein
        IUser commentAuthor = p_comment.getAuthor();
        if (commentAuthor != null && author != null && author.equals(commentAuthor)) {
            throw new IllegalOperationException("Autor des Kommentars darf nicht gleichzeitig Autor des Posts sein.");
        }

        // Kommentarinhalt darf max 256 Zeichen haben (gemäß IComment-Javadoc / IPost-Javadoc)
        String commentContent = p_comment.getContent();
        if (commentContent != null && commentContent.length() > 256) {
            throw new IllegalOperationException("Kommentar darf maximal 256 Zeichen umfassen.");
        }

        // Kommentarautor muss im Blog-System registriert sein
        if (blogEngine == null || commentAuthor == null || !blogEngine.containsUser(commentAuthor)) {
            throw new UserNotFoundException("Autor des Kommentars ist nicht im Blog-System registriert.");
        }

        // Verknüpfen und speichern
        p_comment.setPost(this);
        comments.add(p_comment);
    }

    @Override
    public void removeComment(IUser p_user, IComment p_comment) throws IllegalOperationException {
        if (p_user == null || p_comment == null) {
            return;
        }

        // Nur der Autor des Kommentars darf löschen
        IUser commentAuthor = p_comment.getAuthor();
        if (commentAuthor == null || !commentAuthor.equals(p_user)) {
            throw new IllegalOperationException("Nur der Autor des Kommentars darf den Kommentar entfernen.");
        }

        comments.remove(p_comment);
    }

    // ===== Likes / Dislikes =====

    @Override
    public void like(IUser p_person) throws IllegalOperationException, UserNotFoundException {
        ensureCanVote(p_person);

        // Wenn zuvor disliked -> entfernen
        dislikes.remove(p_person);

        // In likes aufnehmen (Set verhindert Duplikate)
        likes.add(p_person);
    }

    @Override
    public void disLike(IUser p_person) throws IllegalOperationException, UserNotFoundException {
        ensureCanVote(p_person);

        // Wenn zuvor liked -> entfernen
        likes.remove(p_person);

        // In dislikes aufnehmen
        dislikes.add(p_person);
    }

    private void ensureCanVote(IUser person) throws IllegalOperationException, UserNotFoundException {
        if (person == null) {
            throw new UserNotFoundException("Benutzer ist nicht vorhanden.");
        }

        // Autor darf nicht voten
        if (author != null && author.equals(person)) {
            throw new IllegalOperationException("Autor des Posts darf den eigenen Post nicht bewerten.");
        }

        // User muss registriert sein
        if (blogEngine == null || !blogEngine.containsUser(person)) {
            throw new UserNotFoundException("Benutzer ist nicht im Blog-System registriert.");
        }
    }

    @Override
    public int getScore() {
        return (likes.size() - dislikes.size()) * 10;
    }

    @Override
    public List<IUser> getLikes() {
        return new ArrayList<>(likes);
    }

    @Override
    public List<IUser> getDisLikes() {
        return new ArrayList<>(dislikes);
    }
}
