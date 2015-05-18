package mobi.hsz.idea.vcswatch.requests;

import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.config.GitVcsApplicationSettings;
import mobi.hsz.idea.vcswatch.core.Commit;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitWatchRequest extends VcsWatchRequest {

    private static final String FORMAT = "%h %an ## %ad ## %s";
    private static final Pattern PATTERN = Pattern.compile("^(\\w+) (.*?) ## (\\d+) .*? ## (.*)$", Pattern.MULTILINE);

    /** Path to the GIT executable. */
    private final String bin;

    public GitWatchRequest(@NotNull AbstractVcs vcs, @NotNull VirtualFile workingDirectory) {
        super(vcs, workingDirectory);

        // TODO: handle empty path to git
        this.bin = GitVcsApplicationSettings.getInstance().getPathToGit();
    }

    @Override
    public void run() {
        // Update information about ahead commits. If nothing is returned, repository has no remote.
        if (exec(bin, "remote", "update") == null) {
            return;
        }

        // Check logs. If nothing is returned, there are not commits to pull.
        ProcessOutput output = exec(bin, "log", "..@{u}", "--date=raw", "--pretty=format:" + FORMAT);
        if (output == null) {
            return;
        }

        // Parse logs.
        Matcher matcher = PATTERN.matcher(output.getStdout());
        while (matcher.find()) {
            String id = matcher.group(1);
            String user = matcher.group(2);
            Date date = new Date(Long.valueOf(matcher.group(3)) * 1000);
            String message = matcher.group(4);

            addCommit(new Commit(id, user, date, message));
        }
    }

}
