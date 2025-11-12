package edu.ftcc.farmstore.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Csv {
    public static List<String[]> read(Path path){
        List<String[]> rows = new ArrayList<>();
        if (!Files.exists(path)) return rows;
        try (BufferedReader br = Files.newBufferedReader(path)){
            String line; boolean header = true;
            while ((line = br.readLine()) != null){
                if (header){ header=false; continue; }
                if (line.isBlank()) continue;
                rows.add(splitCsv(line));
            }
        } catch(IOException e){ e.printStackTrace(); }
        return rows;
    }

    public static void write(Path path, String header, List<String[]> rows){
        try { Files.createDirectories(path.getParent()); } catch (IOException ignored){}
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))){
            pw.println(header);
            for (String[] r: rows) pw.println(String.join(",", safe(r)));
        } catch(IOException e){ e.printStackTrace(); }
    }

    public static void ensureWithSeed(Path path, String header, List<String[]> seed){
        try { Files.createDirectories(path.getParent()); } catch (IOException ignored){}
        if (!Files.exists(path)){
            write(path, header, seed);
        }
    }

    private static String[] splitCsv(String line){
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQ = false;
        for (int i=0;i<line.length();i++){
            char c=line.charAt(i);
            if (c=='"'){ inQ=!inQ; continue; }
            if (c==',' && !inQ){ out.add(sb.toString()); sb.setLength(0); }
            else sb.append(c);
        }
        out.add(sb.toString());
        return out.toArray(new String[0]);
    }

    private static String sq(String s){
        if (s==null) return "";
        s = s.replace("\"","").replace("\n"," ");
        if (s.contains(",")) return "\""+s+"\"";
        return s;
    }
    private static String[] safe(String[] arr){
        String[] r = new String[arr.length];
        for (int i=0;i<arr.length;i++) r[i]=sq(arr[i]);
        return r;
    }
}
