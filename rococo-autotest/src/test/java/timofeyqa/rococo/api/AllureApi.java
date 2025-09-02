package timofeyqa.rococo.api;

import timofeyqa.rococo.model.allure.AllureResults;
import timofeyqa.rococo.model.allure.Project;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import timofeyqa.rococo.model.allure.ProjectResponse;

public interface AllureApi {
  @POST("allure-docker-service/projects")
  Call<Void> createProject(@Body Project project);

  @POST("allure-docker-service/send-results")
  Call<Void> sendResults(@Query("project_id") String projectId,
                         @Body AllureResults allureResults);

  @GET("allure-docker-service/generate-report")
  Call<Void> generateReport(@Query("project_id") String projectId,
                            @Query("execution_name") String executionName,
                            @Query("execution_from") String executionFrom,
                            @Query("execution_type") String executionType);

  @GET("allure-docker-service/projects")
  Call<ProjectResponse> getProjects();

}
