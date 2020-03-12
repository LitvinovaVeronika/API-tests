package tests.projects.delete;

import org.junit.Test;
import tests.BaseTest;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static tests.Config.getTestUser;

public class NotFoundProjectRemoveProjectAPITest extends BaseTest {

    @Test(timeout = 60000)
    public void deleteProject_should_notFound_when_projectUserHeader_and_fakeProjectId() {
        given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .pathParam("project-id", UUID.randomUUID().toString())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));
    }
}
