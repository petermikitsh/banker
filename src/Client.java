/**
 * Clients.java
 * @authors mdy7643, pam3961, acc1728
 *
 */
public class Client extends Thread implements Comparable {

	private Banker banker;
	private int nUnits;
	private int nRequests;
	private long minSleepMillis;
	private long maxSleepMillis;
	
	public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis) {
		super(name);
		this.banker = banker;
		this.nUnits = nUnits;
		this.nRequests = nRequests;
		this.minSleepMillis = minSleepMillis;
		this.maxSleepMillis = maxSleepMillis;
	}
	
	/**
	 * Registers a claim for up to nUnits, and requests and releases resources
	 * nTimes.
	 */
	public void run() {
		banker.setClaim(nUnits);
		for (int i = 0; i <= nRequests; i++) {
			makeTransaction(i);
			if(banker.remaining() == 0) {
				break;
			}
			
			try {
				Thread.sleep((long) ((maxSleepMillis-minSleepMillis * Math.random()) + minSleepMillis));
			} catch (InterruptedException e) {
				System.err.println("InterruptedException in thread" + Thread.currentThread().getName());
			}
		}
		if (banker.allocated() > 0) {
			// TODO this we need to change to release only those that are allocated to this thread
			// otherwise we'll prematurely hit exit(1) condition since 99% of the time nUnits will always
			// be larger than the allocated units, unless of course we never are successfully completing requests
			// banker.release(nUnits); // old approach
			banker.release(banker.allocated());
		}
	}
	
	private void makeTransaction(int i) {
		if (banker.remaining() == 0) {
			banker.release(nUnits);
			return;
		} else {
			int randNUnits = (int)((banker.remaining() - 1) * Math.random()) + 1;
			banker.request(randNUnits);
		}
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
	
}
