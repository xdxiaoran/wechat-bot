package com.harry.wechat.util;

import java.util.Date;

/**
 * @author Harry
 * @date 2020/10/12
 * Time: 18:00
 * Desc: DateUtils
 */
public class DateUtils {

    public static final int MILL = 1_000;

    /**
     * 返回相差分钟
     *
     * @param end
     * @param start
     * @return
     */
    public static int getTimeDifference(Date end, Date start) {


        long endTime = end.getTime();
        long startTime = start.getTime();

        long times = endTime / MILL - startTime / MILL;

        int cost = (int) (times / 60);
        return cost;
    }

    public static void main(String[] args) {
        System.out.println(new Date().getTime());
    }
}
