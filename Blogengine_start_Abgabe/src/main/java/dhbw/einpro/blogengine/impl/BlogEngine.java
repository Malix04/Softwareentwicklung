package dhbw.einpro.blogengine.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.PostNotFoundException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IBlogEngine;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;

/**
 * Klasse implementiert die Funktionalität einer Blog Engine.
 */
public class BlogEngine implements IBlogEngine
{
    /** Alle im Blog registrierten Benutzer. */
    private final TreeSet<IUser> users;

    /** Alle im Blog erstellten Posts. */
    private final List<IPost> posts;

    /** Nächste zu vergebende Post-Id (beginnend bei 1). */
    private int nextPostId = 1;

    /**
     * Erzeugt eine neue, leere BlogEngine.
     */
    public BlogEngine()
    {
        // Benutzer werden nach Nachname, Vorname und Email sortiert
        this.users = new TreeSet<>(
                Comparator.comparing(IUser::getLastName, Comparator.nullsFirst(String::compareTo))
                        .thenComparing(IUser::getFirstName, Comparator.nullsFirst(String::compareTo))
                        .thenComparing(IUser::getEmail, Comparator.nullsFirst(String::compareTo)));
        this.posts = new ArrayList<>();
    }

    /**
     * Liefert die Anzahl der Benutzer im Blog-System.
     */
    @Override
    public int size()
    {
        return users.size();
    }

    /**
     * Fügt einen neuen Benutzer zum Blog-System hinzu.
     */
    @Override
    public boolean addUser(IUser p_user) throws DuplicateEmailException, DuplicateUserException
    {
        if (p_user == null)
        {
            return false;
        }

        // Prüfen, ob der User bereits existiert (Gleichheit nach equals)
        if (containsUser(p_user))
        {
            throw new DuplicateUserException("Benutzer ist bereits im Blog-System registriert.");
        }

        // Prüfen, ob die E-Mail-Adresse bereits vergeben ist
        for (IUser existing : users)
        {
            if (existing.getEmail() != null && existing.getEmail().equals(p_user.getEmail()))
            {
                throw new DuplicateEmailException("E-Mail-Adresse ist bereits vergeben.");
            }
        }

        users.add(p_user);
        return true;
    }

    /**
     * Entfernt einen Benutzer aus dem Blog-System.
     */
    @Override
    public boolean removeUser(IUser p_user)
    {
        if (p_user == null)
        {
            return false;
        }
        return users.remove(p_user);
    }

    /**
     * Fügt einen Post zum Blog-System hinzu und nummeriert diesen.
     */
    @Override
    public int addPost(IPost p_post) throws UserNotFoundException
    {
        if (p_post == null)
        {
            return -1;
        }

        IUser author = p_post.getAuthor();
        if (author == null || !containsUser(author))
        {
            throw new UserNotFoundException("Autor des Posts ist nicht im Blog-System registriert.");
        }

        int id = nextPostId++;
        p_post.setId(id);
        posts.add(p_post);
        return id;
    }

    /**
     * Entfernt einen Post aus dem Blog-System.
     */
    @Override
    public void removePost(IUser p_author, int p_postId)
            throws PostNotFoundException, IllegalOperationException
    {
        IPost post = findPostById(p_postId);
        if (post == null)
        {
            throw new PostNotFoundException("Post mit Id " + p_postId + " wurde nicht gefunden.");
        }

        // Nur der tatsächliche Autor darf den Post löschen
        if (p_author == null || post.getAuthor() == null || !post.getAuthor().equals(p_author))
        {
            throw new IllegalOperationException("Nur der Autor des Posts darf diesen löschen.");
        }

        posts.remove(post);
    }

    /**
     * Liefert eine Liste aller Posts, die im Blog-System erstellt wurden.
     */
    @Override
    public List<IPost> getPosts()
    {
        // defensive Kopie zurückgeben
        return new ArrayList<>(posts);
    }

    /**
     * Liefert alle Posts eines bestimmten Autors.
     */
    @Override
    public List<IPost> findPostsByAuthor(IUser p_author)
    {
        List<IPost> result = new ArrayList<>();
        if (p_author == null)
        {
            return result;
        }

        for (IPost post : posts)
        {
            if (p_author.equals(post.getAuthor()))
            {
                result.add(post);
            }
        }
        return result;
    }

    /**
     * Liefert den Post mit einer bestimmten Id oder null, falls nicht vorhanden.
     */
    @Override
    public IPost findPostById(int p_postId)
    {
        for (IPost post : posts)
        {
            if (post.getId() == p_postId)
            {
                return post;
            }
        }
        return null;
    }

    /**
     * Prüft, ob ein Post mit der angegebenen Id existiert.
     */
    @Override
    public boolean containsPost(int p_postId)
    {
        return findPostById(p_postId) != null;
    }

    /**
     * Prüft, ob ein bestimmter Benutzer im Blog-System existiert.
     */
    @Override
    public boolean containsUser(IUser user)
    {
        if (user == null)
        {
            return false;
        }
        // TreeSet.contains nutzt den Comparator, der auf den relevanten Feldern basiert
        return users.contains(user);
    }

    /**
     * Liefert den Benutzer mit der angegebenen E-Mail-Adresse.
     */
    @Override
    public IUser findUserByEmail(String p_email) throws UserNotFoundException
    {
        for (IUser user : users)
        {
            if (user.getEmail() != null && user.getEmail().equals(p_email))
            {
                return user;
            }
        }
        throw new UserNotFoundException("Kein Benutzer mit der Email-Adresse " + p_email + " gefunden.");
    }

    /**
     * Sortiert die Posts anhand ihres Titels.
     */
    @Override
    public List<IPost> sortPostsByTitle()
    {
        List<IPost> sorted = new ArrayList<>(posts);
        sorted.sort(Comparator.comparing(IPost::getTitle, Comparator.nullsFirst(String::compareTo)));
        return sorted;
    }

    /**
     * Liefert alle Posts mit einem gegebenen Titel.
     */
    @Override
    public List<IPost> findPostsByTitle(String title)
    {
        List<IPost> result = new ArrayList<>();
        for (IPost post : posts)
        {
            String postTitle = post.getTitle();
            if (postTitle != null && postTitle.equals(title))
            {
                result.add(post);
            }
        }
        return result;
    }
}
