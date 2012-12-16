/**
 * Banker.java
 * @authors mdy7643, pam3961, acc1728
 *
 */
public class Banker {

	private int nUnits;
	
	public Banker(int nUnits) {
		this.nUnits = nUnits;
	}
	
	/**
	 * The current thread attempts to register a claim for up to nUnits of resource.
	 */
	public void setClaim(int nUnits) {}
	
	/**
	 * The current thread requests nUnits more resources.
	 * @return True if request is successful.
	 */
	public boolean request(int nUnits) {
		return true;
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
	
}
