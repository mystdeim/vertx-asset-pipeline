package mystdeim.vertx;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Roman Novikov
 */
public class EngineTest {

    static final String ASSETS_DIR_NAME = "assets";

    @Rule
    public TemporaryFolder buildFolder = new TemporaryFolder();

    Engine engine;

    @Before
    public void setUp() {
        engine = new Engine("src/test/resources/mystdeim/assets", buildFolder.getRoot().toString(), ASSETS_DIR_NAME);
    }

    @Test
    public void testCss() throws Exception {
        String cssProductionFile = engine.css();

        assertEquals(1, buildFolder.getRoot().listFiles().length);

        // check file inside assets dir
        Path assetsPath = Paths.get(buildFolder.getRoot().toString(), ASSETS_DIR_NAME);
        assertEquals(1, Files.list(assetsPath).count());
        String cssFileName = Files.list(assetsPath).findAny().get().getFileName().toString();
        assertEquals(cssProductionFile, cssFileName);
        assertTrue(cssProductionFile.startsWith(Engine.APP));
        assertTrue(cssProductionFile.endsWith(".css"));
        assertEquals(40, cssProductionFile.length());

        // check content
        List<String> lines = Files.readAllLines(buildFolder.getRoot().toPath()
                .resolve(ASSETS_DIR_NAME).resolve(cssProductionFile));
        assertEquals(1, lines.size());
    }

    @Test
    public void testImages() throws Exception {
        Map<String, String> map = new HashMap<>();
        engine.images(map);

        // check manifest
        assertEquals(3, map.size());
        {
            String prodName = map.get("logo.png");
            assertTrue(prodName.startsWith("logo-"));
            assertTrue(prodName.endsWith(".png"));
            assertEquals(41, prodName.length());

            Path filePath = buildFolder.getRoot().toPath().resolve(ASSETS_DIR_NAME).resolve(prodName);
            assertTrue(Files.exists(filePath));
        }
        {
            String prodName = map.get("others/photo.jpg");
            assertTrue(prodName.startsWith("others/photo-"));
            assertTrue(prodName.endsWith(".jpg"));
            assertEquals(49, prodName.length());

            Path filePath = buildFolder.getRoot().toPath().resolve(ASSETS_DIR_NAME).resolve(prodName);
            assertTrue(Files.exists(filePath));
        }
        {
            String prodName = map.get("others/gallery/photo1.jpg");
            assertTrue(prodName.startsWith("others/gallery/photo1-"));
            assertTrue(prodName.endsWith(".jpg"));
            assertEquals(58, prodName.length());

            Path filePath = buildFolder.getRoot().toPath().resolve(ASSETS_DIR_NAME).resolve(prodName);
            assertTrue(Files.exists(filePath));
        }
    }

    @Test
    public void testRun() throws Exception {
        engine.run();

        assertEquals(2, buildFolder.getRoot().listFiles().length);

        // check manifest
        Properties manifest = new Properties();
        Path manifestPath = buildFolder.getRoot().toPath().resolve(Engine.MANIFEST);
        manifest.load(new FileInputStream(manifestPath.toFile()));

        // check css
        String cssFile = manifest.getProperty(Engine.APP + ".css");
        assertTrue(cssFile.startsWith(Engine.APP));
        assertTrue(cssFile.endsWith(".css"));
        assertEquals(40, cssFile.length());
    }

    @Test
    public void testManifest() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("app.css", "app-#hash#.css");
        map.put("app.js", "app-#hash#.js");

        String[] content = engine.manifest(map).split(System.getProperty("line.separator"));
        assertEquals(2, content.length);
        assertEquals("app.css:app-#hash#.css", content[0]);
        assertEquals("app.js:app-#hash#.js", content[1]);
    }

}
