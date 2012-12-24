package client;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import utils.FileUtils;

/**
 * @author samato
 * 
 */
public class ClientSync extends JFrame {

	private static final long serialVersionUID = 1L;
	static int port = 2525;
	private static File Dir = new File(
			"/Users/samato/Documents/workspace/syncer/RemoteDir");
	private static boolean sts = false;
	private static ClientConnection conn;
	private static String hostName = "localhost";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientSync clntSync = new ClientSync();
		ClientSideHandler handle = null;
		boolean run = true;

		while (run) {
			if (clntSync.isSts()) {
				clntSync.setConn(new ClientConnection(hostName, port));

				while (conn.getSock() == null) {

				}
				run = false;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		handle = new ClientSideHandler(conn, Dir);

		handle.initialize();

	}

	/**
	 * 
	 */
	public ClientSync() {
		setup();
	}

	/**
	 * 
	 */
	void setup() {
		boolean isVis = true;

		this.setSize(300, 300);
		this.setTitle("ClientSyncer");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel p1 = new JPanel(new GridLayout(2, 1, 2, 2));
		p1.add(new JLabel("Server hostname"));
		final JTextField textfld1 = new JTextField("localhost");
		p1.add(textfld1);
		p1.add(new JLabel("Server port"));
		final JTextField textfld2 = new JTextField("2525");
		p1.add(textfld2);
		p1.setBorder(new TitledBorder("Server settings"));

		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		p2.add(new JLabel("Folder To Sync"));
		JButton browse = new JButton("browse");
		p2.add(browse);
		p2.setBorder(new TitledBorder("Folder settings"));

		this.setLayout(new GridLayout(3, 2, 2, 2));
		this.add(p1);
		this.add(p2);
		JButton done = new JButton("Done");
		this.add(done);
		this.setVisible(isVis);

		browse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				setDir(FileUtils.selectDir());
			}
		});

		done.addActionListener(new ActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				hostName = textfld1.getText();
				port = Integer.valueOf(textfld2.getText());

				if (port != 0 && getDir() != null) {
					System.out.println("The port is " + port);
					System.out.println("The Directory is "
							+ getDir().getAbsolutePath());
					close();
				} else {
					System.out
							.println("Make sure to add a port and a Directory");
				}

				return;
			}
		});

	}

	/**
	 * 
	 */
	public void close() {
		this.dispose();
		setSts(true);
	}

	/**
	 * @return
	 */
	public File getDir() {
		return Dir;
	}

	/**
	 * @param dir
	 */
	public void setDir(File dir) {
		Dir = dir;
	}

	/**
	 * @return
	 */
	public static ClientConnection getConn() {
		return conn;
	}

	/**
	 * @param _conn
	 */
	public void setConn(ClientConnection _conn) {
		conn = _conn;
	}

	/**
	 * @return
	 */
	public boolean isSts() {
		return sts;
	}

	/**
	 * @param sts
	 */
	public static void setSts(boolean sts) {
		ClientSync.sts = sts;
	}
}
