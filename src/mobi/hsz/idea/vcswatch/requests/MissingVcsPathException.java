package mobi.hsz.idea.vcswatch.requests;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class MissingVcsPathException extends RuntimeException {

    public MissingVcsPathException(String displayName) {
        super(displayName);
    }

}
