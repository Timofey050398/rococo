package timofeyqa.rococo.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;
import timofeyqa.rococo.api.AllureApi;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.model.allure.AllureResults;
import timofeyqa.rococo.model.allure.Project;
import io.qameta.allure.Step;
import retrofit2.Response;
import timofeyqa.rococo.model.allure.ProjectResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

public class AllureApiClient extends RestClient {

  private final AllureApi allureApi;

  private static final Logger LOG = LoggerFactory.getLogger(AllureApiClient.class);

  public AllureApiClient() {
    super(CFG.allureDockerServiceUrl());
    this.allureApi = create(AllureApi.class);
  }

  private void logErr(String errText, HttpException e) {
      String errorBody = Optional.of(e)
          .map(HttpException::response)
          .map(Response::errorBody)
          .map(rb -> {
            try {
              return rb.string();
            } catch (IOException ex) {
              throw new UncheckedIOException(ex);
            }
          })
          .orElse("");
    LOG.error("{}. message: {}, error: {}",errText, e.getMessage(), errorBody);
  }

  @Step("Create allure project")
  public void createProject(String projectId) {
    try {
      execute(allureApi.createProject(new Project(projectId)));
    } catch (HttpException e) {
      logErr("ERROR WHILE CREATING ALLURE PROJECT",e);
      throw new RuntimeException(e);
    }
  }

  @Step("Send allure results")
  public void sendResults(String projectId, AllureResults allureResults) {
    try {
      execute(allureApi.sendResults(
          projectId,
          allureResults
      ));
    } catch (HttpException e) {
      logErr("ERROR WHILE SEND ALLURE RESULTS",e);
      throw new RuntimeException(e);
    }
  }

  @Step("Generate allure report")
  public void generateReport(String projectId,
                             String executionName,
                             String executionFrom,
                             String executionType) {
    try {
      execute(allureApi.generateReport(
          projectId,
          executionName,
          executionFrom,
          executionType
      ));
    } catch (HttpException e) {
      logErr("ERROR WHILE GENERATE ALLURE REPORT",e);
      throw new RuntimeException(e);
    }
  }

  @Step("Get projects map")
  public ProjectResponse getProjectsMap() {
    return execute(allureApi.getProjects());
  }

  public boolean isProjectExists(String projectId) {
    return getProjectsMap()
        .data()
        .projects()
        .containsKey(projectId);
  }
}
