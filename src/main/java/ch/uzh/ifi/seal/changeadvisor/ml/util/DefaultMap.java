package ch.uzh.ifi.seal.changeadvisor.ml.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DefaultMap<K, V> implements Map<K, V> {

    private Map<K, V> map;

    private V defaultValue;

    public DefaultMap(V defaultValue) {
        this.defaultValue = defaultValue;
        map = new HashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Entry<K, V> entry : map.entrySet()) {
            sb.append(entry.getKey() + ": " + entry.getValue());
        }

        return sb.append("}").toString();
    }

    public List<Integer> getIndexOfTopNValues(int n) {
        if (n > 0) {
            n = n > map.size() ? map.size() : n;
            Comparator<Integer> valueComparator = new MapValueComparator((Map<Integer, Double>) map);
            TreeMap<Integer, Double> orderedMap = new TreeMap<>(valueComparator);

            orderedMap.putAll((Map<? extends Integer, ? extends Double>) map);


            List<Integer> keys = new ArrayList<>(orderedMap.keySet());
            List<Double> values = new ArrayList<>(orderedMap.values());
            return keys.subList(0, n);
        }
        return new ArrayList<>();
    }
}
