package tests;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;
import tests.utils.users.User;
import tests.utils.users.Users;

import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.restassured.config.MultiPartConfig.multiPartConfig;
import static org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE;

public class Config {

    private final static String ADMIN_TOKEN;
    private final static String CONFIG_FILE_PATH;
    private final static String API_VERSION = "v1.4";

    static void initialize() {
    }

    static {
        RestAssured.baseURI = "https://" + System.getProperty("apiServerName");
        RestAssured.port = Integer.parseInt(System.getProperty("apiServerPort"));
        RestAssured.basePath = API_VERSION;
        RestAssured.useRelaxedHTTPSValidation();

        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig().httpMultipartMode(BROWSER_COMPATIBLE))
                .multiPartConfig(multiPartConfig().defaultCharset("UTF-8"));

        ADMIN_TOKEN = System.getProperty("adminToken");
        CONFIG_FILE_PATH = System.getProperty("configFilePath");

        enableLoggingOfRequestAndResponseIfValidationFails();

        try {
            Users.init();
            Users.clearOut();
        } catch (Exception e) {
            throw new RuntimeException("Problems with user initialization", e);
        }
    }

    private static void enableLoggingOfRequestAndResponseIfValidationFails() {
        final LogConfig logConfig = new LogConfig()
                .defaultStream(
                        new PrintStream(
                                new JULOutputStream(
                                        Logger.getLogger(Config.class.getSimpleName()),
                                        Level.INFO)))
                .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        RestAssured.config = RestAssured.config().logConfig(logConfig);
    }

    public static String getAdminToken() {
        return ADMIN_TOKEN;
    }

    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH;
    }

    public static User getTestUser() {
        return Users.testUser;
    }

    public static Map<String, String> getTokenRequestParams() {
        return Users.tokenRequestParams;
    }

}
