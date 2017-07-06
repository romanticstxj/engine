package com.madhouse.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by WUJUNFENG on 2017/6/16.
 */
public class SetUtil<T> {
    public Set<T> setUnion(Set<T> var1, Set<T> var2) {
        Set<T> result = new HashSet<>();

        if (var1 != null && !var1.isEmpty()) {
            result.addAll(var1);
        }

        if (var2 != null && !var2.isEmpty()) {
            result.addAll(var2);
        }

        return result;
    }

    public Set<T> setInter(Set<T> var1, Set<T> var2) {
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

    public Set<T> setDiff(Set<T> var1, Set<T> var2) {
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

    public Set<T> multiSetInter(List<Set<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Set<T> var : list) {
                if (result != null) {
                    result = this.setInter(result, var);
                } else {
                    result = var;
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }

    public Set<T> multiSetUnion(List<Set<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Set<T> var : list) {
                if (result != null) {
                    result = this.setUnion(result, var);
                } else {
                    result = var;
                }
            }
        } else {
            result = new HashSet<>();
        }

        return result;
    }

    public Set<T> multiSetDiff(List<Set<T>> list) {
        Set<T> result = null;

        if (!list.isEmpty()) {
            for (Set<T> var : list) {
                if (result != null) {
                    result = this.setDiff(result, var);
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
