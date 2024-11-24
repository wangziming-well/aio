package com.wzm.aio;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 需要下面依赖：
 *         <dependency>
 *             <groupId>org.apache.poi</groupId>
 *             <artifactId>poi</artifactId>
 *             <version>5.2.3</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.apache.poi</groupId>
 *             <artifactId>poi-ooxml</artifactId>
 *             <version>5.2.3</version>
 *         </dependency>
 *  需要指定filepath
 */
public class EsbInfo {

    private static final Map<String,ESB> esbMap = esbMap();
    private static final String filepath = "D:\\Data\\Temporary\\esb.xlsx";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String next;
        while ((next = scanner.next() )!= null){
            //System.out.print("\n"+next);
            List<ESB> esbList = parseText(next);
            esbList.stream().distinct().forEach(esb -> {
                if (esb != null){
                    System.out.print("\n"+esb.serviceId + "--" +esb.func + "(" +esb.name.trim() + ")");
                }
            });
        }
    }

    public static List<ESB> parseText(String text){
        Pattern pattern = Pattern.compile("esb\\.\\w*\\.\\w*\\.\\w*|esb\\.\\w*\\.\\w*");
        Matcher matcher = pattern.matcher(text);
        ArrayList<ESB> result = new ArrayList<>();
        while (matcher.find()){
            String esbId = matcher.group();
            result.add(esbMap.get(esbId));
        }
        return result;
    }

    private static Map<String,ESB> esbMap(){
        HashMap<String,ESB> resultMap = new HashMap<>();

        try{
            FileInputStream fis = new FileInputStream(filepath);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                ESB esb = new ESB();
                esb.serviceId = row.getCell(0).getStringCellValue();
                esb.func = row.getCell(1).getStringCellValue();
                esb.name = row.getCell(2).getStringCellValue();
                resultMap.put(esb.serviceId,esb);
            }
            workbook.close();
            fis.close();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return resultMap;
    }



    public static class ESB{
        public String serviceId;
        public String func;
        public String name;

        @Override
        public String toString() {
            return "ESB{" +
                    "serviceId='" + serviceId + '\'' +
                    ", func='" + func + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

}
