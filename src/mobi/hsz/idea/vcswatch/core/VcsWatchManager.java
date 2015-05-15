package mobi.hsz.idea.vcswatch.core;

import com.intellij.concurrency.JobScheduler;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.vcswatch.requests.VcsWatchRequest;
import mobi.hsz.idea.vcswatch.requests.VcsWatchRequestFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Manager that checks for the changes in the registered VCS repositories.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class VcsWatchManager {

    /** TODO: don't use static delay */
    private static final long DELAY = 10;

    /** Current project. */
    private final Project project;

    /** Project VCS manager. */
    private final ProjectLevelVcsManager vcsManager;

    /** An {@link java.util.concurrent.ExecutorService} that can schedule commands. */
    private final ScheduledExecutorService scheduler;

    /** List of the scheduled futures. */
    private final List<ScheduledFuture<?>> scheduledFutureList = ContainerUtil.newArrayList();

    private VcsWatchManager(@NotNull Project project) {
        this.project = project;
        this.vcsManager = ProjectLevelVcsManager.getInstance(project);
        this.scheduler = JobScheduler.getScheduler();
    }

    public static VcsWatchManager getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, VcsWatchManager.class);
    }

    /**
     * Fetches available project VCS roots and starts listening.
     */
    public void init() {
        stop();

        VcsRoot[] roots = vcsManager.getAllVcsRoots();
        for (VcsRoot root : roots) {
            VcsWatchRequest request = VcsWatchRequestFactory.create(root);
            scheduledFutureList.add(scheduler.schedule(request, DELAY, TimeUnit.SECONDS));
        }
    }

    /**
     * Cancels all elements from {#link #scheduledFutureList} and clears the list.
     */
    public void stop() {
        for (ScheduledFuture<?> scheduledFuture : scheduledFutureList) {
            scheduledFuture.cancel(true);
        }
        scheduledFutureList.clear();
    }

}
