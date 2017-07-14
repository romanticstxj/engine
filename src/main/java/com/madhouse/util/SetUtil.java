package com.madhouse.util;

import java.util.*;

/**
 * Created by WUJUNFENG on 2017/6/16.
 */
public class SetUtil {
    public static<T> Set<T> setUnion(Collection<T> var1, Collection<T> var2) {
        Set<T> result = new HashSet<>();

        if (var1 != null && !var1.isEmpty()) {
            result.addAll(var1);
        }

        if (var2 != null && !var2.isEmpty()) {
            result.addAll(var2);
        }

        return result;
    }

    public static<T> Set<T> setInter(Collection<T> var1, Collection<T> var2) {
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

    public static<T> Set<T> setDiff(Collection<T> var1, Collection<T> var2) {
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

    public static<T> Set<T> multiSetInter(List<Collection<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Collection<T> var : list) {
                if (result != null) {
                    result = SetUtil.setInter(result, var);
                } else {
                    result = new HashSet<>();
                    result.addAll(var);
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }

    public static<T> Set<T> multiSetUnion(List<Collection<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Collection<T> var : list) {
                if (result != null) {
                    result = SetUtil.setUnion(result, var);
                } else {
                    result = new HashSet<>();
                    result.addAll(var);
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }

    public static<T> Set<T> multiSetDiff(List<Collection<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Collection<T> var : list) {
                if (result != null) {
                    result = SetUtil.setDiff(result, var);
                } else {
                    result = new HashSet<>();
                    result.addAll(var);
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }
}
