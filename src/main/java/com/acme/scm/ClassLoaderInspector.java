package com.acme.scm;

import java.lang.reflect.Field;

public class ClassLoaderInspector {

    public static void inspect() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            Field parentField = ClassLoader.class.getDeclaredField("parent");
            parentField.setAccessible(true);

            ClassLoader parent = (ClassLoader) parentField.get(cl);
            System.out.println("Parent ClassLoader: " + parent);

        } catch (Exception e) {
            throw new RuntimeException("ClassLoader inspection failed", e);
        }
    }
}