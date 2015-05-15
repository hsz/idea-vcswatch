package mobi.hsz.idea.vcswatch.requests;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.VcsRoot;
import git4idea.config.GitVcsApplicationSettings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitWatchRequest extends VcsWatchRequest {


    // git remote update -p
    // git log ..@{u}

    public GitWatchRequest(@NotNull VcsRoot root) {
        super(root);
    }

    @Override
    public void run() {
        final String bin = GitVcsApplicationSettings.getInstance().getPathToGit();
        if (StringUtil.isNotEmpty(bin)) {
            Process pr = null;
            try {
                pr = Runtime.getRuntime().exec(bin + " git remote update");
                pr.waitFor();
                pr = Runtime.getRuntime().exec(bin + " git remote update");
                pr.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String data = reader.readLine();

                System.out.println("xx");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
