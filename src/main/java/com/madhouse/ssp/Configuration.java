package com.madhouse.ssp;

import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by WUJUNFENG on 2017/6/14.
 */
public class Configuration {
    private List<Pair<Long, String>> bidurls = new LinkedList<Pair<Long, String>>();
    private String kafkabrokers;
    private Pair<String, String> redismaster;
    private Pair<String, String> redisslave;

    public boolean init(String path) {

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(path));
            Element element = document.getRootElement();

        } catch (Exception ex) {

        }

        return true;
    }

}
