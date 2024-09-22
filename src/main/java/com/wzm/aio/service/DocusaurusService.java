package com.wzm.aio.service;

import com.wzm.aio.properties.DocusaurusProperties;
import com.wzm.aio.util.FileUtils;
import com.wzm.aio.util.Shell;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocusaurusService {
    private static final Log logger = LogFactory.getLog(DocusaurusService.class);
    private final DocusaurusProperties properties;

    private final DocusaurusProperties.Project project;

    private final String projectRootPath;

    private final MarkdownInterceptor interceptor;

    private final ResourceLoader resourceLoader;

    private Consumer<String> commandOutputConsumer = logger::info;

    private final Git git;

    public DocusaurusService(DocusaurusProperties properties, MarkdownInterceptor interceptor, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.project = properties.getProject();
        this.projectRootPath = properties.getWorkDirectory() + File.separator + project.getName();
        this.interceptor = interceptor;
        this.resourceLoader = resourceLoader;
        this.git = new Git();
        initDirectory();
    }

    //初始化文件夹
    private void initDirectory() {
        String workDirectory = this.properties.getWorkDirectory();
        FileUtils.mkdirs(workDirectory);
    }

    public void loadDocusaurusWeb() {
        checkEnv();
        initDocusaurus();
        loadConfigFile();
        loadNote();
        installPluginDependencies();
        loadDocusaurusStaticPages(); //加载Web静态资源
        loadNotePicture();
    }


    private boolean hasNodejs() {
        String command = "node -v";
        consumeCommand(command);
        String output = Shell.execStr(command).trim();
        consumeCommandOutput(output);
        return output.startsWith("v");
    }

    private boolean hasNpm() {
        String command = "npm -v";
        consumeCommand(command);
        String output = Shell.execStr(command).trim();
        consumeCommandOutput(output);
        Pattern compile = Pattern.compile("^\\d.*?");
        Matcher matcher = compile.matcher(output);
        return matcher.matches();
    }

    private void checkEnv() {
        if (!hasNpm() || !hasNodejs())
            throw new RuntimeException("docusaurus环境未就绪:npm["+hasNpm()+"];nodejs["+hasNodejs()+"]");
    }

    private boolean docusaurusReady() {
        File file = new File(this.projectRootPath);
        return file.exists();
    }

    private void initDocusaurus() {
        logger.info("初始化docusaurus项目");
        if (docusaurusReady()) {
            logger.info("docusaurus项目已存在，跳过项目初始化");
            return;
        }

        String command = "npx create-docusaurus@" + project.getVersion() + " " + project.getName() + " classic --javascript";
        consumeCommand(command);
        Shell.exec(this.commandOutputConsumer, properties.getWorkDirectory(), command);
        logger.info("完成docusaurus项目初始化");
    }

    private void loadConfigFile() {
        logger.info("加载docusaurus项目配置文件");

        try {
            File localDocusaurusConfig = resourceLoader.getResource(project.getDocusaurusConfigPath()).getFile();
            File localSidebarsConfig = resourceLoader.getResource(project.getSidebarsConfigPath()).getFile();
            logger.info("复制文件["+localDocusaurusConfig+"]到文件夹["+this.projectRootPath+"]下");
            FileUtils.copy(localDocusaurusConfig, this.projectRootPath);
            logger.info("复制文件["+localSidebarsConfig+"]到文件夹["+this.projectRootPath+"]下");
            FileUtils.copy(localSidebarsConfig, this.projectRootPath);
        } catch (IOException e) {
            throw new RuntimeException("加载docusaurus配置文件异常", e);
        }
        logger.info("加载docusaurus项目配置文件完成");

    }

    private void loadNote() {
        logger.info("加载note到docusaurus项目");

        //从远程仓库拉取
        String noteRepoUrl = properties.getNoteRepoUrl();
        String workDirectory = properties.getWorkDirectory();
        logger.info("从" + noteRepoUrl + " 拉取远端仓库到本地:" + workDirectory);
        this.git.cloneOrPull(noteRepoUrl, workDirectory);
        //将note移动到Docusaurus项目中
        String repoName = this.git.analysisRepoName(noteRepoUrl);
        Path source = Path.of(workDirectory, repoName);
        Path target = Path.of(projectRootPath, "/docs");
        logger.info("复制note文件夹:" + source + " -> " + target);
        //先删除target中的所有内容
        FileUtils.deleteDir(target);
        FileUtils.copyDir(source, target, ".git");
        //处理note中md文件
        logger.info("拦截改造所有md文件");
        interceptor.intercept(target);
        logger.info("完成加载note到docusaurus项目");

    }


    private void loadNotePicture() {
        //从远程仓库拉取
        logger.info("加载图床到静态资源路径");
        String repoUrl = properties.getNotePicture().getRepoUrl();
        String workDirectory = properties.getWorkDirectory();
        logger.info("从" + repoUrl + " 拉取远端仓库到:" + workDirectory);
        this.git.cloneOrPull(repoUrl, workDirectory);
        //将仓库中图片文件夹中的所有图片放到静态映射文件夹中
        String imgLocation = properties.getNotePicture().getImgLocation();
        String repoName = this.git.analysisRepoName(repoUrl);
        Path source = Path.of(workDirectory, repoName, imgLocation);
        Path target = Path.of(properties.getNotePicture().getStaticPath());
        logger.info("复制pic文件夹:" + source + " ->" + target);
        FileUtils.deleteDir(target);
        FileUtils.copyDir(source, target);
        logger.info("完成加载图床到静态资源路径");
    }

    private void installPluginDependencies() {
        String[] pluginDependencies = project.getPluginDependencies();
        for (String pluginDependence : pluginDependencies) {
            if (pluginDependenceExists(pluginDependence)) {
                logger.info("检查依赖[" + pluginDependence + "]:已存在");
                continue;
            }
            logger.info("检查依赖[" + pluginDependence + "]:不存在，进行安装操作");
            String command = "npm install --save " + pluginDependence;
            consumeCommand(command);
            Shell.exec(this.commandOutputConsumer, this.projectRootPath, command);
        }
    }

    private boolean pluginDependenceExists(String dependenceName) {
        String command = "npm ls " + dependenceName;
        consumeCommand(command);
        String output = Shell.execStr(this.projectRootPath, command);
        consumeCommandOutput(output);
        return !output.contains("(empty)");
    }

    //编译加载note网站的静态资源
    private void loadDocusaurusStaticPages() {
        logger.info("开始构建docusaurus静态页面");
        String buildOutDir = project.getStaticPath();
        String command = "npm run build -- --out-dir " + buildOutDir;
        consumeCommand(command);
        Shell.exec(this.commandOutputConsumer, this.projectRootPath, command);
        logger.info("完成构建docusaurus静态页面");
    }

    /**
     * 设置命令行输出的消费者，默认情况下，日志打印命令行
     *
     * @param commandOutputConsumer 命令行输出的消费者
     */
    public void setCommandOutputConsumer(Consumer<String> commandOutputConsumer) {
        this.commandOutputConsumer = commandOutputConsumer;
    }

    private void consumeCommand(String command) {
        this.commandOutputConsumer.accept("执行命令: " + command);
    }

    private void consumeCommandOutput(String output) {
        this.commandOutputConsumer.accept(output);
    }

    private class Git {

        /**
         * 从指定远端拉取或者克隆项目到指定的文件夹
         *
         * @param repoUrl       远端origin
         * @param localLocation 项目所在文件夹
         */
        public void cloneOrPull(String repoUrl, String localLocation) {
            String repoName = analysisRepoName(repoUrl);
            String localRepoDirectory = localLocation + File.separator + repoName;

            if (existGit(localRepoDirectory)) {
                logger.info("已存在本地仓库,尝试pull origin操作");
                //判断是否该仓库对应的远程仓库是否是repoUrl
                if (!matchOrigin(repoUrl, localRepoDirectory))
                    throw new RuntimeException("指定位置["+localRepoDirectory +"]已存在本地仓库，但其origin remote 地址与repoUrl["+repoUrl +"]不匹配");
                //执行pull
                String command = "git pull origin";
                DocusaurusService.this.consumeCommand(command);
                Shell.exec(DocusaurusService.this.commandOutputConsumer, localRepoDirectory, command);
            } else { //指定位置不存在仓库，清空该位置
                //执行clone
                logger.info("不存在本地仓库，尝试clone远程仓库");
                String command = "git clone " + repoUrl;
                DocusaurusService.this.consumeCommand(command);
                Shell.exec(DocusaurusService.this.commandOutputConsumer, localLocation, command);
            }
        }

        public String analysisRepoName(String repoUrl) {
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
    }


}
