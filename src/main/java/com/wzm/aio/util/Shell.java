package com.wzm.aio.util;

import expect4j.Expect4j;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.regex.MalformedPatternException;

import java.io.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Shell {

    private static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

    private static final List<String> winCommandPrefix = List.of("cmd.exe", "/c");

    private static final Log logger = LogFactory.getLog(Shell.class);

    public static void main(String[] args) {
        String location = "D:/Data/Code/aio/target/classes/temp.cmd";
        HashMap<String, String> map = new HashMap<>();
        map.put("请", "王梓铭");
        exec(map, System.out::println, "D:\\Data\\Temporary", location);
    }


    public static void exec(Consumer<String> outputConsumer, String directory, String... command) throws IOException {
        exec(Collections.emptyMap(), outputConsumer, directory, command);
    }

    public static void exec(Map<String, String> matches, Consumer<String> outputConsumer, String directory, String... command) {

        ProcessBuilder pb = new ProcessBuilder(perProcess(command));
        pb.directory(new File(directory));
        pb.redirectErrorStream(true);
        try {
            Process start = pb.start();
            try (OutputStream outputStream = start.getOutputStream();
                 InputStream inputStream = start.getInputStream()) {
                dealInteractive(inputStream, outputStream, matches, outputConsumer);
            }
        } catch (IOException e) {
            throw new RuntimeException("执行shell脚本失败",e);
        }
    }
    private static void dealInteractive(InputStream inputStream, OutputStream outputStream, Map<String, String> matches, Consumer<String> outputConsumer) throws UnsupportedEncodingException {
        Expect4j expect = new Expect4j(inputStream, new PrintStream(outputStream, false, charset()));
        expect.registerBufferChangeLogger((chars, len) -> outputConsumer.accept(new String(chars, 0, len)));
        List<Match> collect = matches.entrySet().stream().map(entry -> {
            try {
                return new RegExpMatch(entry.getKey(), state -> {
                    expect.send(entry.getValue() + "\n");
                    outputConsumer.accept(entry.getValue());
                    state.exp_continue();
                });
            } catch (MalformedPatternException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        try {
            expect.expect(collect);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] perProcess(String[] command) {
        if (isWin)
            return Stream.concat(winCommandPrefix.stream(), Arrays.stream(command)).toArray(String[]::new);
        return command;
    }

    private static String charset() {
        return isWin ? "GBK" : Charset.defaultCharset().toString();
    }
}