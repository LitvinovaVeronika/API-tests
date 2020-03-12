package tests.projects.delete;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.Config;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tests.Config.getAdminToken;
import static tests.Config.getTestUser;
import static tests.utils.Utils.createHeaders;
import static tests.utils.Utils.invokeNotNull;

public class AccessDeniedRemoveProjectAPITest extends BaseTest {

    private Project project;

    @Before
    public void setUp() throws Exception {
        project = getTestUser()
                .postProject(AccessDeniedRemoveProjectAPITest.class.getSimpleName());
    }

    @Test(timeout = 60000)
    public void deleteProject_should_accessDenied_when_controlUserHeader_foreignUserProject() {
        given()
                .header("Authorization", "Bearer " + getAdminToken())
                .pathParam("project-id", project.getId())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(403)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"));

        assertThat(project.exists(), is(true));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(project, Project::remove);
    }
}
