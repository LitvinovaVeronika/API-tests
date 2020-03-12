package tests.projects.delete;

import org.junit.Test;
import tests.BaseTest;
import tests.Config;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static tests.Config.getAdminToken;
import static tests.utils.Utils.createHeaders;

public class AccessDeniedRemoveFakeProjectAPITest extends BaseTest {

    @Test(timeout = 60000)
    public void deleteProject_should_accessDenied_when_controlUserHeader_fakeProject() {
        given()
                .header("Authorization", "Bearer " + getAdminToken())
                .pathParam("project-id", UUID.randomUUID().toString())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(403)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));
    }
}
