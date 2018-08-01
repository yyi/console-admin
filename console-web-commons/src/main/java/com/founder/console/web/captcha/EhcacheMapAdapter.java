package com.founder.console.web.captcha;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.*;

public class EhcacheMapAdapter<K, V> implements Map<K, V> {

    private final Ehcache targetCache;

    public EhcacheMapAdapter(Ehcache targetCache) {
        this.targetCache = targetCache;
    }

    @Override
    public int size() {
        return targetCache.getSize();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return targetCache.get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Object key : targetCache.getKeys()) {
            Element element = targetCache.get(key);
            if (element != null && element.getValue() != null && element.getValue().equals(value))
                return true;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Element element = targetCache.get(key);
        if (element != null)
            return (V) element.getValue();
        else
            return null;
    }

    @Override
    public V put(K key, V value) {
        V previousValue = get(key);
        targetCache.put(new Element(key, value));
        return previousValue;
    }

    @Override
    public V remove(Object key) {
        V previousValue = get(key);
        targetCache.remove(key);
        return previousValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet())
            put(entry.getKey(), entry.getValue());
    }

    @Override
    public void clear() {
        targetCache.removeAll();
    }

    @Override
    public Set<K> keySet() {
        return new HashSet<K>(targetCache.getKeys());
    }

    @Override
    public Collection<V> values() {
        final ArrayList<V> values = new ArrayList<V>();
        for (Object key : targetCache.getKeys()) {
            Element element = targetCache.get(key);
            if (element != null)
                values.add((V) element.getValue());
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<K, V>> values = new HashSet<>();
        for (Object key : targetCache.getKeys()) {
            Element element = targetCache.get(key);
            if (element != null)
                values.add(new EhcacheEntry<K, V>((K) key));
        }
        return values;
    }

    private class EhcacheEntry<K, V> implements Entry<K, V> {

        private final K key;

        public EhcacheEntry(K key) {
            this.key = key;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            Element element = targetCache.get(key);
            return element != null ? (V) element.getValue() : null;
        }

        @Override
        public V setValue(V value) {
            Element element = targetCache.get(key);
            V previousValue = element != null ? (V) element.getValue() : null;
            targetCache.put(new Element(key, value));
            return previousValue;
        }
    }
}
