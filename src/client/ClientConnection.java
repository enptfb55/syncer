package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author samato
 * 
 *         Client connection to the server
 */
public class ClientConnection {
	private String host_name;
	private int known_port;
	private static Socket sock = null;

	/**
	 * @param _host_name
	 * @param _known_port
	 */
	public ClientConnection(String _host_name, int _known_port) {
		this.setHost_name(_host_name);
		this.setKnown_port(_known_port);
		run();

	}

	/**
	 * 
	 */
	public void run() {
		try {
			setSock(new Socket(this.getHost_name(), this.getKnown_port()));
			System.out.println("Sucsessfull connection to "
					+ getSock().getInetAddress() + " on port "
					+ getSock().getPort());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public String getHost_name() {
		return host_name;
	}

	/**
	 * @param host_name
	 */
	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	/**
	 * @return
	 */
	public int getKnown_port() {
		return known_port;
	}

	/**
	 * @param known_port
	 */
	public void setKnown_port(int known_port) {
		this.known_port = known_port;
	}

	/**
	 * @return
	 */
	public Socket getSock() {
		return sock;
	}

	/**
	 * @param _sock
	 */
	public static void setSock(Socket _sock) {
		sock = _sock;
	}

}
