package mystdeim.vertx;

import com.yahoo.platform.yui.compressor.CssCompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Roman Novikov
 *
 * Source directory:
 * assets/
 *   css/app.css
 *   js/app.js
 *   images/
 *     logo.png
 *     other/
 *       img01.png
 *
 * Build directory:
 * build/resources/main/
 *   manifest.properties
 *   assets/
 *     app-#md5hash#.css
 *     app-#md5hash#.js
 *     logo-#md5hash#.png
 *     other/
 *       img01-#md5hash#.png
 *
 */
public class Engine {

    public static final String APP = "app";
    public static final String CSS = "css";
    public static final String IMAGES = "img";
    public static final String MANIFEST = "assets.properties";

    static final int MAX_LINE_LENGTH = 1_000_000;

    final String inputDir;
    final String outputDir;
    final String assetsName;

    public Engine(String inputDir, String outputDir, String assetsName) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.assetsName = assetsName;
    }

    void run() throws Exception {
        Map<String, String> manifestMap = new HashMap<>();

        // css
        String cssFile = css();
        manifestMap.put("app.css", cssFile);

        // images

//
//        // assets files
//        assets("webroot/public", assetsDir + "/public", manifestMap);
//
        // manifest
        String manifestPath = outputDir + "/" + MANIFEST;
        String manifestContent = manifest(manifestMap);
        Files.write(Paths.get(manifestPath), manifestContent.getBytes());

    }


    String css() throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(String.format("%s/%s/%s.css", inputDir, CSS, APP))));
        String fileName = String.format("%s-%s.css", APP, hash(content));

        String cssDir = String.format("%s/%s", outputDir, assetsName);
        new File(cssDir).mkdirs();
        String css_path = String.format("%s/%s", cssDir, fileName);

        try (Writer css_writer = new FileWriter(css_path)) {
            Reader reader = new StringReader(content);
            CssCompressor compressor = new CssCompressor(reader);
            compressor.compress(css_writer, MAX_LINE_LENGTH);
        }

        return fileName;
    }

    void images(Map<String, String> manifest) throws IOException {
        Files.walk(Paths.get(inputDir, IMAGES)).filter(Files::isRegularFile).forEach(path -> {
            try {
                byte[] bs = Files.readAllBytes(path);
                Path fileOriginal = path.getFileName();
                String fileName[] = fileOriginal.toString().split("\\.(?=[^\\.]+$)");
                String prodFile = String.format("%s-%s.%s", fileName[0], hash(bs), fileName[1]);

                Path parentPath = Paths.get(inputDir, IMAGES).relativize(path).getParent();
                if (null == parentPath) {
                    manifest.put(fileOriginal.toString(), prodFile);
                    Paths.get(outputDir, assetsName).toFile().mkdirs();
                    Files.write(Paths.get(outputDir, assetsName, prodFile), bs);
                } else {
                    manifest.put(
                            parentPath + "/" + fileOriginal.toString(),
                            parentPath + "/" + prodFile);
                    Paths.get(outputDir, assetsName, parentPath.toString()).toFile().mkdirs();
                    Files.write(Paths.get(outputDir, assetsName, parentPath.toString(), prodFile), bs);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    String manifest(Map<String, String> map) {
        return map.entrySet().stream()
                .map(e -> String.format("%s:%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    String hash(String content) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = content.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        String hash = new BigInteger(1, thedigest).toString(16);
        return hash;
    }

    String hash(byte[] bytesOfMessage) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        String hash = new BigInteger(1, thedigest).toString(16);
        return hash;
    }
}
