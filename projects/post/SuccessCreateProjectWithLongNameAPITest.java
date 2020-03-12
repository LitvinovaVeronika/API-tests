package tests.projects.post;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.projects.Project;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tests.Config.getTestUser;
import static tests.utils.Utils.invokeNotNull;

public class SuccessCreateProjectWithLongNameAPITest extends BaseTest {

    private String projectName;

    @Before
    public void setUp() throws Exception {
        projectName = SuccessCreateProjectWithLongNameAPITest.class.getSimpleName();
        projectName = projectName + RandomStringUtils.randomAlphanumeric(219 - projectName.length());
    }

    @Test(timeout = 60000)
    public void postProject_should_success_when_projectUserHeader_and_longProjectName() {
        given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .contentType("application/json")
                .body("{" +
                        "  \"name\":\"" + projectName + "\"" +
                        "}")
                .when().post("/projects")
                .then()
                .assertThat().statusCode(201)
                .assertThat().contentType("application/json")
                .assertThat().body(matchesJsonSchemaInClasspath("create_project_response.json"));

        assertThat(getTestUser().getProjectByName(projectName), is(notNullValue()));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(getTestUser().getProjectByName(projectName), Project::remove);
    }
}

