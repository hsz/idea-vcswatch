package mobi.hsz.idea.vcswatch.requests;

import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.vcswatch.core.Commit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.svn.SvnApplicationSettings;
import org.jetbrains.idea.svn.SvnVcs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SvnWatchRequest extends VcsWatchRequest {

    private final SvnVcs root;

    public SvnWatchRequest(@NotNull AbstractVcs root, @NotNull VirtualFile workingDirectory) {
        super(root, workingDirectory);
        this.root = (SvnVcs) root;
    }

    @NotNull
    @Override
    protected String getExecutable() {
        return SvnApplicationSettings.getInstance().getCommandLinePath();
    }

    @Override
    public void run() {
        // Check logs. If nothing is returned, there are not commits to pull.
        ProcessOutput output = exec("log", "-r", "BASE:HEAD");
        if (output == null) {
            return;
        }

        List<String> lines = output.getStdoutLines();
        String id = null;
        String user = null;
        Date date = null;
        String message;

        // Parse logs.
        for (int i = 3; i < lines.size(); i++) {
            final String line = lines.get(i);
            int mod = i % 3;

            switch (mod) {

                case 1:
                    List<String> parts = ContainerUtil.newArrayList(line.split("\\s*\\|\\s*"));
                    id = parts.get(0);
                    user = parts.get(1);
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

                    try {
                        date = format.parse(parts.get(2));
                    } catch (ParseException e) {
                        date = null;
                    }
                    break;

                case 2:
                    message = line;
                    assert id != null;
                    assert date != null;
                    addCommit(new Commit(id, user, date, message));
                    break;

            }
        }

    }

}
