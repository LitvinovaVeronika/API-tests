package tests.projects.delete;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static tests.Config.getTestUser;
import static tests.utils.Utils.createHeaders;
import static tests.utils.Utils.invokeNotNull;

public class UnauthorizedRemoveProjectAPITest extends BaseTest {

    private static Project project;

    @BeforeClass
    public static void setUp() throws Exception {
        project = getTestUser()
                .postProject(UnauthorizedRemoveProjectAPITest.class.getSimpleName());
    }

    @Test(timeout = 60000)
    public void deleteProject_should_unauthorized_when_fakeUserHeader_and_someProject() {
        given()
                .headers(createHeaders(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .pathParam("project-id", project.getId())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);

        assertThat(project.exists(), is(true));
    }

    @Test(timeout = 60000)
    public void deleteProject_should_unauthorized_when_withoutUserHeader_and_someProject() {
        given()
                .pathParam("project-id", project.getId())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);

        assertThat(project.exists(), is(true));
    }

    @Test(timeout = 60000)
    public void deleteProject_should_unauthorized_when_fakeUserHeader_and_fakeProject() {
        given()
                .headers(createHeaders(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .pathParam("project-id", UUID.randomUUID().toString())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);
    }

    @Test(timeout = 60000)
    public void deleteProject_should_unauthorized_when_withoutUserHeader_and_fakeProject() {
        given()
                .pathParam("project-id", UUID.randomUUID().toString())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(401);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        invokeNotNull(project, Project::remove);
    }
}
