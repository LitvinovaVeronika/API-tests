package tests.projects.get;

import org.junit.Test;
import tests.BaseTest;
import tests.Config;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static tests.Config.getAdminToken;
import static tests.utils.Utils.createHeaders;

public class AccessDeniedGetFakeProjectAPITest extends BaseTest {

    @Test(timeout = 60000)
    public void getProject_should_accessDenied_when_controlUserHeader_and_fakeProject() {
        given()
                .header("Authorization", "Bearer " + getAdminToken())
                .pathParam("project-id", UUID.randomUUID().toString())
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(403)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));
    }
}
