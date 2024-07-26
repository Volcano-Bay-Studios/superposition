package org.modogthedev.superposition.util;

import java.util.Map;
import java.util.function.Supplier;

public class MapHelper {
    public static <K, V> V getOrPut(Map<K, V> map, K key, Supplier<V> supplier) {
        if (!map.containsKey(key)) {
            map.put(key, supplier.get());
        }

        return map.get(key);
    }
}
