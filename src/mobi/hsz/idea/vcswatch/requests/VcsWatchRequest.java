package mobi.hsz.idea.vcswatch.requests;

import com.intellij.openapi.vcs.VcsRoot;
import org.jetbrains.annotations.NotNull;

public abstract class VcsWatchRequest implements Runnable {

    public VcsWatchRequest(@NotNull final VcsRoot root) {
    }

    @Override
    public void run() {

    }

}
