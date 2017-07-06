package com.madhouse.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by WUJUNFENG on 2017/6/16.
 */
public class SetUtil {
    public static<T> Set<T> setUnion(Set<T> var1, Set<T> var2) {
        Set<T> result = new HashSet<>();

        if (var1 != null && !var1.isEmpty()) {
            result.addAll(var1);
        }

        if (var2 != null && !var2.isEmpty()) {
            result.addAll(var2);
        }

        return result;
    }

    public static<T> Set<T> setInter(Set<T> var1, Set<T> var2) {
        Set<T> result = new HashSet<>();

        if (var1 == null || var1.isEmpty() || var2 == null || var2.isEmpty()) {
            return result;
        }

        if (var1.size() < var2.size()) {
            result.addAll(var1);
            result.retainAll(var2);
        } else {
            result.addAll(var2);
            result.retainAll(var1);
        }

        return result;
    }

    public static<T> Set<T> setDiff(Set<T> var1, Set<T> var2) {
        Set<T> result = new HashSet<>();

        if (var1 == null || var1.isEmpty()) {
            return result;
        }

        result.addAll(var1);
        if (var2 == null || var2.isEmpty()) {
            return result;
        }

        result.removeAll(var2);
        return result;
    }

    public static<T> Set<T> multiSetInter(List<Set<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Set<T> var : list) {
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

    public static<T> Set<T> multiSetUnion(List<Set<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Set<T> var : list) {
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

    public static<T> Set<T> multiSetDiff(List<Set<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Set<T> var : list) {
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
