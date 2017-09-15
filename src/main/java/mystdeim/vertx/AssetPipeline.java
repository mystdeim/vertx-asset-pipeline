package mystdeim.vertx;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Roman Novikov
 */
public class AssetPipeline implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("demoSetting", AssetPipelinePluginExtension.class);
        project.getTasks().create("demo", AssetTask.class);

        AssetPipelinePluginExtension ext = new AssetPipelinePluginExtension();
        project.getDependencies().add("compile",
                String.format("io.vertx:vertx-jdbc-client:%s", ext.getVertxVersion()));

        try (Stream<Path> paths = Files.walk(Paths.get("webroot/css"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
