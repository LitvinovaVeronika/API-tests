package tests.projects.post;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static tests.Config.getTestUser;
import static tests.utils.Utils.invokeNotNull;

public class BadRequestCreateProjectWithLongNameAPITest extends BaseTest {

    private String projectName;

    @Before
    public void setUp() throws Exception {
        projectName = RandomStringUtils.randomAlphanumeric(220);
    }

    @Test(timeout = 60000)
    public void postProject_should_badRequest_when_projectUserHeader_and_tooLongProjectName() {
        given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .contentType("application/json")
                .body("{" +
                        "  \"name\":\"" + projectName + "\"" +
                        "}")
                .when().post("/projects")
                .then()
                .assertThat().statusCode(400);

        assertThat(getTestUser().getProjectByName(projectName), is(nullValue()));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(getTestUser().getProjectByName(projectName), Project::remove);
    }
}
