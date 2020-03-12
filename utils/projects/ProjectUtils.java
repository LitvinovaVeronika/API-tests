package tests.utils.projects;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import tests.utils.auxiliary.matchers.DateMatchers;
import tests.utils.users.User;

import java.util.Date;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static tests.utils.Utils.getObjectMap;

public class ProjectUtils {

    final static String PROJECTS_PATH = "/projects";

    private static Response getProject(String userToken, String projectUID) {
        return given()
                .header("Authorization", "Bearer " + userToken)
                .pathParam("project-id", projectUID)
                .when().get(PROJECTS_PATH + "/{project-id}");
    }

    static boolean isExistingProject(String userToken, String projectUID) {
        return getProject(userToken, projectUID).getStatusCode() == 200;
    }

    static ExtractableResponse createProject(String userToken, String projectName) {
        return given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("{\"name\":\"" + projectName + "\"}")
                .when()
                .post(PROJECTS_PATH)
                .then().statusCode(201)
                .extract();
    }

    static ExtractableResponse removeProject(String userToken, String projectUID) {
        return given()
                .pathParam("project-id", projectUID)
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete(PROJECTS_PATH + "/{project-id}")
                .then().statusCode(200)
                .extract();
    }

    @SuppressWarnings("unchecked")
    static String getProjectIdFromResponse(ExtractableResponse response) {
        return (String) ((Map<String, Object>) getObjectMap(response).get("project")).get("id");
    }

    @SuppressWarnings("unchecked")
    public static Date getProjectDateCreationFromResponse(ExtractableResponse response) throws Exception {
        String date = (String) ((Map<String, Object>) getObjectMap(response).get("project")).get("creation_date");
        return DateMatchers.DATE_FORMAT.parse(date);
    }

    public static Project getProjectFromResponse(User user, ExtractableResponse response) {
        return new Project(user, getProjectIdFromResponse(response));
    }
}
