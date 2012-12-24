package client;

import java.io.File;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import utils.CustomQueue;
import utils.ReadingThread;
import utils.SyncerUtils;
import utils.WritingThread;

/**
 * @author samato
 * 
 *         Handles syncing on client side
 */
public class ClientSideHandler {
	private static ClientConnection conn = null;
	private static ObjectOutputStream outfd = null;
	private static ObjectInputStream infd = null;
	private static File Dir;
	private static CustomQueue readQ = null;
	private static CustomQueue writeQ = null;

	/**
	 * Constructor: imports client connection class
	 * 
	 * @param _conn
	 */
	public ClientSideHandler(ClientConnection _conn, File _dir) {
		this.setConn(_conn);
		this.setDir(_dir);

	}

	/**
	 * 
	 */
	public void initialize() {
		setInFd();
		setOutFd();

		writeQ = new CustomQueue();
		readQ = new CustomQueue();

		ReadingThread readThread = new ReadingThread(writeQ, readQ, infd);
		WritingThread writeThread = new WritingThread(writeQ, outfd);

		new Thread(readThread).start();
		new Thread(writeThread).start();

		SyncerUtils sync = new SyncerUtils(Dir, readQ, writeQ);

		sync.startInitSync();
		sync.listener();

	}

	/**
	 * Getter for ClientConnection
	 * 
	 * @return
	 */
	public ClientConnection getConn() {
		return conn;
	}

	/**
	 * Setter for ClientConnection
	 * 
	 * @param _conn
	 */
	public void setConn(ClientConnection _conn) {
		conn = _conn;
	}

	/**
	 * Getter for ObjectOutputStream
	 * 
	 * @return
	 */
	public ObjectOutputStream getOutFd() {
		return outfd;
	}

	/**
	 * Setter for ObjectOutputStream
	 * 
	 */
	public void setOutFd() {
		try {
			outfd = new ObjectOutputStream(conn.getSock().getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Getter for ObjectInputStream
	 * 
	 * @return
	 */
	public ObjectInputStream getInFd() {
		return infd;
	}

	/**
	 * Setter for ObjectInputStream
	 * 
	 */
	public void setInFd() {
		try {
			infd = new ObjectInputStream(conn.getSock().getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getter for the directory
	 * 
	 * @return
	 */
	public File getDir() {
		return Dir;
	}

	/**
	 * setter for the directory
	 * 
	 * @param _dir
	 */
	public void setDir(File _dir) {
		Dir = _dir;
	}

	/**
	 * Elapsed time in milliseconds
	 * 
	 * @param startTime
	 * @param stopTime
	 * @return
	 */
	public long getElapsedTime(Long startTime, Long stopTime) {
		long elapsed;

		elapsed = (stopTime - startTime);

		return elapsed;
	}

}
