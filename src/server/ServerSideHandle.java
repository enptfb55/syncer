package server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

import utils.CustomQueue;

import utils.ReadingThread;
import utils.SyncerUtils;
import utils.WritingThread;

/**
 * @author samato
 * 
 *         Handles syncing on the server side
 */
public class ServerSideHandle implements Runnable {

	private static Socket sock = null;
	private static ObjectOutputStream outFd = null;
	private static ObjectInputStream inFd = null;
	private static CustomQueue writeQ = null;

	private static File Dir = null;
	private static CustomQueue readQ;
	private static SyncerUtils sync = null;

	/**
	 * Constructor: Imports server connection
	 * 
	 * @param _conn
	 */
	public ServerSideHandle(Socket _sock, File _Dir) {
		// TODO Auto-generated constructor stub
		setSock(_sock);
		setDir(_Dir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		initialize();
	}

	/**
	 * Initializes this threads queues, Fd's and then starts the reading and
	 * writing threads then starts the sync listener
	 */
	public void initialize() {
		writeQ = new CustomQueue();
		readQ = new CustomQueue();

		setOutFd();
		setInFd();

		WritingThread writeThread = new WritingThread(writeQ, outFd);
		ReadingThread readThread = new ReadingThread(writeQ, readQ, inFd);

		new Thread(writeThread).start();
		new Thread(readThread).start();

		sync = new SyncerUtils(Dir, readQ, writeQ);

		sync.listener();

	}

	/**
	 * getter for the socket fd
	 * 
	 * @return
	 */
	public static Socket getSock() {
		return sock;
	}

	/**
	 * setter for the socket
	 * 
	 * @param _sock
	 */
	public static void setSock(Socket _sock) {
		sock = _sock;
	}

	/**
	 * getter for the out fd
	 * 
	 * @return
	 */
	public static ObjectOutputStream getOutFd() {
		return outFd;
	}

	/**
	 * setter for the out fd no need for input, uses the classes socket
	 */
	public static void setOutFd() {
		try {
			outFd = new ObjectOutputStream(getSock().getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getter for the in fd
	 * 
	 * @return
	 */
	public static ObjectInputStream getInFd() {
		return inFd;
	}

	/**
	 * setter for the in fd no need for input, uses the classes socket
	 */
	public static void setInFd() {
		try {
			inFd = new ObjectInputStream(getSock().getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getter for directory
	 * 
	 * @return
	 */
	public static File getDir() {
		return Dir;
	}

	/**
	 * setter for the directory
	 * 
	 * @param dir
	 */
	public static void setDir(File dir) {
		Dir = dir;
	}

}
