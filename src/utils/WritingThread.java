package utils;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author samato
 * 
 *         Writitng thread writes objects to the sockets ObjectOutputStream from
 *         the writeQ
 * 
 */
public class WritingThread implements Runnable {
	private static CustomQueue writeQ;
	ObjectOutputStream outFd;

	/**
	 * Constructor for WritingThread
	 * 
	 * @param _writeQ
	 * @param _outFd
	 */
	public WritingThread(CustomQueue _writeQ, ObjectOutputStream _outFd) {
		this.outFd = _outFd;
		this.setWriteQ(_writeQ);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// long start_time = System.currentTimeMillis();
		boolean run = true;
		while (run) {

			try {
				outFd.writeObject(writeQ.take());
				// start_time = System.currentTimeMillis();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("Problem with connection");
				run = false;
			}
			// } else if (timeout(start_time)) {
			/*
			 * try { outFd.writeObject(SyncerProtocols.HEARTBEAT); start_time =
			 * System.currentTimeMillis(); } catch (IOException e) { // TODO
			 * Auto-generated catch block // e.printStackTrace();
			 * System.out.println("Problem with connection"); run = false; }
			 */
			// }

		}
	}

	/**
	 * getter for the write queue
	 * 
	 * @return
	 */
	public static CustomQueue getWriteQ() {
		return writeQ;
	}

	/**
	 * setter for the write queue
	 * 
	 * @param _writeQ
	 */
	public void setWriteQ(CustomQueue _writeQ) {
		writeQ = _writeQ;
	}

	/*
	 * @param start_time
	 * 
	 * @return
	 */
	/*
	 * private boolean timeout(long start_time) { if
	 * ((System.currentTimeMillis() - start_time) > 10000) { return true; }
	 * 
	 * return false; }
	 */
}
