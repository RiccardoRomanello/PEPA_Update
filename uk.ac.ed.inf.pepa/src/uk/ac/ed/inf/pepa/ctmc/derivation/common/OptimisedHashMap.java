/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/

package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


/**
 * Hash table based implementation of the <tt>Map</tt> interface. This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key. (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.) This class makes no guarantees as to the
 * order of the map; in particular, it does not guarantee that the order will
 * remain constant over time.
 * 
 * <p>
 * This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets. Iteration over collection
 * views requires time proportional to the "capacity" of the <tt>HashMap</tt>
 * instance (the number of buckets) plus its size (the number of key-value
 * mappings). Thus, it's very important not to set the initial capacity too high
 * (or the load factor too low) if iteration performance is important.
 * 
 * <p>
 * An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>. The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created. The
 * <i>load factor</i> is a measure of how full the hash table is allowed to get
 * before its capacity is automatically increased. When the number of entries in
 * the hash table exceeds the product of the load factor and the current
 * capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 * 
 * <p>
 * As a general rule, the default load factor (.75) offers a good tradeoff
 * between time and space costs. Higher values decrease the space overhead but
 * increase the lookup cost (reflected in most of the operations of the
 * <tt>HashMap</tt> class, including <tt>get</tt> and <tt>put</tt>). The
 * expected number of entries in the map and its load factor should be taken
 * into account when setting its initial capacity, so as to minimize the number
 * of rehash operations. If the initial capacity is greater than the maximum
 * number of entries divided by the load factor, no rehash operations will ever
 * occur.
 * 
 * <p>
 * If many mappings are to be stored in a <tt>HashMap</tt> instance, creating
 * it with a sufficiently large capacity will allow the mappings to be stored
 * more efficiently than letting it perform automatic rehashing as needed to
 * grow the table.
 * 
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access a hash map concurrently, and at least one of the
 * threads modifies the map structurally, it <i>must</i> be synchronized
 * externally. (A structural modification is any operation that adds or deletes
 * one or more mappings; merely changing the value associated with a key that an
 * instance already contains is not a structural modification.) This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the map.
 * 
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap} method. This
 * is best done at creation time, to prevent accidental unsynchronized access to
 * the map:
 * 
 * <pre>
 *   Map m = Collections.synchronizedMap(new HashMap(...));
 * </pre>
 * 
 * <p>
 * The iterators returned by all of this class's "collection view methods" are
 * <i>fail-fast</i>: if the map is structurally modified at any time after the
 * iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}. Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * 
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed as it
 * is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification. Fail-fast iterators throw
 * <tt>ConcurrentModificationException</tt> on a best-effort basis. Therefore,
 * it would be wrong to write a program that depended on this exception for its
 * correctness: <i>the fail-fast behavior of iterators should be used only to
 * detect bugs.</i>
 * 
 * <p>
 * This class is a member of the <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * 
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 * 
 * @author Doug Lea
 * @author Josh Bloch
 * @author Arthur van Hoff
 * @author Neal Gafter
 * @version 1.72, 04/24/06
 * @see Object#hashCode()
 * @see Collection
 * @see Map
 * @see TreeMap
 * @see Hashtable
 * @since 1.2
 */

public class OptimisedHashMap

{

	public class InsertionResult {

		public State state;

		public boolean wasPresent;

		public InsertionResult(State state, boolean wasPresent) {
			this.state = state;
			this.wasPresent = wasPresent;
		}
	};

	/**
	 * The default initial capacity - MUST be a power of two.
	 */
	static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <= 1<<30.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load factor used when none specified in constructor.
	 */
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * The table, resized as necessary. Length MUST Always be a power of two.
	 */
	transient Entry[] table;

	/**
	 * The number of key-value mappings contained in this map.
	 */
	transient int size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 * 
	 * @serial
	 */
	int threshold;

	/**
	 * The load factor for the hash table.
	 * 
	 * @serial
	 */
	final float loadFactor;

