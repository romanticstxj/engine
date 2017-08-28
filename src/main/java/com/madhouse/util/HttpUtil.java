package com.madhouse.util;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by WUJUNFENG on 2017/6/9.
 */
public class HttpUtil {
    private static CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnPerRoute(1024)
            .setMaxConnTotal(1024)
            .build();

    public static String getRealIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Real-IP");
        if (ip != null) {
            return ip;
        }

        ip = req.getHeader("X-Forwarded-For");
        if (ip != null) {
            int pos = ip.lastIndexOf(",");
            if (pos >= 0) {
                ip = ip.substring(pos);
            }

            return ip;
        }

        ip = req.getRemoteAddr();
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    public static String getUserAgent(HttpServletRequest req) {
        String ua = req.getHeader("X-Real-UA");
        if (ua != null) {
            return ua;
        }

        return req.getHeader("User-Agent");
    }

    public static String getParameter(HttpServletRequest req, String param) {
        try {
            Map<String, String[]> params = req.getParameterMap();
            if (params.containsKey(param)) {
                return params.get(param)[0];
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        return "";
    }
    
    public static String getRequestPostBytes(HttpServletRequest request)
        throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {
            
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return new String(buffer);
    }

    public static String downloadFile(String url, String localPath) {
        HttpHead headMethod = null;

        try {
            int threadCount = 1;
            int contentLength = 0;
            final int minChunkSize = 1024 * 1024;

            headMethod = new HttpHead(url);
            HttpResponse resp = httpClient.execute(headMethod);

            if (resp != null && resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Header[] headers = resp.getHeaders("Content-Length");
                if (headers != null && headers.length > 0) {
                    contentLength = Integer.parseInt(headers[0].getValue());
                    if (contentLength > minChunkSize) {
                        headers = resp.getHeaders("Accept-Ranges");
                        if (headers != null && headers.length > 0 && headers[0].getValue().equals("bytes")) {
                            threadCount = (contentLength - 1) / minChunkSize + 1;
                        }
                    }
                }
            }

            final int fileSize = contentLength;
            final int maxThreadCount = (threadCount > 0x7f) ? 0x7f : threadCount;

            if (!localPath.endsWith(File.separator)) {
                localPath += File.separator;
            }

            String fileName = url;
            int pos = fileName.lastIndexOf("/");
            if (pos > 0) {
                fileName = fileName.substring(pos + 1);
                pos = fileName.indexOf("?");
                if (pos > 0) {
                    fileName = fileName.substring(0, pos);
                }
            }

            final String filePath = localPath + URLDecoder.decode(fileName, "utf-8");
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            final AtomicInteger progress = new AtomicInteger();
            final int chunkSize = fileSize / maxThreadCount;
            final CountDownLatch latch = new CountDownLatch(maxThreadCount);
            final ExecutorService executorService = Executors.newFixedThreadPool(maxThreadCount);

            for (int i = 0; i < maxThreadCount; ++i) {
                final int chunkIndex = i;

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        HttpGet getMethod = null;

                        try {
                            getMethod = new HttpGet(url);

                            int beginIndex = 0;
                            if (maxThreadCount > 1) {
                                beginIndex = chunkSize * chunkIndex;
                                int endIndex = beginIndex + chunkSize - 1;
                                if (chunkIndex >= maxThreadCount - 1) {
                                    endIndex = fileSize - 1;
                                }

                                getMethod.setHeader("Range", String.format("bytes=%d-%d", beginIndex, endIndex));
                            }

                            HttpResponse resp = httpClient.execute(getMethod);
                            if (resp != null && (resp.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT || resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)) {
                                RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
                                raf.seek(beginIndex);

                                int len = 0;
                                long checkSum = resp.getEntity().getContentLength();

                                byte[] buffer = new byte[minChunkSize];
                                while ((len = resp.getEntity().getContent().read(buffer)) > 0) {
                                    raf.write(buffer, 0, len);
                                    checkSum -= len;
                                    progress.addAndGet(len);
                                }

                                raf.close();
                                if (checkSum == 0) {
                                    latch.countDown();
                                }
                            }
                        } catch (Exception ex) {
                            System.err.println(ex.toString());
                        } finally {
                            if (getMethod != null) {
                                getMethod.releaseConnection();
                            }
                        }
                    }
                });
            }

            executorService.shutdown();
            while (!executorService.isTerminated()) {
                int len = progress.get();
                Thread.currentThread().sleep(1000);
                System.out.println(String.format("download progress: %.2f%%\t%.2fkB/s", (double)progress.get() / fileSize * 100, (double)(progress.get() - len) / 1024));
            }

            if (latch.getCount() == 0) {
                return filePath;
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        } finally {
            if (headMethod != null) {
                headMethod.releaseConnection();
            }
        }

        return null;
    }
}
