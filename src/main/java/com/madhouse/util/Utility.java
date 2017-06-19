package com.madhouse.util;

import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public final class Utility {
    private static final Random random = new Random(System.currentTimeMillis());

    public static <T> T randomWithWeights(List<Pair<T, Integer>> dataList) {
        ArrayList<Pair<T, Integer>> weightsList = new ArrayList<Pair<T, Integer>>(dataList.size());

        int maxLength = 0;
        for (Pair<T, Integer> obj : dataList) {
            Pair<T, Integer> var1 = Pair.of(obj.getLeft(), maxLength);
            weightsList.add(var1);
            maxLength += obj.getRight();
        }

        int value = random.nextInt(maxLength);

        int start = 0;
        int end = weightsList.size();
        while (end - start > 1) {
            int mid = (end + start) / 2;
            Pair<T, Integer> var1 = weightsList.get(mid);
            if (value >= var1.getRight()) {
                start = mid;
            } else {
                end = mid;
            }
        }

        return weightsList.get(start).getLeft();
    }
}
