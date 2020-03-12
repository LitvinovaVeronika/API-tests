package tests.projects.post;

import io.restassured.response.ExtractableResponse;
import org.junit.After;
import org.junit.Test;
import tests.BaseTest;
import tests.utils.auxiliary.ConsumerChain;

import static io.restassured.RestAssured.given;
import static java.util.Objects.nonNull;
import static tests.Config.getTestUser;
import static tests.utils.projects.ProjectUtils.getProjectFromResponse;

public class BadRequestWithInvalidBodyCreateProjectAPITest extends BaseTest {

    private ExtractableResponse response;

    @Test(timeout = 60000)
    public void postProject_should_badRequest_when_projectUserHeader_and_invalidRequestBody() {
        response = given()
                .header("Authorization", "Bearer " + getTestUser().getCloudToken())
                .contentType("application/json")
                .body("{" + "}")
                .when().post("/projects")
                .then()
                .assertThat().statusCode(400)
                .extract();
    }

    @After
    public void tearDown() throws Exception {
        ConsumerChain
                .from(response, response -> {
                    if (nonNull(response) && response.statusCode() == 201) {
                        getProjectFromResponse(getTestUser(), response).remove();
                    }
                }).invoke();
    }
}
