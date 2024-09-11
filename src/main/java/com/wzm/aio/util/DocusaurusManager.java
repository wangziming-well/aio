package com.wzm.aio.util;

public class DocusaurusManager {

    public boolean hasNodejs(){
        String s = Shell.execStr("node -v");
        return s.startsWith("v");
    }

    public static void main(String[] args) {
        DocusaurusManager manager = new DocusaurusManager();
        System.out.println(manager.hasNodejs());
    }


}
