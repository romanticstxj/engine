package com.madhouse.ssp;

import com.madhouse.ssp.Constant;
import com.madhouse.ssp.ThreadPool;
import com.madhouse.ssp.WorkThread;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ClickServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WorkThread thread = ThreadPool.getInstance().getResource();
        if (thread != null) {
            thread.onClick(req, resp);
            ThreadPool.getInstance().releaseResource(thread);
        } else {
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }
    }
}
