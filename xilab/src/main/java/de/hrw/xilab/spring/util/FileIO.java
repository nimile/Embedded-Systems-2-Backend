package de.hrw.xilab.spring.util;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.stream.Collectors;

public final class FileIO {
    private FileIO(){}

    public static String readFromResource(String file){
        String result = "";

        try{
            File target = ResourceUtils.getFile("classpath:" + file);
            try(BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(target)))){
                result = rd.lines().collect(Collectors.joining("\n"));
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
