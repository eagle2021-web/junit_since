package com.eagle.gav;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.eagle.entity.ClassMod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // 使用 ClassLoader 获取 resources 目录下的文件列表
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        HashMap<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < 11; i++) {
            String path = String.format("junit-jupiter-params-5.%d.0.jar.json", i);
            URL resource = classLoader.getResource(path);
            Path path1 = new File(resource.getFile()).toPath();
            String jsonString = new String(Files.readAllBytes(path1));
            List<ClassMod> classMods = JSONArray.parseArray(jsonString, ClassMod.class);
            for (ClassMod classMod : classMods) {
                List<String> list = map.getOrDefault(classMod.getName(), new ArrayList<>());
                list.add(path1.getFileName().toString().split("params-")[1]);
                map.put(classMod.getName(), list);
            }
        }
        map.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}