package tests.projects.get;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static tests.Config.getTestUser;
import static tests.utils.Utils.createHeaders;
import static tests.utils.Utils.invokeNotNull;

public class UnauthorizedGetProjectAPITest extends BaseTest {

    private static Project project;

    @BeforeClass
    public static void setUp() throws Exception {
        project = getTestUser()
                .postProject(UnauthorizedGetProjectAPITest.class.getSimpleName());
    }

    @Test(timeout = 60000)
    public void getProject_should_unauthorized_when_fakeUserHeader_and_someProject() {
        given()
                .pathParam("project-id", project.getId())
                .headers(createHeaders(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);
    }

    @Test(timeout = 60000)
    public void getProject_should_unauthorized_when_withoutUserHeader_and_someProject() {
        given()
                .pathParam("project-id", project.getId())
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);
    }

    @Test(timeout = 60000)
    public void getProject_should_unauthorized_when_fakeUserKey_and_fakeProject() {
        given()
                .pathParam("project-id", UUID.randomUUID().toString())
                .headers(createHeaders(UUID.randomUUID().toString(), getTestUser().getCloudToken()))
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);
    }

    @Test(timeout = 60000)
    public void getProject_should_unauthorized_when_fakeUserToken_and_fakeProject() {
        given()
                .pathParam("project-id", UUID.randomUUID().toString())
                .header("Authorization", "Bearer " + UUID.randomUUID().toString())
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);
    }

    @Test(timeout = 60000)
    public void getProject_should_unauthorized_when_withoutUserHeader_and_fakeProject() {
        given()
                .pathParam("project-id", UUID.randomUUID().toString())
                .when().get("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        invokeNotNull(project, Project::remove);
    }
}
