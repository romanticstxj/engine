package com.madhouse.util;
/**
 * Created by WUJUNFENG on 2017/7/10.
 */
public class IdWoker {
    private static final long workerIdBits = 12L;
    private static final long sequenceBits = 10L;
    private static final long maxWorkerId =  -1L ^ (-1L << workerIdBits);
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);
    private static final long timeLeftShift = workerIdBits + sequenceBits;
    private static final long workerIdLeftShift = sequenceBits;

    private long workerId;
    private long lastTime = 0L;
    private long sequenceId = 0L;

    public IdWoker(long workerId) {
        if (workerId > maxWorkerId || workerId < 0L) {
            throw new IllegalArgumentException(String.format("worker id can't be greater than %d or less than 0.", maxWorkerId));
        }

        this.workerId = workerId;
    }

    public static long getMaxWorkerId() {
        return maxWorkerId;
    }

    public synchronized long nextId() {
        long time = System.currentTimeMillis();

        if (time < lastTime) {
            try {
                throw new Exception("clock moved backwards.");
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (time <= lastTime) {
                time = System.currentTimeMillis();
            }
        }

        if (time > lastTime) {
            sequenceId = 0L;
            lastTime = time;
        } else {
            sequenceId = (sequenceId + 1L) & sequenceMask;
            if (sequenceId == 0L) {
                while (time <= lastTime) {
                    time = System.currentTimeMillis();
                }

                lastTime = time;
            }
        }

        return ((lastTime << timeLeftShift) | (workerId << workerIdLeftShift) | sequenceId);
    }

    public static long getCreateTimeMillis(long id) {
        return id >> timeLeftShift;
    }
}
