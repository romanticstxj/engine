package com.madhouse.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ObjectUtils {
    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Collection) {
            if (((Collection)obj).size() < 1) {
                return false;
            }
        }
        if (obj instanceof Map) {
            if (((Map)obj).size() < 1) {
                return false;
            }
        }
        if (obj instanceof String) {
            if (StringUtils.isEmpty((String)obj)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isEmpty(Object obj) {
        return !isNotEmpty(obj);
    }
    
    public static String ReadFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }
    
}
