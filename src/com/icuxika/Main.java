package com.icuxika;

import com.google.gson.Gson;
import com.icuxika.entity.Data;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");

        Data data = new Data();
        data.setName("张三");
        data.setAge(24);
        System.out.println(data);
        Gson gson = new Gson();
        System.out.println(gson.toJson(data));
    }
}