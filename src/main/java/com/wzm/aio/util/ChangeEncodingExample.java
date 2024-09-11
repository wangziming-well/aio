package com.wzm.aio.util;

import java.io.*;

public class ChangeEncodingExample {
    public static void main(String[] args) {
        try {
            // 构建命令，先改变编码格式为 UTF-8
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "chcp 65001 && echo 你好，世界");

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出，强制使用 UTF-8 编码
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // 正确输出 UTF-8 编码的字符串
            }

            // 等待进程结束
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
