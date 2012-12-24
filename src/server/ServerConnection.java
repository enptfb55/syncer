package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author samato
 * 
 *         Server Connection
 */
public class ServerConnection extends Thread {

	private static int port;
	private static File Dir;
	private static ServerSocket serverSock = null;

	/**
	 * Constructor that creates a server on specified port
	 * 
	 * @param _port
	 */
	public ServerConnection(int _port, File _dir) {
		this.setPort(_port);
		this.setDir(_dir);
		this.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		try {
			int ClientNo = 1;
			System.out.println("Listening on port " + port);
			serverSock = new ServerSocket(getPort());

			while (true) {
				Socket sock = serverSock.accept();
				System.out.println("Connection has been established on "
						+ this.getPort() + " with ip " + sock.getInetAddress());

				ServerSideHandle handle = new ServerSideHandle(sock, Dir);

				System.out.println("Starting thread for client " + ClientNo);
				new Thread(handle).start();

				ClientNo++;

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getter for port
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * setter for port
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		ServerConnection.port = port;
	}

	/**
	 * @return
	 */
	public static File getDir() {
		return Dir;
	}

	/**
	 * @param dir
	 */
	public void setDir(File dir) {
		Dir = dir;
	}

}
