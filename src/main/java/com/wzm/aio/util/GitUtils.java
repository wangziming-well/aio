package com.wzm.aio.util;

import com.wzm.aio.service.DocusaurusService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.function.Consumer;

public abstract class GitUtils {

    private static final Log logger = LogFactory.getLog(GitUtils.class);

    /**
     * 从指定远端拉取或者克隆项目到指定的文件夹
     *
     * @param repoUrl       远端origin
     * @param localLocation 项目所在文件夹
     */
    public static void cloneOrPull(String repoUrl, String localLocation, Consumer<String> commandOutputConsumer) {
        String repoName = analysisRepoName(repoUrl);
        String localRepoDirectory = localLocation + File.separator + repoName;

        if (existGit(localRepoDirectory)) {
            logger.info("已存在本地仓库,尝试pull origin操作");
            //判断是否该仓库对应的远程仓库是否是repoUrl
            if (!matchOrigin(repoUrl, localRepoDirectory))
                throw new RuntimeException("指定位置已存在本地仓库，但其origin remote 地址与repoUrl不匹配");
            //执行pull
            String command = "git pull origin";
            Shell.exec(commandOutputConsumer, localRepoDirectory, command);
        } else { //指定位置不存在仓库，清空该位置
            //执行clone
            logger.info("不存在本地仓库，尝试clone远程仓库");
            String command = "git clone " + repoUrl;
            Shell.exec(commandOutputConsumer, localLocation, command);
        }
    }

    public static String analysisRepoName(String repoUrl) {
        String[] split = repoUrl.split("/");
        return split[split.length - 1].split("\\.")[0];
    }

    private static boolean matchOrigin(String repoUrl, String localRepoDirectory) {
        String command = "git remote -v";
        String result = Shell.execStr(localRepoDirectory, command);
        return result.contains("origin\t" + repoUrl);
    }

    private static boolean existGit(String localRepoDirectory) {
        File file = new File(localRepoDirectory);
        if (!file.exists())
            return false;
        File gitConfigFile = new File(localRepoDirectory + File.separator + ".git");
        return gitConfigFile.exists();
    }
}
