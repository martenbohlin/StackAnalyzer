package se.mju.stackanalyzer.util;

import java.util.HashMap;

public abstract class HashMapWithDefaultValue<K,V> extends HashMap<K,V> {
	private static final long serialVersionUID = 1L;

	@Override
	public V get(Object key) {
		@SuppressWarnings("unchecked")
		K key2 = (K) key;
		V v = super.get(key2);
		if (v == null) {
			v = getDefault(key2);
			put(key2, v);
		}
		return v;
	}

	public abstract V getDefault(K key);
}
