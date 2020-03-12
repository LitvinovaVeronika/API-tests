package tests.projects.get;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static tests.Config.getAnotherTestUser;
import static tests.Config.getTestUser;
import static tests.utils.Utils.invokeNotNull;

public class NotFoundGetForeignProjectAPITest extends BaseTest {

    private Project foreignProject;

    @Before
    public void setUp() throws Exception {
        foreignProject = getAnotherTestUser()
                .postProject(NotFoundGetForeignProjectAPITest.class.getSimpleName());
    }

    @Test(timeout = 60000)
    public void getProject_should_notFound_when_projectUserHeader_and_foreignProject() {
        given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .pathParam("project-id", foreignProject.getId())
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(foreignProject, Project::remove);
    }
}
