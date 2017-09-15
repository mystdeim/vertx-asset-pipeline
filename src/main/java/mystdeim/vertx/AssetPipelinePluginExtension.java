package mystdeim.vertx;

/**
 * @author Roman Novikov
 */
public class AssetPipelinePluginExtension {

    private String message = "Default Greeting from Gradle!!!";
    private String vertxVersion = "3.4.2";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVertxVersion() {
        return vertxVersion;
    }

    public void setVertxVersion(String vertxVersion) {
        this.vertxVersion = vertxVersion;
    }
}
