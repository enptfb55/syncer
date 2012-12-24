package utils;

import java.io.File;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

/**
 * @author samato
 * 
 *         Interface for Jnotify
 * 
 */
public class FileWatcher implements Runnable {
	private static File Directory;
	private int watchId;
	private SyncerUtils sync;

	/**
	 * Constructor for FileWatcher
	 * 
	 * @param _sync
	 */
	public FileWatcher(SyncerUtils _sync) {
		sync = _sync;
		setDirectory(sync.getDir());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.setupJNotify();

		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		removeJNotify();

	}

	/**
	 * starts the Listener
	 * 
	 * creates the watchId
	 */
	public void setupJNotify() {
		int mask = JNotify.FILE_ANY;
		boolean watchSubtree = true;

		try {
			watchId = JNotify.addWatch(Directory.getPath(), mask, watchSubtree,
					new Listener());
		} catch (JNotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Stops the Listener wit the watchId
	 */
	public void removeJNotify() {
		boolean res = false;
		try {
			res = JNotify.removeWatch(watchId);
		} catch (JNotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!res) {
			// invalid watch ID specified.
		}
	}

	/**
	 * getter for the directory
	 * 
	 * @return
	 */
	public static File getDirectory() {
		return Directory;
	}

	/**
	 * setter for the directory
	 * 
	 * @param directory
	 */
	public static void setDirectory(File directory) {
		Directory = directory;
	}

	/**
	 * @author samato
	 * 
	 *         Listener for Jnotify
	 * 
	 */
	private class Listener implements JNotifyListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see net.contentobjects.jnotify.JNotifyListener#fileRenamed(int,
		 * java.lang.String, java.lang.String, java.lang.String)
		 */
		public void fileRenamed(int wd, String rootPath, String oldName,
				String newName) {
			print("renamed " + rootPath + " : " + oldName + " -> " + newName);
			sync.renameRemote("." + sync.remoteFileSeperator + oldName, "."
					+ sync.remoteFileSeperator + newName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.contentobjects.jnotify.JNotifyListener#fileModified(int,
		 * java.lang.String, java.lang.String)
		 */
		public void fileModified(int wd, String rootPath, String name) {
			print("modified " + rootPath + " : " + name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.contentobjects.jnotify.JNotifyListener#fileDeleted(int,
		 * java.lang.String, java.lang.String)
		 */
		public void fileDeleted(int wd, String rootPath, String name) {
			print("deleted " + rootPath + " : " + name);
			sync.deleteRemote("." + sync.remoteFileSeperator + name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.contentobjects.jnotify.JNotifyListener#fileCreated(int,
		 * java.lang.String, java.lang.String)
		 */
		public void fileCreated(int wd, String rootPath, String name) {
			print("created " + rootPath + " : " + name);
			sync.createRemote("." + sync.remoteFileSeperator + name);
		}

		/**
		 * @param msg
		 */
		void print(String msg) {
			System.err.println(msg);
		}
	}
}
