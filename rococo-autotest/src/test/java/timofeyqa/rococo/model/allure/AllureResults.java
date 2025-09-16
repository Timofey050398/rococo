package timofeyqa.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;

import java.util.List;

public record AllureResults(
    @Param(mode = Parameter.Mode.HIDDEN)
    @JsonProperty("results")
    List<AllureResult> results
) {
}
