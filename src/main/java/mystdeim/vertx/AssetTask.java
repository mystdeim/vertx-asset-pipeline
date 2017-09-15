package mystdeim.vertx;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Roman Novikov
 */
public class AssetTask extends DefaultTask {

    Engine engine;

    public AssetTask() {
        this.engine = new Engine(
                "webroot",
                "build/resources/main/webroot",
                "assets");
    }

    @TaskAction
    public void greet() {
        AssetPipelinePluginExtension extension = getProject().getExtensions().findByType(AssetPipelinePluginExtension.class);
        if (extension == null) {
            extension = new AssetPipelinePluginExtension();
        }

        String message = extension.getMessage();
        System.out.println(message);

//        engine.
    }
}
