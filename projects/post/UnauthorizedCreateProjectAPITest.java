package tests.projects.post;

import org.junit.After;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static tests.Config.getTestUser;
import static tests.utils.Utils.createHeaders;
import static tests.utils.Utils.invokeNotNull;

public class UnauthorizedCreateProjectAPITest extends BaseTest {

    private String projectName = "project_name";

    @Test(timeout = 60000)
    public void postProject_should_unauthorized_when_fakeUserHeader() {
        given()
                .headers(createHeaders(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .contentType("application/json")
                .body("{" +
                        "  \"name\":\"" + projectName + "\"" +
                        "}")
                .when().post("/projects")
                .then()
                .assertThat().statusCode(401);
    }

    @Test(timeout = 60000)
    public void createProject_should_unauthorized_when_withoutUserHeader() {
        given()
                .contentType("application/json")
                .body("{" +
                        "  \"name\":\"" + projectName + "\"" +
                        "}")
                .when().post("/projects")
                .then()
                .assertThat().statusCode(401);
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(getTestUser().getProjectByName(projectName), Project::remove);
    }
}
