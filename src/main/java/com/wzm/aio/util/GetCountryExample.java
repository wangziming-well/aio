package com.wzm.aio.util;

import java.util.Locale;

public class GetCountryExample {
    public static void main(String[] args) {
        // 获取当前系统默认的 Locale
        Locale currentLocale = Locale.getDefault();

        // 获取国家代码（ISO 3166）
        String countryCode = currentLocale.getCountry();

        // 获取国家名称（根据当前 Locale 显示）
        String countryName = currentLocale.getDisplayCountry();

        // 输出结果
        System.out.println("Country Code: " + countryCode);
        System.out.println("Country Name: " + countryName);
    }
}