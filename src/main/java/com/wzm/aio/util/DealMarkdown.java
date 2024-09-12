package com.wzm.aio.util;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DealMarkdown {
    public static void main(String[] args) throws Exception {
        String filepath = args[0];
        File file = new File(filepath);
        recursionFile(file);
    }

    public static void recursionFile(File file) throws Exception {
        if (file.isDirectory()){
            for (File f : Objects.requireNonNull(file.listFiles())) {
                recursionFile(f);
            }
        } else {
            if (file.getName().contains(".md") )
                processMd(file);
        }
    }

    public static void processMd(File file) throws IOException {
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
        MDAdaptor adaptor = new MDAdaptor();
        contains = adaptor.transform(contains,fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(contains);
        writer.close();
    }
}

class MDAdaptor {

    public MDAdaptor(){
    }


    public String transform(String contains,String filename){
        contains = addTitle(contains,filename);
        contains = changeMysqlCodeBlock(contains);
        contains = dealReactStyleProblem(contains);
        contains = dealImageLink(contains);
        contains = dealTitle(contains);
        contains = dealSuperscript(contains);
        return contains;
    }

    private String addTitle(String contains,String filename){
        if (filename.contains("_"))
            filename = filename.split("_")[1];
        filename = filename.split("\\.")[0];
        String titleStr = "---\ntitle: "+ filename + "\n---\n";
        return titleStr + contains;
    }

    private String changeMysqlCodeBlock(String contains){
        return contains.replaceAll("~~~mysql","~~~sql");
    }

    private String dealReactStyleProblem(String contains){
        Pattern compile = Pattern.compile("style=\"(.*?)\"");
        Matcher matcher = compile.matcher(contains);
        while (matcher.find()){
            String attr = matcher.group(0);
            String attrValue = matcher.group(1);
            String s = transStyleFormat(attrValue);
            contains = contains.replace(attr, s);
        }
        return contains;
    }
    private String transStyleFormat(String old){
        StringBuilder sb = new StringBuilder();
        sb.append("style={{");
        String[] pairs = old.split(";");
        for (String pair : pairs ){
            String[] split = pair.split(":");
            String key = split[0];
            while (key.contains("-")){
                char[] chars = key.toCharArray();
                int i = key.indexOf("-");
                chars[i+1] = (char)(chars[i+1] -32);
                key = new String(chars);
                key = key.replace("-","");
            }
            String value = split[1];
            sb.append(key).append(":").append("'").append(value).append("'");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}}");
        return sb.toString();
    }

    private String dealImageLink(String contains){
        return contains.replaceAll("https://gitee.com/wangziming707/note-pic/raw/master/img","http://47.116.126.160/image");
    }

    private String dealTitle(String contains){
        return contains.replaceAll("# ","## ");
    }

    private String dealSuperscript(String contains){
        Pattern compile = Pattern.compile("\\^(.*?)\\^");
        Matcher matcher = compile.matcher(contains);
        while (matcher.find()){
            String all = matcher.group(0);
            String inner = matcher.group(1);
            contains = contains.replace(all, inner);
        }
        return contains;
    }

}
