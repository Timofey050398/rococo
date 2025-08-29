package timofeyqa.rococo.controller.error;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ApiError {

    private final String apiVersion;
    private final String code;
    private final String message;
    private final String domain;
    private final List<String> errors;

    public ApiError(String apiVersion,
                    String code,
                    String message,
                    String domain,
                    String reason) {
        this.apiVersion = apiVersion;
        this.code = code;
        this.message = message;
        this.domain = domain;
        this.errors = List.of(reason);
    }

    public static ApiError fromAttributesMap(String apiVersion, Map<String, Object> attributesMap) {
        return new ApiError(
                apiVersion,
                ((Integer) attributesMap.get("status")).toString(),
                ((String) attributesMap.getOrDefault("error", "No message found")),
                ((String) attributesMap.getOrDefault("path", "No path found")),
                ((String) attributesMap.getOrDefault("error", "No message found"))
        );
    }

    public Map<String, Object> toAttributesMap() {
        return Map.of(
            "apiVersion", apiVersion,
            "code", code,
            "message", message,
            "domain", domain,
            "errors", errors
        );
    }
}
