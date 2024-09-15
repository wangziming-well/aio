package com.wzm.aio.service;

import com.wzm.aio.properties.DocusaurusProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 为了让本地的md文件能够在docusaurus中正确渲染为mdx格式，需要对md文件做一些适配处理
 * 同时为了避免cors，将md文档中对图床的访问改为对本地图片的访问，会拉取图床到本地并提供静态资源映射
 * TODO 解决**note:**没有渲染的问题
 */

@Component
public class MarkdownInterceptor {

    private final String cloudPicRootPath;
    private final String localPicRootPath;

    public MarkdownInterceptor(DocusaurusProperties properties) {
        // https://gitee.com/wangziming707/note-pic/raw/master/img
        this.cloudPicRootPath = properties.getNotePicture().getCloudRequestPath();
        // http://localhost:80/image
        this.localPicRootPath = properties.getNotePicture().getLocalRequestPath();
    }
    public void intercept(Path path) {
        try {
            recursionFile(path.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void recursionFile(File file) throws Exception {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                recursionFile(f);
            }
        } else {
            if (file.getName().contains(".md"))
                processMd(file);
        }
    }

    private void processMd(File file) throws IOException {
        String fileName = file.getName();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            String line = reader.readLine();
            if (line == null)
                break;
            sb.append(line).append("\n");
        }
        reader.close();
        String contains = sb.toString();
        contains = InnerInterceptor.intercept(contains, fileName, this.cloudPicRootPath, this.localPicRootPath);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(contains);
        writer.close();
    }


    private static class InnerInterceptor {

        public static String intercept(String contains, String filename, String remotePicRootPath, String localPicRootPath) {
            contains = addTitle(contains, filename);
            contains = changeMysqlCodeBlock(contains);
            contains = dealReactStyleProblem(contains);
            contains = dealImageLink(contains, remotePicRootPath, localPicRootPath);
            contains = dealTitle(contains);
            contains = dealSuperscript(contains);
            return contains;
        }

        private static String addTitle(String contains, String filename) {
            if (filename.contains("_"))
                filename = filename.split("_")[1];
            filename = filename.split("\\.")[0];
            String titleStr = "---\ntitle: " + filename + "\n---\n";
            return titleStr + contains;
        }

        private static String changeMysqlCodeBlock(String contains) {
            return contains.replaceAll("~~~mysql", "~~~sql");
        }

        private static String dealReactStyleProblem(String contains) {
            Pattern compile = Pattern.compile("style=\"(.*?)\"");
            Matcher matcher = compile.matcher(contains);
            while (matcher.find()) {
                String attr = matcher.group(0);
                String attrValue = matcher.group(1);
                String s = transStyleFormat(attrValue);
                contains = contains.replace(attr, s);
            }
            return contains;
        }

        private static String transStyleFormat(String old) {
            StringBuilder sb = new StringBuilder();
            sb.append("style={{");
            String[] pairs = old.split(";");
            for (String pair : pairs) {
                String[] split = pair.split(":");
                String key = split[0];
                while (key.contains("-")) {
                    char[] chars = key.toCharArray();
                    int i = key.indexOf("-");
                    chars[i + 1] = (char) (chars[i + 1] - 32);
                    key = new String(chars);
                    key = key.replace("-", "");
                }
                String value = split[1];
                sb.append(key).append(":").append("'").append(value).append("'");
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}}");
            return sb.toString();
        }

        private static String dealImageLink(String contains, String remotePicRootPath, String localPicRootPath) {
            return contains.replaceAll(remotePicRootPath, localPicRootPath);
        }

        private static String dealTitle(String contains) {
            return contains.replaceAll("# ", "## ");
        }

        private static String dealSuperscript(String contains) {
            Pattern compile = Pattern.compile("\\^(.*?)\\^");
            Matcher matcher = compile.matcher(contains);
            while (matcher.find()) {
                String all = matcher.group(0);
                String inner = matcher.group(1);
                contains = contains.replace(all, inner);
            }
            return contains;
        }

    }
}

