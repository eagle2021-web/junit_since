package com.eagle.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassMod {
    private String name;
    private String packagePath;
    private String[] annotations;
}
