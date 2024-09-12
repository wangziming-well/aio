package com.wzm.aio.service;

import com.wzm.aio.properties.DocusaurusProperties;
import com.wzm.aio.util.FileUtils;
import com.wzm.aio.util.Shell;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocusaurusService {
    private static final Log logger = LogFactory.getLog(DocusaurusService.class);
    private final DocusaurusProperties properties;

    private final DocusaurusProperties.Project project;

    private final String projectContext;


    private final MarkdownInterceptor interceptor;

    public DocusaurusService(DocusaurusProperties properties, MarkdownInterceptor interceptor) {
        this.properties = properties;
        this.project = properties.getProject();
        this.projectContext = properties.getWorkDirectory() + File.separator + project.getName();
        this.interceptor = interceptor;
        initDirectory();
    }

    //初始化文件夹
    private void initDirectory() {
        String workDirectory = this.properties.getWorkDirectory();
        FileUtils.mkdir(workDirectory);
    }

    public boolean hasNodejs() {
        String s = Shell.execStr("node -v").trim();
        logger.info("command[node -v] record: " + s);
        return s.startsWith("v");
    }

    public boolean hasNpm() {
        String s = Shell.execStr("npm -v").trim();
        logger.info("command[npm -v] record: " + s);
        Pattern compile = Pattern.compile("^\\d.*?");
        Matcher matcher = compile.matcher(s);
        return matcher.matches();
    }

    public boolean envReady() {
        return hasNpm() && hasNodejs();
    }

    public boolean docusaurusReady() {
        File file = new File(this.projectContext);
        return file.exists();
    }

    public boolean initDocusaurus() {
        if (docusaurusReady()) {
            logger.info("docusaurus项目已存在，跳过项目初始化");
            return true;
        }
        AtomicBoolean success = new AtomicBoolean(false);
        String command = "npx create-docusaurus@latest " + project.getName() + " classic --javascript";
        Shell.exec(s -> {
            logger.info(s);
            if (s.contains("[SUCCESS]"))
                success.set(true);
        }, properties.getWorkDirectory(), command);
        return success.get();
    }

    public void loadConfigFile() {
        File localDocusaurusConfig = getResource("docusaurus-config/" + project.getDocusaurusConfigFileName());
        File localSidebarsConfig = getResource("docusaurus-config/" + project.getSidebarsConfigFileName());

        FileUtils.copy(localDocusaurusConfig, this.projectContext);
        FileUtils.copy(localSidebarsConfig, this.projectContext);
    }

    private File getResource(String location) {
        URL resource = DocusaurusService.class.getClassLoader().getResource(location);
        try {
            return new File(Objects.requireNonNull(resource).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadNote() {
        //从远程仓库拉取
        String noteRepoUrl = properties.getNoteRepoUrl();
        String workDirectory = properties.getWorkDirectory();
        gitPull(noteRepoUrl, workDirectory);
        //将note移动到Docusaurus项目中
        String repoName = analysisRepoName(noteRepoUrl);

        Path source = Path.of(workDirectory, repoName);
        Path target = Path.of(projectContext, "/docs");
        //先删除target中的所有内容
        FileUtils.deleteDir(target);
        FileUtils.copyDir(source, target, ".git");
        //处理note中md文件
        interceptor.intercept(target);
    }



    public void loadNotePicture() {
        //从远程仓库拉取

        String repoUrl = properties.getNotePicture().getRepoUrl();
        String workDirectory = properties.getWorkDirectory();
        gitPull(repoUrl, workDirectory);
        //将仓库中图片文件夹中的所有图片放到静态映射文件夹中
        String imgLocation = properties.getNotePicture().getImgLocation();
        String repoName = analysisRepoName(repoUrl);
        Path source = Path.of(workDirectory, repoName, imgLocation);
        Path target = Path.of(properties.getStaticLocation(), properties.getNotePicture().getStaticPath());
        FileUtils.copyDir(source, target);
    }

    /**
     * 从指定远端拉取或者克隆项目到指定的文件夹
     *
     * @param repoUrl       远端origin
     * @param localLocation 项目所在文件夹
     */
    public void gitPull(String repoUrl, String localLocation) {
        String repoName = analysisRepoName(repoUrl);
        String localRepoDirectory = localLocation + File.separator + repoName;
        if (existGit(localRepoDirectory)) {
            logger.info("已存在本地仓库,尝试pull origin操作");
            //判断是否该仓库对应的远程仓库是否是repoUrl
            if (!matchOrigin(repoUrl, localRepoDirectory))
                throw new RuntimeException("指定位置已存在本地仓库，但其origin remote 地址与repoUrl不匹配");
            //执行pull
            String command = "git pull origin";
            Shell.exec(new UnifiedOutputConsumer(), localRepoDirectory, command);
        } else { //指定位置不存在仓库，清空该位置
            //执行clone
            logger.info("不存在本地仓库，尝试clone远程仓库");
            String command = "git clone " + repoUrl;
            Shell.exec(new UnifiedOutputConsumer(), localLocation, command);

        }
    }

    private String analysisRepoName(String repoUrl) {
        String[] split = repoUrl.split("/");
        return split[split.length - 1].split("\\.")[0];
    }

    private boolean matchOrigin(String repoUrl, String localRepoDirectory) {
        String command = "git remote -v";
        String result = Shell.execStr(localRepoDirectory, command);
        return result.contains("origin\t" + repoUrl);
    }

    private boolean existGit(String localRepoDirectory) {
        File file = new File(localRepoDirectory);
        if (!file.exists())
            return false;
        File gitConfigFile = new File(localRepoDirectory + File.separator + ".git");
        return gitConfigFile.exists();

    }

    public static class UnifiedOutputConsumer implements Consumer<String> {

        @Override
        public void accept(String s) {
            logger.info(s);
        }
    }
}
