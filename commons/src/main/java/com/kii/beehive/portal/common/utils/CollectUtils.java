package com.kii.beehive.portal.common.utils;

import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class CollectUtils {


    public static List<String> createList(String... array) {

        return CollectionUtils.arrayToList(array);
    }

    public static <T> T getFirst(List<T> list) {

        if (list == null || list.isEmpty()) {
            return null;
        }

        return (T) list.get(0);
    }

    public static <T> boolean hasElement(List<T> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * check any null or empty in strings
     *
     * @return
     */
    public static boolean containsBlank(String... strings) {

        if (strings == null) {
            return true;
        }

        for (String string : strings) {
            if (Strings.isBlank(string)) {
                return true;
            }
        }

        return false;
    }

    public static String collectionToString(Collection<?> collection) {
        StringBuilder sb = new StringBuilder();
        collection.forEach(data -> {
            if (0 != sb.length()) {
                sb.append(", ");
            }
            sb.append(data);
        });
        return sb.toString();
    }
}
