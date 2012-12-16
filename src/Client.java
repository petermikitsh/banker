/**
 * Clients.java
 * @authors mdy7643, pam3961, acc1728
 *
 */
public class Client extends Thread {

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
			try {
				Thread.sleep((long) ((maxSleepMillis-minSleepMillis * Math.random()) + minSleepMillis));
			} catch (InterruptedException e) {
				System.err.println("InterruptedException in thread" + Thread.currentThread().getName());
			}
		}
		if (banker.allocated() > 0) {
			banker.release(nUnits);
		}
	}
	
	private void makeTransaction(int i) {
		if (banker.remaining() == 0) {
			banker.release(nUnits);
			return;
		} if (i % 2 == 0) {
			banker.request(nUnits);
		} else { 
			banker.release(nUnits);
		}
	}
	
}
