package mystdeim.vertx;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Roman Novikov
 */
public class AssetTask extends DefaultTask {

    @TaskAction
    public void greet() {
        AssetPipelinePluginExtension extension = getProject().getExtensions().findByType(AssetPipelinePluginExtension.class);
        if (extension == null) {
            extension = new AssetPipelinePluginExtension();
        }

        System.out.println("hello world!");
    }
}
