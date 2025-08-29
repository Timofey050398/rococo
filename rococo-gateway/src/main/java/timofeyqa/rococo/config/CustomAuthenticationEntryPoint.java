package timofeyqa.rococo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import timofeyqa.rococo.controller.error.ApiError;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final RococoGatewayServiceConfig config;

  @Autowired
  public CustomAuthenticationEntryPoint(RococoGatewayServiceConfig config) {
    this.config = config;
  }

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    ApiError apiError = new ApiError(
        config.getApiVersion(),
        "401",
        "Unauthorized",
        request.getRequestURI(),
        authException.getMessage()
    );

    response.getWriter().write(objectMapper.writeValueAsString(apiError));
  }
}
