package tests.projects.post;

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

public class AccessDeniedCreateProjectAPITest extends BaseTest {

    private String projectName;

    @Before
    public void setUp() throws Exception {
        projectName = AccessDeniedCreateProjectAPITest.class.getSimpleName();
    }

    @Test(timeout = 60000)
    public void postProject_should_accessDenied_when_controlUserHeader() {
        given()
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType("application/json")
                .body("{" +
                        "  \"name\":\"" + projectName + "\"" +
                        "}")
                .when().post("/projects")
                .then()
                .assertThat().statusCode(403)
                .assertThat().body(matchesJsonSchemaInClasspath("error_response.json"))
                .extract();
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(getTestUser().getProjectByName(projectName), Project::remove);
    }
}
