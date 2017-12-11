package com.madhouse.util;

import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

public class BidValidateUtil {

    public static boolean validateMediaRequest(MediaRequest.Builder mediaRequest, Logger logger) {
        if (mediaRequest != null) {
            if (StringUtils.isEmpty(mediaRequest.getAdspacekey())) {
                logger.warn("adspaceKey is missing.");
                return false;
            }

            String adspaceKey = mediaRequest.getAdspacekey();
            if (StringUtils.isEmpty(mediaRequest.getBid())) {
                logger.warn("[{}]bid is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getUa())) {
                logger.warn("[{}]ua is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getName())) {
                logger.warn("[{}]appName is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getBundle())) {
                logger.warn("[{}]pkgName is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getOsv())) {
                logger.warn("[{}]osv is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasOs()) {
                logger.warn("[{}]os is missing.", adspaceKey);
                return false;
            }

            switch (mediaRequest.getOs()) {
                case Constant.OSType.ANDROID: {
                    if (StringUtils.isEmpty(mediaRequest.getDid()) && StringUtils.isEmpty(mediaRequest.getDidmd5()) &&
                            StringUtils.isEmpty(mediaRequest.getDpid()) && StringUtils.isEmpty(mediaRequest.getDpidmd5())) {
                        logger.warn("[{}]android deviceId is missing.", adspaceKey);
                        return false;
                    }

                    break;
                }

                case Constant.OSType.IOS: {
                    if (StringUtils.isEmpty(mediaRequest.getDpid()) && StringUtils.isEmpty(mediaRequest.getDpidmd5()) &&
                            StringUtils.isEmpty(mediaRequest.getIfa())) {
                        logger.warn("[{}]iOS deviceId is missing.", adspaceKey);
                        return false;
                    }

                    break;
                }

                default: {
                    if (StringUtils.isEmpty(mediaRequest.getDpid()) && StringUtils.isEmpty(mediaRequest.getDpidmd5()) &&
                            StringUtils.isEmpty(mediaRequest.getMac()) && StringUtils.isEmpty(mediaRequest.getMacmd5())) {
                        logger.warn("[{}]deviceId is missing.", adspaceKey);
                    }

                    break;
                }
            }

            if (!mediaRequest.hasCarrier()) {
                logger.warn("[{}]carrier is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasConnectiontype()) {
                logger.warn("[{}]connection is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasDevicetype()) {
                logger.warn("[{}]devicetype is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasCarrier()) {
                logger.warn("[{}]carrier is missing.", adspaceKey);
                return false;
            }
            return true;
        }

        return false;
    }
}
