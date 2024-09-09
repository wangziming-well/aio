package com.wzm.aio.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell {

    public static void main(String[] args) {
        try {
            // 创建 ProcessBuilder 实例，使用 npx 命令
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "npx create-docusaurus@latest my-website classic");

            // 设置工作目录（如果需要）
            pb.directory(new java.io.File("D:\\Data\\Temporary")); // 可以根据需要设置工作目录

            // 启动进程
            Process process = pb.start();

            // 读取命令执行结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),"gbk"));
            String line;
            // 获取进程的标准输入流
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));


            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("TypeScript"))
                    writer.write("TypeScript");
            }

            // 读取错误流（如果有）
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            // 等待命令执行完成
            process.waitFor();

            // 获取退出值
            int exitValue = process.exitValue();
            System.out.println("Exit value: " + exitValue);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}