package com.wzm.aio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("project")
public class ProjectProperties {

    private Init init;

    private Closed closed;

    @Data
    public static class Init{
        private boolean momoPull;
        private boolean frSync;
    }
    @Data
    public static class Closed{
        private boolean momoPush;
    }
}
