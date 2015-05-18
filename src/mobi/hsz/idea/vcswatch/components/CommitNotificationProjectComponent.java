package mobi.hsz.idea.vcswatch.components;

import com.intellij.concurrency.JobScheduler;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.playback.commands.ActionCommand;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.openapi.vcs.update.AbstractCommonUpdateAction;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.vcs.log.impl.VcsLogContentProvider;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.ui.VcsLogUiImpl;
import mobi.hsz.idea.vcswatch.VcsWatchBundle;
import mobi.hsz.idea.vcswatch.core.Commit;
import mobi.hsz.idea.vcswatch.core.VcsWatchManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.event.HyperlinkEvent;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * {@link ProjectComponent} that notifies about the new commits.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class CommitNotificationProjectComponent implements ProjectComponent {

    /**
     * Delay required to wait for another commits.
     */
    private static final long DELAY = 1500;

    /**
     * Current project.
     */
    private final Project project;

    /**
     * Project VCS watch manager.
     */
    private final VcsWatchManager vcsWatchManager;

    /**
     * An {@link java.util.concurrent.ExecutorService} that can schedule commands.
     */
    private final ScheduledExecutorService scheduler;

    /**
     * Commits to notify about.
     */
    private final List<Commit> commits = ContainerUtil.newArrayList();

    /**
     * Scheduled feature with notification event.
     */
    private ScheduledFuture<?> scheduledFeature;

    /**
     * Action that shows new {@link Notification} with {@link Commit} messages.
     */
    private final Runnable notify = new Runnable() {
        @Override
        public void run() {
            if (commits.isEmpty()) {
                return;
            }

            Notifications.Bus.notify(new CommitNotification(project, commits), project);
            commits.clear();
        }
    };

    /**
     * Listens for the new {@link Commit} items.
     */
    private final VcsWatchManager.OnCommitListener onCommitListener = new VcsWatchManager.OnCommitListener() {
        @Override
        public void onCommit(@NotNull Commit commit) {
            if (scheduledFeature != null) {
                scheduledFeature.cancel(true);
            }
            commits.add(commit);
            scheduledFeature = scheduler.schedule(notify, DELAY, TimeUnit.MILLISECONDS);
        }
    };

    public CommitNotificationProjectComponent(@NotNull Project project) {
        this.project = project;
        this.vcsWatchManager = VcsWatchManager.getInstance(project);
        this.scheduler = JobScheduler.getScheduler();
    }

    @Override
    public void projectOpened() {
        this.vcsWatchManager.setOnCommitListener(onCommitListener);
    }

    @Override
    public void projectClosed() {
        this.vcsWatchManager.removeOnCommitListener(onCommitListener);
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "VcsWatch.CommitNotification";
    }

    private static class CommitNotification extends Notification {

        private static final String TITLE = VcsWatchBundle.message("notification.title");
        private static final String TITLE_PLURAL = VcsWatchBundle.message("notification.title.plural");

        private final List<Commit> commits;

        private CommitNotification(@NotNull final Project project, @NotNull final List<Commit> commits) {
            super(
                    VcsWatchBundle.PLUGIN_NAME,
                    commits.size() == 1 ? TITLE : TITLE_PLURAL,
                    VcsWatchBundle.PLUGIN_NAME,
                    NotificationType.INFORMATION,
                    createListener(project)
            );

            this.commits = ContainerUtil.newArrayList(commits);
        }

        @NotNull
        @Override
        public String getContent() {
            List<String> messages = ContainerUtil.newArrayList();
            for (Commit commit : commits) {
                String time = new PrettyTime(Locale.ENGLISH).format(commit.getDate());
                messages.add(VcsWatchBundle.message("notification.content", commit.getMessage(), commit.getId(), time, commit.getUser()));
            }

            return VcsWatchBundle.message("notification.update", StringUtil.join(messages, "<br/>"));
        }

        private static NotificationListener.Adapter createListener(@NotNull final Project project) {
            return new NotificationListener.Adapter() {

                private static final String HASH = "HASH:";

                private static final String UPDATE = "UPDATE";

                private static final String UPDATE_ACTION_ID = "Vcs.UpdateProject";

                @Override
                protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
                    if (StringUtil.equals(event.getDescription(), UPDATE)) {
                        update();
                        if (!notification.isExpired()) {
                            notification.expire();
                        }
                    } else if (StringUtil.startsWith(event.getDescription(), HASH)) {
                        jumpToReference(StringUtil.substringAfter(event.getDescription(), HASH));
                    }
                }

                private void update() {
                    final AnAction action = ActionManager.getInstance().getAction(UPDATE_ACTION_ID);
                    assert action instanceof AbstractCommonUpdateAction;
                    final AbstractCommonUpdateAction updateAction = (AbstractCommonUpdateAction) action;
                    ActionManager.getInstance().tryToExecute(
                            updateAction,
                            ActionCommand.getInputEvent(UPDATE_ACTION_ID),
                            null,
                            ActionPlaces.UNKNOWN,
                            true
                    );
                }

                private void jumpToReference(@Nullable final String hash) {
                    final VcsLogManager log = ServiceManager.getService(project, VcsLogManager.class);
                    if (log == null) {
                        return;
                    }

                    final ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
                    final ToolWindow window = windowManager.getToolWindow(ChangesViewContentManager.TOOLWINDOW_ID);
                    ContentManager cm = window.getContentManager();
                    Content[] contents = cm.getContents();
                    for (Content content : contents) {
                        if (VcsLogContentProvider.TAB_NAME.equals(content.getDisplayName())) {
                            cm.setSelectedContent(content);
                        }
                    }

                    Runnable selectCommit = new Runnable() {
                        private boolean invokedLater = false;

                        @Override
                        public void run() {
                            VcsLogUiImpl logUi = log.getLogUi();
                            if (logUi != null) {
                                logUi.getVcsLog().jumpToReference(hash);
                            } else {
                                if (invokedLater) {
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException ignored) {
                                    }
                                }
                                invokedLater = true;
                                windowManager.invokeLater(this);
                            }
                        }
                    };

                    if (!window.isVisible()) {
                        window.activate(selectCommit, true);
                    } else {
                        selectCommit.run();
                    }
                }
            };
        }

    }

}
