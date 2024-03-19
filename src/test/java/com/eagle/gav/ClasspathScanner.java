package com.eagle.gav;

import com.eagle.entity.ClassMod;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class ClasspathScanner {
    public static void main(String[] args) {
        printClassNamesInClasspath();
    }

    public static void printClassNamesInClasspath() {
        for (int i = 0; i < 11; i++) {
            String path = String.format("D:/maven/repository/org/junit/jupiter/junit-jupiter-params/5.%d.0/junit-jupiter-params-5.%d.0.jar", i, i);
            File file = new File(path);
            try {
                scanJarFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(path);
        }
    }

    private static void scanDirectory(File directory) {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
        if (files != null) {
            for (File file : files) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                className = className.replace('/', '.');
                System.out.println(className);
            }
        }
    }

    private static void scanJarFile(File jarFile) throws IOException {
        System.out.println(jarFile);
        URLClassLoader child = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, ClasspathScanner.class.getClassLoader());
        ArrayList<ClassMod> list = new ArrayList<>();
        String filename = jarFile.getName();
        try(JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    System.out.println(entry.getName());
                    // Remove ".class" extension and replace "/" with "."
                    String className = entry.getName().substring(0, entry.getName().length() - 6);
                    System.out.println(className);
                    try {
                        // Load the class
                        className = className.replace('/', '.');
                        if(className.endsWith("module-info")){
                            continue;
                        }
                        Class<?> cls = Class.forName(className, true, child);
                        if(!cls.isAnnotation()){
                            continue;
                        }
                        ClassMod classMod = new ClassMod();
                        classMod.setName(className);
                        String[] annotations1 = new String[cls.getAnnotations().length];
                        int i = 0;
                        for (Annotation annotation : cls.getAnnotations()) {
                            annotations1[i++] = annotation.toString();
                        }
                        classMod.setAnnotations(annotations1);
                        classMod.setPackagePath(className);
                        list.add(classMod);
                        // Print class annotations
                        Annotation[] annotations = cls.getAnnotations();
                    } catch (ClassNotFoundException e) {
                        // Handle the exception if the class cannot be located
                        e.printStackTrace();
                    } catch (NoClassDefFoundError e) {
                        // Handle the exception if the class cannot be located
                        e.printStackTrace();
                    } catch (NoSuchMethodError e){
                        e.printStackTrace();
                    } catch (Throwable e){
                        e.printStackTrace();
                    }
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            // 将 list 写入指定的 JSON 文件
            mapper.writeValue(new File(filename + ".json"), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
