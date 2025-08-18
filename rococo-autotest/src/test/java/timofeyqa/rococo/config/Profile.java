package timofeyqa.rococo.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Profile {
  LOCAL(LocalConfig.INSTANCE),
  DOCKER(DockerConfig.INSTANCE),
  STAGING(StagingConfig.INSTANCE),
  PROD(ProdConfig.INSTANCE);

  private final Config instance;
}
