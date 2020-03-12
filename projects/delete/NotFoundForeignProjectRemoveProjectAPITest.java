package tests.projects.delete;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tests.Config.getAnotherTestUser;
import static tests.Config.getTestUser;
import static tests.utils.Utils.invokeNotNull;

public class NotFoundForeignProjectRemoveProjectAPITest extends BaseTest {

    private Project foreignProject;

    @Before
    public void setUp() throws Exception {
        foreignProject = getAnotherTestUser()
                .postProject(NotFoundForeignProjectRemoveProjectAPITest.class.getSimpleName());
    }

    @Test(timeout = 60000)
    public void deleteProject_should_notFound_when_projectUserHeader_and_project_of_other_user() {
        given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .pathParam("project-id", foreignProject.getId())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));

        assertThat(foreignProject.exists(), is(true));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(foreignProject, Project::remove);
    }
}
