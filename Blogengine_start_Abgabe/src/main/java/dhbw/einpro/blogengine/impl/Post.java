package dhbw.einpro.blogengine.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IBlogEngine;
import dhbw.einpro.blogengine.interfaces.IComment;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;

/**
 * Die Klasse implementiert einen Post im Blog-System.
 */
public class Post implements IPost
{
    private static final long serialVersionUID = 1L;

    /** Id des Posts (wird von der BlogEngine gesetzt). */
    private int id;

    /** Titel des Posts. */
    private String title;

    /** Inhalt des Posts. */
    private String content;

    /** Autor des Posts. */
    private IUser author;

    /** Zugehörige Blog-Engine, u.a. für Benutzerprüfungen. */
    private final IBlogEngine blogEngine;

    /** Zeitpunkt, zu dem der Post erstellt wurde. (nicht im Interface, aber praktisch) */
    private final LocalDateTime createdAt;

    /** Kommentare zu diesem Post. */
    private final List<IComment> comments;

    /** Benutzer, die den Post gut finden. */
    private final List<IUser> likes;

    /** Benutzer, die den Post nicht gut finden. */
    private final List<IUser> disLikes;

    /**
     * Erzeugt einen neuen Post.
     *
     * @param title      Titel des Posts
     * @param content    Inhalt des Posts
     * @param author     Autor des Posts
     * @param blogEngine Blog-Engine, in welcher der Post geführt wird
     */
    public Post(String title, String content, IUser author, IBlogEngine blogEngine)
    {
        this.title = title;
        this.content = content;
        this.author = author;
        this.blogEngine = blogEngine;
        this.createdAt = LocalDateTime.now();
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.disLikes = new ArrayList<>();
    }

    // --------------------------------------------------------
    // IUser-Vergleich nach Spezifikation (Email + Vorname + Nachname)
    // --------------------------------------------------------

    private static boolean sameUser(IUser a, IUser b)
    {
        if (a == b)
        {
            return true;
        }
        if (a == null || b == null)
        {
            return false;
        }
        return Objects.equals(a.getEmail(), b.getEmail())
                && Objects.equals(a.getFirstName(), b.getFirstName())
                && Objects.equals(a.getLastName(), b.getLastName());
    }

    private static boolean listContainsUser(List<IUser> list, IUser u)
    {
        for (IUser x : list)
        {
            if (sameUser(x, u))
            {
                return true;
            }
        }
        return false;
    }

    private static void listRemoveUser(List<IUser> list, IUser u)
    {
        for (Iterator<IUser> it = list.iterator(); it.hasNext();)
        {
            IUser x = it.next();
            if (sameUser(x, u))
            {
                it.remove();
            }
        }
    }

    // --------------------------------------------------------
    // Implementierung von IPost
    // --------------------------------------------------------

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public void setId(int p_id)
    {
        this.id = p_id;
    }

    @Override
    public List<IComment> getComments()
    {
        // Kommentare in Einfüge-Reihenfolge zurückgeben (wie bisher)
        return comments;
    }

    @Override
    public void addComment(IComment p_comment) throws IllegalOperationException, UserNotFoundException
    {
        if (p_comment == null)
        {
            throw new IllegalOperationException("Kommentar darf nicht null sein.");
        }

        IUser commentAuthor = p_comment.getAuthor();

        // 1) Autor-Prüfung: Kommentar-Autor darf nicht der Post-Autor sein (nach Spezifikation!)
        if (author != null && sameUser(author, commentAuthor))
        {
            throw new IllegalOperationException("Der Autor des Kommentars darf nicht gleichzeitig Autor des Posts sein.");
        }

        // 2) Inhalt-Längenprüfung: Inhalt darf maximal 256 Zeichen umfassen.
        String commentContent = p_comment.getContent();
        if (commentContent != null && commentContent.length() > 256)
        {
            throw new IllegalOperationException("Der Kommentar darf maximal 256 Zeichen umfassen.");
        }

        // 3) Prüfen, ob der Autor des Kommentars in der Blog-Engine registriert ist.
        ensureUserRegistered(commentAuthor);

        // 4) Kommentar dem Post zuordnen und intern speichern.
        p_comment.setPost(this);
        comments.add(p_comment);
    }

