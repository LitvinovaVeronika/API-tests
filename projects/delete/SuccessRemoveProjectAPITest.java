package tests.projects.delete;

import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.Assert.assertThat;
import static tests.Config.getTestUser;

public class SuccessRemoveProjectAPITest extends BaseTest {

    private Project project;

    @Before
    public void setUp() throws Exception {
        project = getTestUser()
                .postProject(SuccessRemoveProjectAPITest.class.getSimpleName());
    }

    @Test(timeout = 60000)
    public void deleteProject_should_success_when_projectUserHeader_and_emptyOwnProject() {
        given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .pathParam("project-id", project.getId())
                .when().delete("/projects/{project-id}")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body(is(emptyOrNullString()));

        assertThat(project.exists(), is(false));
    }
}
