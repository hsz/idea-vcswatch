package mobi.hsz.idea.vcswatch.util;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;

/**
 * Utils methods.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1.1
 */
public class Utils {

    /**
     * Checks if specified plugin is enabled.
     *
     * @param id plugin id
     * @return plugin is enabled
     */
    public static boolean isPluginEnabled(@NotNull final String id) {
        IdeaPluginDescriptor p = PluginManager.getPlugin(PluginId.getId(id));
        return p instanceof IdeaPluginDescriptorImpl && p.isEnabled();
    }

}