    @Override
    public void removeComment(IUser p_user, IComment p_comment) throws IllegalOperationException
    {
        if (p_user == null || p_comment == null)
        {
            throw new IllegalOperationException("Benutzer und Kommentar dürfen nicht null sein.");
        }

        // Nur der Autor des Kommentars darf diesen entfernen (nach Spezifikation!)
        IUser commentAuthor = p_comment.getAuthor();
        if (!sameUser(p_user, commentAuthor))
        {
            throw new IllegalOperationException("Nur der Autor des Kommentars darf diesen entfernen.");
        }

        comments.remove(p_comment);
    }

    @Override
    public void like(IUser p_person) throws IllegalOperationException, UserNotFoundException
    {
        validateReactionUser(p_person);

        // Robust: ggf. vorhandenen Dislike entfernen (auch wenn anderes IUser-Objekt)
        listRemoveUser(disLikes, p_person);

        // Robust: nur einmal liken (nach Spezifikation)
        if (!listContainsUser(likes, p_person))
        {
            likes.add(p_person);
        }
    }

    @Override
    public void disLike(IUser p_person) throws IllegalOperationException, UserNotFoundException
    {
        validateReactionUser(p_person);

        // Robust: ggf. vorhandenen Like entfernen (auch wenn anderes IUser-Objekt)
        listRemoveUser(likes, p_person);

        // Robust: nur einmal disliken (nach Spezifikation)
        if (!listContainsUser(disLikes, p_person))
        {
            disLikes.add(p_person);
        }
    }

    @Override
    public int getScore()
    {
        // Score = (Anzahl Likes - Anzahl Dislikes) * 10
        return (likes.size() - disLikes.size()) * 10;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public void setTitle(String p_title)
    {
        this.title = p_title;
    }

    @Override
    public String getContent()
    {
        return content;
    }

    @Override
    public void setContent(String p_content)
    {
        this.content = p_content;
    }

    @Override
    public IUser getAuthor()
    {
        return author;
    }

    @Override
    public void setAuthor(IUser p_author)
    {
        this.author = p_author;
    }

    @Override
    public List<IUser> getLikes()
    {
        return likes;
    }

    @Override
    public List<IUser> getDisLikes()
    {
        return disLikes;
    }

    // --------------------------------------------------------
    // Hilfsmethoden
    // --------------------------------------------------------

    /**
     * Prüft, ob der angegebene Benutzer für Likes/Dislikes zulässig ist.
     *
     * @param user Benutzer, der reagiert
     * @throws IllegalOperationException falls der Benutzer zugleich Autor des Posts ist
     * @throws UserNotFoundException     falls der Benutzer nicht in der Blog-Engine registriert ist
     */
    private void validateReactionUser(IUser user) throws IllegalOperationException, UserNotFoundException
    {
        if (user == null)
        {
            throw new IllegalOperationException("Benutzer darf nicht null sein.");
        }

        // Autor des Posts darf den eigenen Post nicht bewerten (nach Spezifikation!)
        if (author != null && sameUser(author, user))
        {
            throw new IllegalOperationException("Der Autor des Posts darf den eigenen Post nicht bewerten.");
        }

        // Benutzer muss in der BlogEngine registriert sein.
        ensureUserRegistered(user);
    }

    /**
     * Stellt sicher, dass der Benutzer in der zugehörigen BlogEngine registriert ist.
     *
     * @param user zu prüfender Benutzer
     * @throws UserNotFoundException falls der Benutzer nicht registriert ist oder keine BlogEngine vorhanden ist
     */
    private void ensureUserRegistered(IUser user) throws UserNotFoundException
    {
        if (user == null || blogEngine == null || !blogEngine.containsUser(user))
        {
            throw new UserNotFoundException("Benutzer ist nicht im Blog-System registriert.");
        }
    }
}