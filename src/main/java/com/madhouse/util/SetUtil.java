package com.madhouse.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by WUJUNFENG on 2017/6/16.
 */
public class SetUtil {
    public static Set<Long> setUnion(Set<Long> var1, Set<Long> var2) {
        Set<Long> result = new HashSet<Long>();

        result.addAll(var1);
        result.addAll(var2);
        return result;
    }

    public static Set<Long> setInter(Set<Long> var1, Set<Long> var2) {
        Set<Long> result = new HashSet<Long>();

        result.addAll(var1);
        result.retainAll(var2);
        return result;
    }

    public static Set<Long> setDiff(Set<Long> var1, Set<Long> var2) {
        Set<Long> result = new HashSet<Long>();

        result.addAll(var1);
        result.removeAll(var2);
        return result;
    }

    public static Set<Long> multiSetInter(List<Set<Long>> list) {
        Set<Long> result = null;

        if (!list.isEmpty()) {
            for (Set<Long> var : list) {
                if (result != null) {
                    result = SetUtil.setInter(result, var);
                } else {
                    result = var;
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }

    public static Set<Long> multiSetUnion(List<Set<Long>> list) {
        Set<Long> result = null;

        if (!list.isEmpty()) {
            for (Set<Long> var : list) {
                if (result != null) {
                    result = SetUtil.setUnion(result, var);
                } else {
                    result = var;
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }

    public static Set<Long> multiSetDiff(List<Set<Long>> list) {
        Set<Long> result = null;

        if (!list.isEmpty()) {
            for (Set<Long> var : list) {
                if (result != null) {
                    result = SetUtil.setDiff(result, var);
                } else {
                    result = var;
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }
}
