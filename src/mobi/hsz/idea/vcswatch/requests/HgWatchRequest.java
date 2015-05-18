package mobi.hsz.idea.vcswatch.requests;

import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.vcswatch.core.Commit;
import org.jetbrains.annotations.NotNull;
import org.zmlx.hg4idea.HgVcs;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HgWatchRequest extends VcsWatchRequest {

    private static final String TEMPLATE = "{node|short} {author} ## {date} ## {desc}\n";
    private static final Pattern PATTERN = Pattern.compile("^(\\w+) (.*?) ## (\\d+).*? ## (.*)$", Pattern.MULTILINE);

    private final HgVcs root;

    public HgWatchRequest(@NotNull AbstractVcs root, @NotNull VirtualFile workingDirectory) {
        super(root, workingDirectory);
        this.root = (HgVcs) root;
    }

    @NotNull
    @Override
    protected String getExecutable() {
        return this.root.getProjectSettings().getHgExecutable();
    }

    @Override
    public void run() {
        // Check logs. If nothing is returned, there are not commits to pull.
        ProcessOutput output = exec("incoming", "--quiet", "--template", TEMPLATE);
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
