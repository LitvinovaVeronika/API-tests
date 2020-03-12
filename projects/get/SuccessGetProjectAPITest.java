package tests.projects.get;

import io.restassured.response.ExtractableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;
import tests.utils.projects.ProjectUtils;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static tests.Config.getTestUser;
import static tests.utils.Utils.invokeNotNull;

public class SuccessGetProjectAPITest extends BaseTest {

    private Project project;
    private String fullProjectName;

    @Before
    public void setUp() throws Exception {
        fullProjectName = SuccessGetProjectAPITest.class.getSimpleName();
        project = getTestUser()
                .postProject(fullProjectName);
    }

    @Test(timeout = 60000)
    public void getProject_should_success_when_projectUserHeader_and_ownProject() throws Exception {

        ExtractableResponse response = given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .pathParam("project-id", project.getId())
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(200)
                .assertThat().contentType("application/json")
                .assertThat().body(matchesJsonSchemaInClasspath("get_project_response.json"))
                .assertThat().body("project", notNullValue())
                .assertThat().body("project.id", is(project.getId()))
                .assertThat().body("project.name", is(fullProjectName))
                .extract();

        Date actualDate = ProjectUtils.getProjectDateCreationFromResponse(response);
        assertThat(actualDate, is(project.getCreationDate()));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(project, Project::remove);
    }
}
