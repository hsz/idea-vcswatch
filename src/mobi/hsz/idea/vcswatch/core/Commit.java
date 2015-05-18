package mobi.hsz.idea.vcswatch.core;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class Commit {

    private final String id;
    private final String user;
    private final Date dateTime;
    private final String message;

    public Commit(@NotNull String id, @NotNull String user, @NotNull Date date, @NotNull String message) {
        this.id = id;
        this.user = user;
        this.dateTime = date;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public Date getDate() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }

}
