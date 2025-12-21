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


public class Post implements IPost
{
    private static final long serialVersionUID = 1L;

    private int id;

    private String title;

    private String content;

    private IUser author;

    private final IBlogEngine blogEngine;

    private final LocalDateTime createdAt;

    private final List<IComment> comments;

    private final List<IUser> likes;

    private final List<IUser> disLikes;


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

        if (author != null && sameUser(author, commentAuthor))
        {
            throw new IllegalOperationException("Der Autor des Kommentars darf nicht gleichzeitig Autor des Posts sein.");
        }


        String commentContent = p_comment.getContent();
        if (commentContent != null && commentContent.length() > 256)
        {
            throw new IllegalOperationException("Der Kommentar darf maximal 256 Zeichen umfassen.");
        }

        ensureUserRegistered(commentAuthor);

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

        listRemoveUser(disLikes, p_person);

        if (!listContainsUser(likes, p_person))
        {
            likes.add(p_person);
        }
    }

    @Override
    public void disLike(IUser p_person) throws IllegalOperationException, UserNotFoundException
    {
        validateReactionUser(p_person);

        listRemoveUser(likes, p_person);

        if (!listContainsUser(disLikes, p_person))
        {
            disLikes.add(p_person);
        }
    }

    @Override
    public int getScore()
    {
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


    private void validateReactionUser(IUser user) throws IllegalOperationException, UserNotFoundException
    {
        if (user == null)
        {
            throw new IllegalOperationException("Benutzer darf nicht null sein.");
        }

        if (author != null && sameUser(author, user))
        {
            throw new IllegalOperationException("Der Autor des Posts darf den eigenen Post nicht bewerten.");
        }

        ensureUserRegistered(user);
    }


    private void ensureUserRegistered(IUser user) throws UserNotFoundException
    {
        if (user == null || blogEngine == null || !blogEngine.containsUser(user))
        {
            throw new UserNotFoundException("Benutzer ist nicht im Blog-System registriert.");
        }
    }
}