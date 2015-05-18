package mobi.hsz.idea.vcswatch.requests;

import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class HgWatchRequest extends VcsWatchRequest {

    public HgWatchRequest(@NotNull AbstractVcs root, @NotNull VirtualFile workingDirectory) {
        super(root, workingDirectory);
    }

    @Override
    public void run() {
    }

}
