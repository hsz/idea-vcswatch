package mobi.hsz.idea.vcswatch.requests;

import com.intellij.openapi.vcs.VcsRoot;
import org.jetbrains.annotations.NotNull;

public class HgWatchRequest extends VcsWatchRequest {

    public HgWatchRequest(@NotNull VcsRoot root) {
        super(root);
    }

}
