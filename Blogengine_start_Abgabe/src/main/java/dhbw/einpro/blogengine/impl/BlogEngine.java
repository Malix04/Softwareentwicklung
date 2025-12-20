package dhbw.einpro.blogengine.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import dhbw.einpro.blogengine.exceptions.DuplicateEmailException;
import dhbw.einpro.blogengine.exceptions.DuplicateUserException;
import dhbw.einpro.blogengine.exceptions.IllegalOperationException;
import dhbw.einpro.blogengine.exceptions.PostNotFoundException;
import dhbw.einpro.blogengine.exceptions.UserNotFoundException;
import dhbw.einpro.blogengine.interfaces.IBlogEngine;
import dhbw.einpro.blogengine.interfaces.IPost;
import dhbw.einpro.blogengine.interfaces.IUser;

public class BlogEngine implements IBlogEngine {

    private final List<IUser> users = new ArrayList<>();
    private final List<IPost> posts = new ArrayList<>();
    private int nextPostId = 1;

    private static boolean sameUser(IUser a, IUser b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return Objects.equals(a.getEmail(), b.getEmail())
                && Objects.equals(a.getFirstName(), b.getFirstName())
                && Objects.equals(a.getLastName(), b.getLastName());
    }

    @Override
    public int size() {
        return users.size();
    }

    @Override
    public boolean addUser(IUser p_user) throws DuplicateEmailException, DuplicateUserException {
        if (p_user == null) return false;

        // DuplicateUser (nach Spezifikation: Email+Vorname+Nachname)
        if (containsUser(p_user)) {
            throw new DuplicateUserException("Benutzer ist bereits registriert.");
        }

        // DuplicateEmail (Email soll eindeutig sein)
        String email = p_user.getEmail();
        if (email != null) {
            for (IUser u : users) {
                if (email.equals(u.getEmail())) {
                    throw new DuplicateEmailException("E-Mail-Adresse ist bereits vergeben.");
                }
            }
        }

        users.add(p_user);
        return true;
    }

    @Override
    public boolean removeUser(IUser p_user) {
        if (p_user == null) return false;

        for (Iterator<IUser> it = users.iterator(); it.hasNext();) {
            if (sameUser(it.next(), p_user)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public int addPost(IPost p_post) throws UserNotFoundException {
        if (p_post == null) return -1;

        IUser author = p_post.getAuthor();
        if (author == null || !containsUser(author)) {
            throw new UserNotFoundException("Autor ist nicht registriert.");
        }

        int id = nextPostId++;
        p_post.setId(id);
        posts.add(p_post);
        return id;
    }

    @Override
    public void removePost(IUser p_author, int p_postId)
            throws PostNotFoundException, IllegalOperationException {

        IPost post = findPostById(p_postId);
        if (post == null) throw new PostNotFoundException("Post nicht gefunden.");

        if (!sameUser(post.getAuthor(), p_author)) {
            throw new IllegalOperationException("Nur der Autor darf den Post löschen.");
        }

        posts.remove(post);
    }

    @Override
    public List<IPost> getPosts() {
        return new ArrayList<>(posts);
    }

    @Override
    public List<IPost> findPostsByAuthor(IUser p_author) {
        List<IPost> result = new ArrayList<>();
        if (p_author == null) return result;

        for (IPost p : posts) {
            if (sameUser(p_author, p.getAuthor())) result.add(p);
        }
        return result;
    }

    @Override
    public IPost findPostById(int p_postId) {
        for (IPost p : posts) {
            if (p.getId() == p_postId) return p;
        }
        return null;
    }

    @Override
    public boolean containsPost(int p_postId) {
        return findPostById(p_postId) != null;
    }

    @Override
    public boolean containsUser(IUser user) {
        if (user == null) return false;

        for (IUser u : users) {
            if (sameUser(u, user)) return true;
        }
        return false;
    }

    @Override
    public IUser findUserByEmail(String p_email) throws UserNotFoundException {
        if (p_email == null) throw new UserNotFoundException("Benutzer nicht gefunden.");

        for (IUser u : users) {
            if (p_email.equals(u.getEmail())) return u;
        }
        throw new UserNotFoundException("Benutzer nicht gefunden.");
    }

    @Override
    public List<IPost> sortPostsByTitle() {
        List<IPost> sorted = new ArrayList<>(posts);
        sorted.sort(Comparator.comparing(IPost::getTitle, Comparator.nullsFirst(String::compareTo)));
        return sorted;
    }

    @Override
    public List<IPost> findPostsByTitle(String title) {
        List<IPost> result = new ArrayList<>();
        if (title == null) return result;

        for (IPost p : posts) {
            if (title.equals(p.getTitle())) result.add(p);
        }
        return result;
    }
}
