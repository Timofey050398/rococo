package timofeyqa.rococo.service.api;

import io.qameta.allure.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;
import timofeyqa.rococo.api.AllureApi;
import timofeyqa.rococo.api.core.RestClient;
import timofeyqa.rococo.model.allure.AllureResults;
import timofeyqa.rococo.model.allure.Project;
import timofeyqa.rococo.model.allure.AllureResult;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import retrofit2.Response;
import timofeyqa.rococo.model.allure.ProjectResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AllureApiClient extends RestClient {

  private final AllureApi allureApi;

  private static final Logger LOG = LoggerFactory.getLogger(AllureApiClient.class);
  private static final int MAX_BATCH_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

  public AllureApiClient() {
    super(CFG.allureDockerServiceUrl(),true);
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

  public void cleanResults(String projectId) {
    try {
      execute(allureApi.cleanResults(projectId));
    } catch (HttpException e) {
      logErr("ERROR WHILE CLEAN RESULTS",e);
      throw new RuntimeException(e);
    }
  }

  @Step("Send allure results")
  public void sendResults(String projectId, @Param(mode = Parameter.Mode.HIDDEN) AllureResults allureResults) {
    LOG.info("Preparing to send {} allure results for project {}", allureResults.results().size(), projectId);
    final List<AllureResult> batch = new ArrayList<>();
    int batchSize = 0;
    int batchNumber = 1;
    for (AllureResult result : allureResults.results()) {
      final int resultSize = result.contentBase64().length();
      if (batchSize + resultSize > MAX_BATCH_SIZE_BYTES && !batch.isEmpty()) {
        LOG.info("Sending batch {} with {} results ({} bytes)", batchNumber, batch.size(), batchSize);
        sendBatch(projectId, batch);
        batch.clear();
        batchSize = 0;
        batchNumber++;
      }
      batch.add(result);
      batchSize += resultSize;
    }
    if (!batch.isEmpty()) {
      LOG.info("Sending batch {} with {} results ({} bytes)", batchNumber, batch.size(), batchSize);
      sendBatch(projectId, batch);
    }
  }

  private void sendBatch(String projectId, List<AllureResult> results) {
    try {
      execute(allureApi.sendResults(projectId, new AllureResults(new ArrayList<>(results))));
      LOG.info("Successfully sent batch with {} results", results.size());
    } catch (HttpException e) {
      logErr("ERROR WHILE SEND ALLURE RESULTS", e);
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
