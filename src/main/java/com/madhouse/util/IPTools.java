package com.madhouse.util;

import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by WUJUNFENG on 2017/7/20.
 */
public class IPTools {
    private ArrayList<Pair<Long, String>> iptables = null;

    public IPTools(InputStream inputStream) {
        this.iptables = this.loadIPDatFile(inputStream);
    }

    public IPTools(String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                this.iptables = this.loadIPDatFile(fileInputStream);
            } catch (Exception ex) {
                System.err.println(ex.toString());
            }
        }
    }

    private ArrayList loadIPDatFile(InputStream inputStream) {
        try {
            ArrayList<Pair<Long, String>> iptables = new ArrayList<Pair<Long, String>>();

            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String text = null;
                while((text = bufferedReader.readLine()) != null) {
                    String[] var1 = text.split(",");
                    if (var1.length >= 4) {
                        String[] var2 = var1[0].split("\\.");
                        if (var2.length >= 4) {
                            Long addr = (Long.parseLong(var2[0]) << 24) | (Long.parseLong(var2[1]) << 16 ) | (Long.parseLong(var2[2]) << 8) | Long.parseLong(var2[3]);
                            iptables.add(Pair.of(addr, var1[3]));
                        }
                    }
                }

                reader.close();
                return iptables.isEmpty() ? null : iptables;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getLocation(String ip) {
        if (this.iptables == null || this.iptables.isEmpty()) {
            return null;
        }

        String[] ips = ip.split("\\.");
        if (ips.length >= 4) {
            Long addr = (Long.parseLong(ips[0]) << 24) | (Long.parseLong(ips[1]) << 16 ) | (Long.parseLong(ips[2]) << 8) | Long.parseLong(ips[3]);

            int start = 0;
            int end = this.iptables.size();
            while (end - start > 1) {
                int mid = (start + end) / 2;
                if (this.iptables.get(mid).getLeft() > addr) {
                    end = mid;
                } else {
                    start = mid;
                }
            }

            return this.iptables.get(start).getRight();
        }

        return null;
    }
}
