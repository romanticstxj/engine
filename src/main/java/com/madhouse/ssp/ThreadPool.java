package com.madhouse.ssp;

import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ThreadPool {
    private final static ThreadPool handler = new ThreadPool();
    private LinkedList<WorkThread> threadList = new LinkedList<WorkThread>();

    public static ThreadPool getInstance() {
        return handler;
    }

    public WorkThread getResource() {
        WorkThread thread = null;

        synchronized (threadList) {
            thread = this.threadList.pollLast();
        }

        if (thread == null) {
            thread = new WorkThread();
            if (!thread.init()) {
                thread = null;
            }
        }

        return thread;
    }

    public void releaseResource(WorkThread thread) {
        synchronized (threadList) {
            this.threadList.add(thread);
        }
    }
}
