package utils;

import java.io.IOException;
import java.io.ObjectInputStream;

import utils.SyncerUtils.SyncerProtocols;

/**
 * @author samato
 * 
 *         ReadingThread reads objects from the ObjectInputStream and then puts
 *         it in the readQ for the main thread to see
 * 
 */
public class ReadingThread implements Runnable {

	private static CustomQueue writeQ;
	private static CustomQueue readQ;
	private static SyncerProtocols sProtocols;
	private static ObjectInputStream inFd;

	/**
	 * Constructor for ReadingThread
	 * 
	 * @param writeQueue
	 * @param readQueue
	 * @param _inFd
	 */
	public ReadingThread(CustomQueue writeQueue, CustomQueue readQueue,
			ObjectInputStream _inFd) {
		// TODO Auto-generated constructor stub
		this.setWriteQ(writeQueue);
		this.setReadQ(readQueue);
		this.setInFd(_inFd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean run = true;
		while (run) {
			Object obj = null;
			try {
				obj = getInFd().readObject();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("Problem with connection");
				run = false;
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Problem with object");
			}
			if (obj instanceof SyncerProtocols) {
				sProtocols = (SyncerProtocols) obj;

				switch (sProtocols) {

				case HEARTBEAT:
					System.out.println("HeartBeat Recieved");
					writeQ.add(SyncerProtocols.REPLY);
					break;
				case REPLY:
					System.out.println("Reply Recieved");
					break;
				default:
					readQ.add(sProtocols);
					break;
				}
			} else {
				if (obj != null)
					readQ.add(obj);
			}
		}

	}

	/**
	 * getter for writeQ
	 * 
	 * @return
	 */
	public static CustomQueue getWriteQ() {
		return writeQ;
	}

	/**
	 * setter for writeQ
	 * 
	 * @param _writeQ
	 */
	public void setWriteQ(CustomQueue _writeQ) {
		writeQ = _writeQ;
	}

	/**
	 * getter for readQ
	 * 
	 * @return
	 */
	public static CustomQueue getReadQ() {
		return readQ;
	}

	/**
	 * setter for the readQ
	 * 
	 * @param _readQ
	 */
	public void setReadQ(CustomQueue _readQ) {
		readQ = _readQ;
	}

	/**
	 * getter for the ObjectInputStream
	 * 
	 * @return
	 */
	public static ObjectInputStream getInFd() {
		return inFd;
	}

	/**
	 * setter for the ObjectInputStream
	 * 
	 * @param _inFd
	 */
	public void setInFd(ObjectInputStream _inFd) {
		inFd = _inFd;
	}
}
