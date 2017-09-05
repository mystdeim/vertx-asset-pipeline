package mystdeim.vertx;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author Roman Novikov
 */
public class AssetPipeline implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("demoSetting", AssetPipelinePluginExtension.class);
        project.getTasks().create("demo", AssetTask.class);
    }

}
