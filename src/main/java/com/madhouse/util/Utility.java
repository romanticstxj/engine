package com.madhouse.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public final class Utility {
    private static final Random random = new Random(System.currentTimeMillis());

    public static <T> int randomWithWeights(Collection<Pair<T, Integer>> dataSource) {
        if (dataSource == null || dataSource.isEmpty()) {
            return -1;
        }

        List<Pair<Integer, Integer>> weightsList = new ArrayList<>(dataSource.size());

        int index = 0;
        int maxLength = 0;
        for (Pair<T, Integer> data : dataSource) {
            Pair<Integer, Integer> var1 = Pair.of(index++, maxLength);
            weightsList.add(var1);
            maxLength += data.getRight();
        }

        int value = random.nextInt(maxLength);

        int start = 0;
        int end = weightsList.size();
        while (end - start > 1) {
            int mid = (end + start) / 2;
            Pair<Integer, Integer> var1 = weightsList.get(mid);
            if (value >= var1.getRight()) {
                start = mid;
            } else {
                end = mid;
            }
        }

        return weightsList.get(start).getLeft();
    }
}
