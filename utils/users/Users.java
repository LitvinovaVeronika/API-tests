package tests.utils.users;

import tests.Config;
import tests.utils.auxiliary.YamlReader;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Users {

    public static User testUser;

    public static Map<String, String> tokenRequestParams;
    private static Logger logger = Logger.getLogger(Users.class.getName());

    public static void init() throws Exception {

        List<Map<String, String>> users;

        try {
            YamlReader reader = new YamlReader();
            Map config = reader.read(Config.getConfigFilePath());
            tokenRequestParams = (Map<String, String>) config.get("tokenRequestParameters");
            users = (List<Map<String, String>>) config.get("apiTestUsers");
        } catch (Exception e) {
            logger.info("Config file was NOT read");
            throw e;
        }
        logger.info("Config file was read");

        try {
            Map<String, String> firstUser = users.get(0);
            testUser = User.UserBuilder.anUser()
                    .withName(firstUser.get("name"))
                    .withDescription(firstUser.get("password"))
                    .withRole(User.Role.PROJECT)
                    .build();
        } catch (Exception e) {
            logger.info("Users couldn't be initialized");
            throw e;
        }
        logger.info("Users were initialized");
    }

    public static void clearOut() {
        try {
            testUser.clearOut();
        } catch (Exception e) {
            logger.info("Problem with user cleanup");
            throw e;
        }
        logger.info("Users were cleaned up");
    }
}
