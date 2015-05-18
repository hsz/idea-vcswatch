package mobi.hsz.idea.vcswatch.requests;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class NotImplementedException extends RuntimeException {

    public NotImplementedException(String displayName) {
        super(displayName);
    }

}
