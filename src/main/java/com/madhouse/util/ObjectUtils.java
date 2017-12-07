package com.madhouse.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class ObjectUtils {
    private static XStream xStream = new XStream();
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Collection) {
            if (((Collection)obj).isEmpty()) {
                return true;
            }

            return false;
        }

        if (obj instanceof Map) {
            if (((Map)obj).isEmpty()) {
                return true;
            }

            return false;
        }

        if (obj instanceof String) {
            return StringUtils.isEmpty((String)obj);
        }

        return false;
    }

    public static String toEntityString(final HttpEntity entity) throws IOException {

        if (entity == null) {
            return null;
        }

        boolean needDecompress = false;

        Header ceheader = entity.getContentEncoding();
        if (ceheader != null) {
            HeaderElement[] codecs = ceheader.getElements();
            for (int i = 0; i < codecs.length; i++) {
                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                    needDecompress = true;
                }
            }
        }

        String outStr = "";
        if (needDecompress) {
            InputStream is = entity.getContent();
            GZIPInputStream gis = new GZIPInputStream(is);
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(gis));

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    outStr += line;
                }
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        } else {
            outStr = EntityUtils.toString(entity);
        }

        return outStr;
    }
    public String objectToXml (Object obj) {
        return xStream.toXML(obj);
    }
}
