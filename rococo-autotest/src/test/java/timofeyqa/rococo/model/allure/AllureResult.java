package timofeyqa.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
import lombok.ToString;

public record AllureResult(
    @JsonProperty("file_name")
    String fileName,
    @JsonProperty("content_base64")
    @Param(mode = Parameter.Mode.MASKED)
    @ToString.Exclude
    String contentBase64
) {
}
