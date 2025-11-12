package edu.ftcc.farmstore.util;

import java.nio.file.Path;

public class PathsCfg {
    public static final String DATA_DIR = "data";
    public static Path p(String name){ return Path.of(DATA_DIR, name); }
}
