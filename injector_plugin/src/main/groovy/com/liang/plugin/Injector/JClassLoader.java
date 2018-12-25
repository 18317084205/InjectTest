package com.liang.plugin.Injector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String s) {
        byte[] clazzBytes = null;
        Path path = null;
        try {
            path = Paths.get(new URI(s));
            clazzBytes = Files.readAllBytes(path);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Class clazz = defineClass(s, clazzBytes, 0, clazzBytes.length);
        return clazz;
    }
}