	/**
	 * The number of times this HashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the HashMap or otherwise modify its internal structure (e.g., rehash).
	 * This field is used to make iterators on Collection-views of the HashMap
	 * fail-fast. (See ConcurrentModificationException).
	 */
	transient volatile int modCount;

	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

	private final ReadLock rLock = rwLock.readLock();

	private final WriteLock wLock = rwLock.writeLock();

	/**
	 * Constructs an empty <tt>HashMap</tt> with the specified initial
	 * capacity and load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity
	 * @param loadFactor
	 *            the load factor
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor is
	 *             nonpositive
	 */
	public OptimisedHashMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal initial capacity: "
					+ initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: "
					+ loadFactor);

		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new Entry[capacity];
		init();
	}

	/**
	 * Constructs an empty <tt>HashMap</tt> with the specified initial
	 * capacity and the default load factor (0.75).
	 * 
	 * @param initialCapacity
	 *            the initial capacity.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative.
	 */
	public OptimisedHashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty <tt>HashMap</tt> with the default initial capacity
	 * (16) and the default load factor (0.75).
	 */
	public OptimisedHashMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		table = new Entry[DEFAULT_INITIAL_CAPACITY];
		init();
	}

	/**
	 * Initialization hook for subclasses. This method is called in all
	 * constructors and pseudo-constructors (clone, readObject) after HashMap
	 * has been initialized but before any entries have been inserted. (In the
	 * absence of this method, readObject would require explicit knowledge of
	 * subclasses.)
	 */
	void init() {
	}

	/**
	 * Applies a supplemental hash function to a given hashCode, which defends
	 * against poor quality hash functions. This is critical because HashMap
	 * uses power-of-two length hash tables, that otherwise encounter collisions
	 * for hashCodes that do not differ in lower bits. Note: Null keys always
	 * map to hash 0, thus index 0.
	 */
	static int hash(int h) {
		// This function ensures that hashCodes that differ only by
		// constant multiples at each bit position have a bounded
		// number of collisions (approximately 8 at default load factor).
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * Returns index for hash code h.
	 */
	static int indexFor(int h, int length) {
		return h & (length - 1);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @return the number of key-value mappings in this map
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 * 
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	private State old_getKey(State key) {
		if (key == null)
			throw new NullPointerException();
		int hash = hash(key.hashCode());
		for (Entry e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			State k;
			if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
				return e.key;
		}
		return null;
	}

	/**
	 * 
	 * @param key
	 *            the state vector to find
	 * @param hashCode
	 *            the already calculated hash code
	 * @return
	 */
	private State old_getKey(short[] key, int hashCode) {
		int hash = hash(hashCode);
		int i = indexFor(hash, table.length);
		for (Entry e = table[i]; e != null; e = e.next) {
			// State k;
			if (e.hash == hash && Arrays.equals(e.key.fState, key))
				return e.key;
		}
		return null;
	}
	
	private int stateNumber = 0;

	/**
	 * Puts the arrays as a new state if not already present
	 * 
	 * @param key
	 *            the array to put in the map
	 * @param hashCode
	 *            the hashCode, passed externally to reuse it
	 * @return a new state with unspecified state number if the state was added,
	 *         else the previously present state
	 * 
	 */
	public synchronized InsertionResult putIfNotPresentSync(short[] key,
			int hashCode) {
		int hash = hash(hashCode);
		int i = indexFor(hash, table.length);
		for (Entry e = table[i]; e != null; e = e.next) {
			if (e.hash == hash && Arrays.equals(e.key.fState, key)) {
				return new InsertionResult(e.key, true);
			}
		}
		// not present
		State state = new State(key, hashCode);
		state.stateNumber = this.stateNumber++;
		modCount++;
		addEntry(hash, state, i);
		return new InsertionResult(state, false);

	}
	
	public InsertionResult old_putIfNotPresentSync(short[] key, int hashCode) {
		int hash = hash(hashCode);
		int i = indexFor(hash, table.length);
		rLock.lock();
		try {
			for (Entry e = table[i]; e != null; e = e.next) {
				if (e.hash == hash && Arrays.equals(e.key.fState, key)) {
					return new InsertionResult(e.key, true);
				}
			}
		} finally {
			rLock.unlock();
		}
		wLock.lock();
		try {
			i = indexFor(hash, table.length);
			// double check if someone else has added in the meanwhile
			for (Entry e = table[i]; e != null; e = e.next) {
				if (e.hash == hash && Arrays.equals(e.key.fState, key)) {
					return new InsertionResult(e.key, true);
				}
			}
			// not present
			State state = new State(key, hashCode);
			state.stateNumber = this.stateNumber++;
			modCount++;
			addEntry(hash, state, i);
			return new InsertionResult(state, false);
		} finally {
			wLock.unlock();
		}

	}

	public InsertionResult putIfNotPresentUnsync(short[] key, int hashCode) {
		int hash = hash(hashCode);
		int i = indexFor(hash, table.length);
		for (Entry e = table[i]; e != null; e = e.next) {
			if (e.hash == hash && Arrays.equals(e.key.fState, key)) {
				return new InsertionResult(e.key, true);
			}
		}
		// not present
		State state = new State(key, hashCode);
		state.stateNumber = this.stateNumber++;
		modCount++;
		addEntry(hash, state, i);
		return new InsertionResult(state, false);

	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is
	 * replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or
	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>. (A
	 *         <tt>null</tt> return can also indicate that the map previously
	 *         associated <tt>null</tt> with <tt>key</tt>.)
	 */
	private Object old_put(State key) {
		int hash = hash(key.hashCode());
		int i = indexFor(hash, table.length);
		// a state cannot be put twice
		/*
		 * for (Entry e = table[i]; e != null; e = e.next) { State k; if (e.hash ==
		 * hash && ((k = e.key) == key || key.equals(k))) { return null; } }
		 */
		modCount++;
		addEntry(hash, key, i);
		return null;
	}

	/**
	 * Rehashes the contents of this map into a new array with a larger
	 * capacity. This method is called automatically when the number of keys in
	 * this map reaches its threshold.
	 * 
	 * If current capacity is MAXIMUM_CAPACITY, this method does not resize the
	 * map, but sets threshold to Integer.MAX_VALUE. This has the effect of
	 * preventing future calls.
	 * 
	 * @param newCapacity
	 *            the new capacity, MUST be a power of two; must be greater than
	 *            current capacity unless current capacity is MAXIMUM_CAPACITY
	 *            (in which case value is irrelevant).
	 */
	void resize(int newCapacity) {
		Entry[] oldTable = table;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		Entry[] newTable = new Entry[newCapacity];
		transfer(newTable);
		table = newTable;
		threshold = (int) (newCapacity * loadFactor);
	}

	/**
	 * Transfers all entries from current table to newTable.
	 */
	void transfer(Entry[] newTable) {
		Entry[] src = table;
		int newCapacity = newTable.length;
		for (int j = 0; j < src.length; j++) {
			Entry e = src[j];
			if (e != null) {
				src[j] = null;
				do {
					Entry next = e.next;
					int i = indexFor(e.hash, newCapacity);
					e.next = newTable[i];
					newTable[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	private void old_clear() {
		modCount++;
		Entry[] tab = table;
		for (int i = 0; i < tab.length; i++)
			tab[i] = null;
		size = 0;
	}

	static class Entry {

		final State key;

		Entry next;

		// the map's hash
		final int hash;

		/**
		 * Creates new entry.
		 */
		Entry(int h, State k, Entry n) {
			next = n;
			key = k;
			hash = h;
		}

		public final State getKey() {
			return key;
		}

	}

	/**
	 * Adds a new entry with the specified key, value and hash code to the
	 * specified bucket. It is the responsibility of this method to resize the
	 * table if appropriate.
	 * 
	 * Subclass overrides this to alter the behavior of put method.
	 */
	void addEntry(int hash, State key, int bucketIndex) {
		Entry e = table[bucketIndex];
		table[bucketIndex] = new Entry(hash, key, e);
		if (size++ >= threshold)
			resize(2 * table.length);
	}

}
