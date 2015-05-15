package mobi.hsz.idea.vcswatch.requests;

import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.VcsRoot;
import git4idea.GitVcs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zmlx.hg4idea.HgVcs;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class VcsWatchRequestFactory {

    private VcsWatchRequestFactory() {
    }

    @Nullable
    public static VcsWatchRequest create(@NotNull VcsRoot root) {
        AbstractVcs vcs = root.getVcs();
        if (vcs == null) {
            return null;
        }

        if (vcs instanceof GitVcs) {
            return new GitWatchRequest(root);
        } else if (vcs instanceof HgVcs) {
            return new HgWatchRequest(root);
        }

        throw new NotProvidedException(vcs.getName());
    }

}
