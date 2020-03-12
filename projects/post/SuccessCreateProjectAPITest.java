package tests.projects.post;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tests.BaseTest;
import tests.utils.projects.Project;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tests.Config.getTestUser;
import static tests.utils.Utils.invokeNotNull;

@RunWith(Parameterized.class)
public class SuccessCreateProjectAPITest extends BaseTest {

    @Parameterized.Parameters(name = "{index} : name = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"english", "project_name"},
                {"russian", "имя проекта"},
                {"unicorn", "\uD83E\uDD84\uD83E\uDD84\uD83E\uDD84"},
                {"with acute accent", "Castillito Aragón"},
                {"with +", "project+"}
        });
    }

    @Parameterized.Parameter()
    public String testSubName;

    @Parameterized.Parameter(1)
    public String projectName;

    @Before
    public void setUp() throws Exception {
        projectName = SuccessCreateProjectAPITest.class.getSimpleName() + projectName;
    }

    @Test(timeout = 60000)
    public void postProject_should_created_when_projectUserHeader() {
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
                .assertThat().body(matchesJsonSchemaInClasspath("create_project_response.json"))
                .assertThat().body("project.id", notNullValue())
                .assertThat().body("project.name", is(projectName))
                .assertThat().body("project.creation_date", notNullValue());

        assertThat(getTestUser().getProjectByName(projectName), is(notNullValue()));
    }

    @After
    public void tearDown() throws Exception {
        invokeNotNull(getTestUser().getProjectByName(projectName), Project::remove);
    }
}
