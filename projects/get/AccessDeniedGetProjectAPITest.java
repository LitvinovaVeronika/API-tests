package tests.projects.get;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.Config;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static tests.Config.getAdminToken;
import static tests.Config.getTestUser;
import static tests.utils.Utils.createHeaders;
import static tests.utils.Utils.invokeNotNull;

public class AccessDeniedGetProjectAPITest extends BaseTest {

    private Project project;

    @Before
    public void setUp() throws Exception {
        project = getTestUser()
                .postProject(AccessDeniedGetProjectAPITest.class.getSimpleName());
    }

    @Test(timeout = 60000)
    public void getProject_should_accessDenied_when_controlUserHeader_and_foreignProject() {
        given()
                .header("Authorization", "Bearer " + getAdminToken())
                .pathParam("project-id", project.getId())
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(403)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(project, Project::remove);
    }
}
