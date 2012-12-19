import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

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
	private Map<String, Map<String, Integer>> threadMap = new ConcurrentHashMap<String, Map<String, Integer>>();
	
	public Banker(int nUnits) {
		this.nUnits = nUnits;
	}
	
	/**
	 * The current thread attempts to register a claim for up to nUnits of resource.
	 */
	public void setClaim(int nUnits) {
		Thread currentThread = Thread.currentThread();
		
		
		
		if(threadMap.get(currentThread.getName()) != null || !(nUnits > 0) || nUnits > this.nUnits) {
			System.err.println("exited3");
			System.exit(1);
		}
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put(ALLOCATED, 0);
		map.put(CURRENT, nUnits);
		map.put(REMAINING, nUnits);
		
		threadMap.put(currentThread.getName(), map);
		
		System.out.printf("Thread %s sets a claim for %s units.\n", currentThread.getName(), nUnits);
		
		return;
	}
	
	/**
	 * The current thread requests nUnits more resources.
	 * @return True if request is successful.
	 */
	public boolean request(int nUnits) {
		
		Thread currentThread = Thread.currentThread();
		
		if(!threadMap.containsKey(currentThread.getName()) || !(nUnits > 0) || nUnits > threadMap.get(currentThread.getName()).get(REMAINING)) {
			System.err.println("exited1");
			System.exit(1);
		}
		
		System.out.printf("Thread %s requests %s units.\n", currentThread.getName(), nUnits);
				
		if(isStateSafe(this.nUnits, Collections.unmodifiableMap(threadMap))) {
			System.out.printf("Thread %s has %s units allocated.\n", currentThread.getName(), nUnits);
			int allocated = threadMap.get(currentThread.getName()).get(ALLOCATED);
			int remaining = threadMap.get(currentThread.getName()).get(REMAINING);
			threadMap.get(currentThread.getName()).put(ALLOCATED, allocated + nUnits);
			threadMap.get(currentThread.getName()).put(REMAINING, remaining - nUnits);
			
			synchronized(currentThread) {
				this.nUnits -= nUnits;
			}
			
			return true;
		}
		
			while(!isStateSafe(this.nUnits, Collections.unmodifiableMap(threadMap))) {
				System.out.printf("Thread %s waits.\n", currentThread.getName());
				try {
					synchronized(currentThread) {
						currentThread.wait();
					}
				} catch (InterruptedException ie) {
					System.err.println("Error: " + ie.getMessage() );
				}
			}
			
			///notifyAll();
			
			System.out.printf("Thread %s awakened.\n", currentThread.getName());
			
			//request(nUnits);
			return request(nUnits);
		

		
	}
	
	/**
	 * The current thread releases nUnits resources.
	 */
	public synchronized void release(int nUnits) {
		Thread currentThread = Thread.currentThread();
		
		if(!threadMap.containsKey(currentThread.getName()) || !(nUnits > 0) || nUnits > threadMap.get(currentThread.getName()).get(ALLOCATED)) {
			System.err.println("exited2");
			System.exit(1);
		}
		
		System.out.printf("Thread %s releases %s units.\n", currentThread.getName(), nUnits);
		
		int allocated = threadMap.get(currentThread.getName()).get(ALLOCATED);
		threadMap.get(currentThread.getName()).put(ALLOCATED, allocated - nUnits);
		
		synchronized(currentThread) {
			this.nUnits += nUnits;
		}
		
		// TODO do we need to do anything for remaining? i don't think so.
		
		notifyAll();
		
		return;
	}
	
	/**
	 * @return The number of units allocated to the current thread
	 */
	public int allocated() {
		Thread currentThread = Thread.currentThread();
		return threadMap.get(currentThread.getName()).get(ALLOCATED);
	}
	
	/**
	 * @return The number of units remaining in the current thread's claim.
	 */
	public int remaining() {
		Thread currentThread = Thread.currentThread();
		return threadMap.get(currentThread.getName()).get(REMAINING);
	}
	
	private boolean isStateSafe(int numberOfUnitsOnHand, Map<String, Map<String, Integer>> map) {
		Map<String, Map<String, Integer>> sortedMap = sortMap(map);
		
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
	private Map<String, Map<String, Integer>> sortMap(final Map<String, Map<String, Integer>> unsortedMap) {
		// Comparator only works on the key set for TreeMap. So when we compare 2 keys, we'll
		// need to get the value using unsortedMap.get(key)
		Comparator<String> comparator =  new Comparator<String>() {
		    public int compare(String obj1, String obj2) {
		    	// Sort using the remaining units.
		    	Integer obj1_remaining = unsortedMap.get(obj1).get(REMAINING);
		    	Integer obj2_remaining = unsortedMap.get(obj2).get(REMAINING);
		    	
		    	return obj1_remaining.compareTo(obj2_remaining) == 0 ? 1 : obj1_remaining.compareTo(obj2_remaining);
		    }
		};
		
		Map<String, Map<String, Integer>> sortedMap = new TreeMap<String, Map<String, Integer>>(comparator);
		sortedMap.putAll(unsortedMap);
		
		return sortedMap;
	}
}
