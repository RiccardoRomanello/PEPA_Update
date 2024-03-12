package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * 
 * @author Giacomo Alzetta
 *
 * @param <S> The type of single state.
 */
public class Aggregated<S extends Comparable<S>>
	implements Iterable<S>, Comparable<Aggregated<S>>, List<S>{

	private Set<S> internalStates = new HashSet<S>();
	private S representative = null;
	private int hash = -1;
	
	
	public Aggregated(Iterable<S> states) {
		internalStates = new HashSet<>();
		representative = null;
		for (S state: states) {
			add(state);
		}
	}
	public S getRepresentative() {
		return representative;
	}
	
	@Override
	public int size() {
		return internalStates.size();
	}
	
	@Override
	public boolean add(S state) {
		hash = -1;
		if (representative == null || state.compareTo(representative) < 0) {
			representative = state;
		}
		internalStates.add(state);
		
		return true;
	}
	
	public boolean contains(S state) {
		return internalStates.contains(state);
	}
	
	public boolean contains(Object s) {
		return internalStates.contains(s);
	}
	
	@Override
	public Iterator<S> iterator() {
		return internalStates.iterator();
	}

	@Override
	public int compareTo(Aggregated<S> o) {
		// FIXME: is this correct? 
		return getRepresentative().compareTo(o.getRepresentative());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Aggregated)) {
			return false;
		}
		
		Aggregated<S> oo = (Aggregated<S>) o;
		return internalStates.equals(oo.internalStates);
	}
	
	@Override
	public int hashCode() {
		if (hash == -1) {
			hash = internalStates.hashCode();
		}
		return hash;
	}
	
	@Override
	public String toString() {
		return "Aggregated(" + internalStates.toString() + ")";
	}
	
	@Override
	public boolean isEmpty() {
		return internalStates.isEmpty();
	}
	
	@Override
	public Object[] toArray() {
		Object[] arr = new Object[internalStates.size()];
		int i=0;
		for (S state: internalStates) {
			arr[i] = state;
		}
		
		return arr;
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(Collection<? extends S> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(int index, Collection<? extends S> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public S get(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public S set(int index, S element) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void add(int index, S element) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public S remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ListIterator<S> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ListIterator<S> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<S> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}
