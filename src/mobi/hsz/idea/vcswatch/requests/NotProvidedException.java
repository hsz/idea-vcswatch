package mobi.hsz.idea.vcswatch.requests;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class NotProvidedException extends RuntimeException {

    public NotProvidedException(String displayName) {
        super(displayName);
    }

}
