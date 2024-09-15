package com.wzm.aio.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "docusaurus")
public class DocusaurusProperties {


    private String workDirectory;

    private String noteRepoUrl;

    private final Project project;

    private final NotePicture notePicture;


    public DocusaurusProperties() {
        this.project = new Project();
        this.notePicture = new NotePicture();
    }

    @Data
    public static class Project {
        private String name;
        private String version;
        private String staticPath;
        private String docusaurusConfigPath;
        private String sidebarsConfigPath;
        private String[] pluginDependencies;
    }

    @Data
    public static class NotePicture {
        private String repoUrl;
        private String staticPath;
        private String imgLocation;
        private String cloudRequestPath;
        private String localRequestPath;

    }
}
