package tests.projects.delete;

import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static tests.Config.getTestUser;

public class NotFoundDeletedProjectRemoveProjectAPITest extends BaseTest {

    private Project project;

    @Before
    public void setUp() throws Exception {
        project = getTestUser()
                .postProject(NotFoundDeletedProjectRemoveProjectAPITest.class.getSimpleName())
                .remove();
    }

    @Test(timeout = 60000)
    public void deleteProject_should_notFound_when_projectUserHeader_and_deletedProject() {
        given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .pathParam("project-id", project.getId())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));
    }
}
