package mobi.hsz.idea.vcswatch.requests;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.vcswatch.core.Commit;
import mobi.hsz.idea.vcswatch.core.VcsWatchManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public abstract class VcsWatchRequest implements Runnable {

    /** VCS to handle. */
    private final AbstractVcs vcs;

    /** VCS working directory. */
    private final VirtualFile workingDirectory;

    /** Project VCS watch manager. */
    private final VcsWatchManager vcsWatchManager;

    public VcsWatchRequest(@NotNull final AbstractVcs vcs, @NotNull final VirtualFile workingDirectory) {
        this.vcs = vcs;
        this.workingDirectory = workingDirectory;
        this.vcsWatchManager = VcsWatchManager.getInstance(vcs.getProject());
    }

    /**
     * Executes passed command with specified {@link #workingDirectory}.
     *
     * @param command Command to execute
     * @return {@link ProcessOutput} result or <code>null</code> if failed
     */
    @Nullable
    protected ProcessOutput exec(@NotNull String... command) {
        try {
            final ProcessOutput output = ExecUtil.execAndGetOutput(Arrays.asList(command), workingDirectory.getPath());
            if (output.getExitCode() > 0 || output.getStdoutLines().size() == 0) {
                return null;
            }
            return output;
        } catch (ExecutionException ignored) {
            return null;
        }
    }

    /**
     * Adds new {@link Commit} to the {@link VcsWatchManager} registry.
     *
     * @param commit to add
     */
    public void addCommit(@NotNull Commit commit) {
        this.vcsWatchManager.add(commit);
    }

}
