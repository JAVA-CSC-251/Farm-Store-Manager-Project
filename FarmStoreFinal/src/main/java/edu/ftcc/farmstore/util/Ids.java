package edu.ftcc.farmstore.util;
import java.util.UUID;

public class Ids {
    public static String id(String prefix){ return prefix + "-" + UUID.randomUUID().toString().substring(0,8); }
}
