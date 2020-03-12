package tests.utils.projects;

import io.restassured.response.ExtractableResponse;
import tests.utils.Utils;
import tests.utils.users.User;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static tests.utils.projects.ProjectUtils.getProjectDateCreationFromResponse;
import static tests.utils.projects.ProjectUtils.getProjectIdFromResponse;
import static tests.utils.projects.ProjectUtils.isExistingProject;

public class Project {

    private static Logger logger = Logger.getLogger(Project.class.getName());

    private final User user;
    private final String id;
    private Date creationDate;
    private String projectName;

    public static Project post(User user, String projectName) throws Exception {
        ExtractableResponse response = ProjectUtils.createProject(user.getCloudToken(), projectName);
        return new Project(user, getProjectIdFromResponse(response),
                getProjectDateCreationFromResponse(response), projectName);
    }

    public Project(User user, String id, Date creationDate, String projectName) {
        Objects.requireNonNull(user, "user is null");
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(projectName, "projectName is null");
        this.user = user;
        this.id = id;
        this.creationDate = creationDate;
        this.projectName = projectName;
    }

    public Project(User user, String id) {
        this.user = user;
        this.id = id;
    }

    public Project remove() {
        ProjectUtils.removeProject(user.getCloudToken(), id);
        assertThat(this.exists(), is(false));
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return projectName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Project waitForCreation(long timeout) throws TimeoutException, InterruptedException {
        long started = System.currentTimeMillis();
        try {
            Utils.waitUntil(this, Project::exists, timeout);
        } catch (TimeoutException e) {
            logger.info(e.getMessage());
            throw e;
        }
        logger.info(String.format("Waiting time: %s ms", System.currentTimeMillis() - started));
        return this;
    }

    public boolean exists() {
        return isExistingProject(user.getCloudToken(), id);
    }
}
