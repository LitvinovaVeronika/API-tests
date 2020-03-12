package tests.utils.users;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Headers;
import io.restassured.specification.RequestSpecification;
import tests.Config;
import tests.utils.projects.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static java.util.Objects.nonNull;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static tests.utils.Utils.createHeaders;

public class User {

    private final String key;
    private String cloudToken;
    protected final String name;
    protected final String description;
    protected final Role role;

    private static Logger logger = Logger.getLogger(User.class.getName());

    private static final RequestSpecification SPEC_TO_GET_TOKEN = new RequestSpecBuilder()
            .setBaseUri("https://" + System.getProperty("accountServerName"))
            .setBasePath("")
            .setPort(Integer.parseInt(System.getProperty("accountServerPort")))
            .build();

    protected User(String key, String cloudToken, String name, String description, Role role) {
        this.key = key;
        this.cloudToken = cloudToken;
        this.name = name;
        this.description = description;
        this.role = role;
    }

    public String getKey() {
        return key;
    }

    public String getCloudToken() {
        if (role != Role.CONTROL) { initCloudToken(); }
        return cloudToken;
    }

    public Headers getAuthHeaders() {
        return createHeaders(key, cloudToken);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Role getRole() {
        return role;
    }

    private void setCloudToken(String cloudToken) {
        this.cloudToken = cloudToken;
    }

    private void initCloudToken() {
        try {
            Map<String, String> tokenRequestParams = Config.getTokenRequestParams();
            String accessToken = given()
                    .spec(SPEC_TO_GET_TOKEN)
                    .auth().preemptive().basic(
                            tokenRequestParams.get("client_id"),
                            tokenRequestParams.get("client_secret"))
                    .formParam("grant_type", tokenRequestParams.get("grant_type"))
                    .formParam("username", name)
                    .formParam("password", description)
                    .when().post("/oauth/token")
                    .then().assertThat().statusCode(200)
                    .extract().path("access_token");

            setCloudToken(accessToken);
        } catch (Exception e) {
            logger.info("Token could NOT be obtained");
            throw e;
        }
    }

    public Project postProject(String name) throws Exception {
        Project project = Project.post(this, name);
        project.waitForCreation(TimeUnit.SECONDS.toMillis(60));
        assertThat(project, notNullValue());
        assertThat(project.getId(), notNullValue());
        assertThat(project.getName(), is(name));
        assertThat(project.exists(), is(true));
        return project;
    }

    List<Map<String, Object>> getProjectList() {
        return given()
                .header("Authorization", "Bearer " + getCloudToken())
                .queryParam("limit", 100)
                .when().get("/projects")
                .then().statusCode(200)
                .extract().path("projects");
    }

    protected List<Project> getProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        getProjectList().forEach(project -> projects.add(new Project(this, (String) project.get("id"))));
        return projects;
    }

    public Project getProjectByName(String projectName) {

        List<Map<String, Object>> projectList = getProjectList();

        if (nonNull(projectList)) {
            Map<String, Object> project = projectList
                    .stream()
                    .filter(p -> p.get("name").equals(projectName))
                    .findAny()
                    .orElse(null);

            if (nonNull(project) && nonNull(project.get("id"))) {
                return new Project(this, (String) project.get("id"));
            }
        }

        return null;
    }

    private void removeProjects() {
        getProjects().forEach(Project::remove);
    }


    void clearOut() {
        removeProjects();
    }

    public enum Role {
        CONTROL,
        PROJECT
    }

    static final class UserBuilder {

        private String key;
        private String token;
        private String name;
        private String description;
        private Role role;

        private UserBuilder() {
        }

        static UserBuilder anUser() {
            return new UserBuilder();
        }

        UserBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        UserBuilder withToken(String token) {
            this.token = token;
            return this;
        }

        UserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        UserBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        UserBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        User build() {
            return new User(key, token, name, description, role);
        }
    }
}
