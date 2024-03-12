/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An HashMap class that automatically creates default values
 * when they are not present.
 * 
 * This calls allows to write code such as:
 * <code>
 * map.get(key).put(key2, value)
 * </code>
 * without having to check explicitly for null every time.
 * 
 * @author Giacomo Alzetta
 *
 */
public class DefaultHashMap<K, V> implements Map<K, V> {

	private HashMap<K, V> map;
	private Defaulter<V> defaulter;
	
	public DefaultHashMap(Defaulter<V> defaulter, int initialCapacity) {
		this.defaulter = defaulter;
		map = new HashMap<>(initialCapacity);
	}
	
	public DefaultHashMap(Defaulter<V> defaulter) {
		this(defaulter, 0);
	}
	
	public DefaultHashMap(Defaulter<V> defaulter, Map<K, V> map) {
		map = new HashMap<>(map);
		this.defaulter = defaulter;
	}
	
	@Override
	public V get(Object key) {
		
		V val = map.get(key);
		if (val == null) {
			val = defaulter.getDefault();
			map.put((K) key, val);
		}
		return val;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
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
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultHashMap({");
		for (Map.Entry<K, V> entry: entrySet()) {
			builder.append(entry.getKey().toString() + ": " + entry.getValue().toString() +", ");
		}
		builder.append("})");
		return builder.toString();
	}
}
