/**
 * Driver.java
 * @authors mdy7643, pam3961, acc1728
 * 
 */
public class Driver {
	
	// TODO Choose better default values?
	
	// Number of resources for the Banker.
	private final static int nBUnits = 8;
	// Number of Clients.
	private final static int nClients = 3;
	// Max number of resources to register for each Client.
	private final static int maxNCUnits = 6;
	// Number of requests for each Client.
	private final static int nRequests = 2;
	// Minimum number of milliseconds for each client to sleep.
	private final static long minSleepMillis = 1000;
	// Maximum number of milliseconds for each client to sleep.
	private final static long maxSleepMillis = 3000;
	
	public static void main(String[] args) {
		Client[] clients = new Client[nClients];
		
		// Create the banker.
		Banker banker = new Banker(nBUnits);
		
		// Create each of the clients and start them.
		for (int i = 0; i < nClients; i++) {

			clients[i] = ( new Client ( "Client " + (i + 1),
					banker,
					(int)((maxNCUnits - 1) * Math.random()) + 1,
					nRequests,
					minSleepMillis,
					maxSleepMillis
					) );
			
			clients[i].start();
		}
		
		// Wait for the clients to finish.
		for (int j = 0; j < clients.length; j++){
			try{
				clients[j].join();
			} catch (InterruptedException ie) {
				System.err.println("Error: " + ie.getMessage());
			}
		}
	}
	
}
