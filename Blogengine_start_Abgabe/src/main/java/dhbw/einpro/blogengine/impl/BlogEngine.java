package dhbw.einpro.blogengine.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class BlogEngine implements IBlogEngine {

    private final List<IUser> users = new ArrayList<>();
    private final Map<String, IUser> usersByEmail = new HashMap<>();

    private final List<IPost> posts = new ArrayList<>();
    private final Map<Integer, IPost> postsById = new HashMap<>();

    private int nextPostId = 1;

    // ===== User-Verwaltung =====

    @Override
    public int size() {
        return users.size();
    }

    @Override
    public boolean addUser(IUser p_user) throws DuplicateEmailException, DuplicateUserException {
        if (p_user == null) {
            return false;
        }

        // 1) DuplicateEmail prüfen (nur Email zählt)
        String emailKey = normalizeEmail(p_user.getEmail());
        if (emailKey != null && usersByEmail.containsKey(emailKey)) {
            throw new DuplicateEmailException("Ein User mit dieser Email existiert bereits: " + p_user.getEmail());
        }

        // 2) DuplicateUser prüfen (equals entscheidet)
        if (users.contains(p_user)) {
            throw new DuplicateUserException("Der gleiche User wurde erneut hinzugefügt.");
        }

        users.add(p_user);
        if (emailKey != null) {
            usersByEmail.put(emailKey, p_user);
        }
        return true;
    }

    @Override
    public boolean removeUser(IUser p_user) {
        if (p_user == null) {
            return false;
        }

        boolean removed = users.remove(p_user);
        // Map bereinigen (falls Email vorhanden)
        String emailKey = normalizeEmail(p_user.getEmail());
        if (emailKey != null) {
            IUser mapped = usersByEmail.get(emailKey);
            // Nur entfernen, wenn es wirklich derselbe Eintrag ist
            if (mapped != null && Objects.equals(mapped, p_user)) {
                usersByEmail.remove(emailKey);
            } else if (mapped != null && removed) {
                // Fallback: wenn entfernt wurde, aber equals anders war, Map neu aufbauen
                rebuildUsersByEmail();
            }
        } else if (removed) {
            rebuildUsersByEmail();
        }

        return removed;
    }

    @Override
    public boolean containsUser(IUser user) {
        if (user == null) return false;
        return users.contains(user);
    }

    @Override
    public IUser findUserByEmail(String p_email) throws UserNotFoundException {
        String key = normalizeEmail(p_email);
        IUser user = (key == null) ? null : usersByEmail.get(key);
        if (user == null) {
            throw new UserNotFoundException("User mit Email nicht gefunden: " + p_email);
        }
        return user;
    }

    private void rebuildUsersByEmail() {
        usersByEmail.clear();
        for (IUser u : users) {
            String key = normalizeEmail(u.getEmail());
            if (key != null && !usersByEmail.containsKey(key)) {
                usersByEmail.put(key, u);
            }
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        String trimmed = email.trim().toLowerCase();
        return trimmed.isEmpty() ? null : trimmed;
    }

    // ===== Post-Verwaltung =====

    @Override
    public int addPost(IPost p_post) throws UserNotFoundException {
        if (p_post == null) {
            return -1;
        }

        IUser author = p_post.getAuthor();
        if (author == null || !containsUser(author)) {
            throw new UserNotFoundException("Autor ist nicht im Blog-System registriert.");
        }

        int id = nextPostId++;
        p_post.setId(id);

        posts.add(p_post);
        postsById.put(id, p_post);

        return id;
    }

    @Override
    public void removePost(IUser p_author, int p_postId) throws PostNotFoundException, IllegalOperationException {
        IPost post = postsById.get(p_postId);
        if (post == null) {
            throw new PostNotFoundException("Post nicht gefunden: " + p_postId);
        }

        if (p_author == null || post.getAuthor() == null || !post.getAuthor().equals(p_author)) {
            throw new IllegalOperationException("Nur der Autor des Posts darf den Post entfernen.");
        }

        posts.remove(post);
        postsById.remove(p_postId);
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
            if (p_author.equals(p.getAuthor())) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public IPost findPostById(int p_postId) {
        // Interface-Signatur hat keine Exception → üblich: null wenn nicht gefunden
        return postsById.get(p_postId);
    }

    @Override
    public boolean containsPost(int p_postId) {
        return postsById.containsKey(p_postId);
    }

    @Override
    public List<IPost> sortPostsByTitle() {
        List<IPost> sorted = new ArrayList<>(posts);
        sorted.sort(Comparator.comparing(
                p -> p.getTitle() == null ? "" : p.getTitle(),
                String.CASE_INSENSITIVE_ORDER
        ));
        return sorted;
    }

    @Override
    public List<IPost> findPostsByTitle(String title) {
        List<IPost> result = new ArrayList<>();
        if (title == null) return result;

        for (IPost p : posts) {
            if (p.getTitle() != null && p.getTitle().equals(title)) {
                result.add(p);
            }
        }
        return result;
    }
}

