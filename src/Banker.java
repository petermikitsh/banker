import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Banker.java
 * @authors mdy7643, pam3961, acc1728
 *
 */
public class Banker {

	private final String ALLOCATED = "allocated";
	private final String REMAINING = "remaining";
	private final String CURRENT   = "current_claim";
	
	private int nUnits;
	private Map<Thread, Map<String, Integer>> threadMap = new TreeMap<Thread, Map<String, Integer>>();
	
	public Banker(int nUnits) {
		this.nUnits = nUnits;
	}
	
	/**
	 * The current thread attempts to register a claim for up to nUnits of resource.
	 */
	public void setClaim(int nUnits) {
		Thread currentThread = Thread.currentThread();
		
		if(threadMap.containsKey(currentThread) || !(nUnits > 0) || nUnits > this.nUnits) {
			System.exit(1);
		}
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put(ALLOCATED, 0);
		map.put(CURRENT, nUnits);
		map.put(REMAINING, nUnits);
		threadMap.put(currentThread, map);
		
		System.out.printf("Thread %s sets a claim for %s units.", currentThread.getName(), nUnits);
		
		return;
	}
	
	/**
	 * The current thread requests nUnits more resources.
	 * @return True if request is successful.
	 */
	public boolean request(int nUnits) {
		
		Thread currentThread = Thread.currentThread();
		
		if(!threadMap.containsKey(currentThread) || !(nUnits > 0) || nUnits > threadMap.get(currentThread).get(REMAINING)) {
			System.exit(1);
		}
		
		System.out.printf("Thread %s requests %s units.", currentThread.getName(), nUnits);
		
		if(isStateSafe(nUnits, Collections.unmodifiableMap(threadMap))) {
			System.out.printf("Thread %s has %s units allocated.", currentThread.getName(), nUnits);
			int allocated = threadMap.get(currentThread).get(ALLOCATED);
			int remaining = threadMap.get(currentThread).get(REMAINING);
			threadMap.get(currentThread).put(ALLOCATED, allocated + nUnits);
			threadMap.get(currentThread).put(REMAINING, remaining - nUnits);
			
			return true;
		} else {
			System.out.printf("Thread %s waits.", currentThread.getName());
			//TODO add false condition
			return false;
		}

		
	}
	
	/**
	 * The current thread releases nUnits resources.
	 */
	public void release(int nUnits) {}
	
	/**
	 * @return The number of units allocated to the current thread
	 */
	public int allocated() {
		return 0;
	}
	
	/**
	 * @return The number of units remaining in the current thread's claim.
	 */
	public int remaining() {
		return 0;
	}
	
	private boolean isStateSafe(int numberOfUnitsOnHand, Map<Thread, Map<String, Integer>> map) {
		Map<Thread, Map<String, Integer>> sortedMap = sortMap(map);
		
		// This is sorted according to remaining units in ascending order.
		for(Map<String, Integer> threadDetail : sortedMap.values()) {
			if(threadDetail.get(REMAINING) > numberOfUnitsOnHand) {
				return false;
			}
			
			numberOfUnitsOnHand += threadDetail.get(ALLOCATED);
		}
		
		return true;
	}
	
	/**
	 * Sorts an unsorted map according to remaining units.
	 * @param unsortedMap The map to sort. Must be made final to access it within the inner class.
	 * @return A sorted version of this unsortedMap based on remaining units.
	 */
	private <K, M, V extends Comparable<V>> Map<K, Map<M, V>> sortMap(final Map<K, Map<M, V>> unsortedMap) {
		// Comparator only works on the key set for TreeMap. So when we compare 2 keys, we'll
		// need to get the value using unsortedMap.get(key)
		Comparator<K> comparator =  new Comparator<K>() {
		    public int compare(K obj1, K obj2) {
		    	// Sort uisng the remaining units.
		    	return unsortedMap.get(obj1).get(REMAINING).compareTo(unsortedMap.get(obj2).get(REMAINING));
		    }
		};
		
		Map<K, Map<M, V>> sortedMap = new TreeMap<K, Map<M, V>>(comparator);
		sortedMap.putAll(unsortedMap);
		
		return sortedMap;
	}
}
