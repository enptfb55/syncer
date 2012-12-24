package utils;

import java.io.File;
import java.util.Date;

import javax.swing.JFileChooser;

/**
 * @author samato
 * 
 *         File utilities
 */
public class FileUtils {

	String os = System.getProperty("os.name");

	/**
	 * Selects a file to send
	 * 
	 * @return
	 */
	public static File selectFile() {
		File fd = null;
		JFileChooser dialog = new JFileChooser();
		int retval = dialog.showOpenDialog(null);

		if (retval == JFileChooser.APPROVE_OPTION) {
			fd = dialog.getSelectedFile();
		} else {
			System.out.println("Failed to open file");
		}

		return fd;
	}

	/**
	 * Gui interface for selecting directory
	 * 
	 * @return
	 */
	public static File selectDir() {
		File dir = null;
		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int retval = dialog.showOpenDialog(null);

		if (retval == JFileChooser.APPROVE_OPTION) {
			dir = dialog.getSelectedFile();
		} else {
			System.out.println("Failed to open file");
		}

		return dir;
	}

	/**
	 * Iterate Directory
	 * 
	 * @param Dir
	 */
	public static void IterateDir(File Dir) {
		File[] listOfFiles = Dir.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.print("-" + listOfFiles[i].getName() + "\n");
			} else if (listOfFiles[i].isDirectory()) {
				System.out.print("d-" + listOfFiles[i].getName() + "\n");
				// IterateDir(listOfFiles[i]);
			}
		}
	}

	/**
	 * gets the file list of a directory
	 * 
	 * @param Dir
	 * @return
	 */
	public static String[] getFileList(File Dir) {
		File[] oldList = Dir.listFiles();
		String[] newList = new String[oldList.length];

		for (int i = 0; i < oldList.length; i++) {
			if (oldList[i].isFile())
				newList[i] = oldList[i].getName();
		}
		return newList;
	}

	/**
	 * gets the list of folders and files in a directory
	 * 
	 * @param Dir
	 * @return
	 */
	public static String[] getFolderList(File Dir) {
		File[] oldList = Dir.listFiles();
		String[] newList = new String[1];

		if (oldList != null) {
			newList = new String[oldList.length];

			int i, j;
			i = j = 0;

			while (i < oldList.length) {
				// TODO ignore files that begin with a period
				if (!oldList[i].equals(".DS_Store"))
					newList[j] = oldList[i].getName();
				j++;
				i++;
			}

		}
		return newList;

	}

	/**
	 * creates a local directory
	 * 
	 * @param Parent
	 * @param folderList
	 */
	public static void createDirsLocal(File Parent, String[] folderList) {
		for (int i = 0; i < folderList.length; i++) {
			if (folderList[i] != null) {
				File folder = new File(Parent, folderList[i]);
				if (!folder.exists()) {
					folder.mkdir();
				}
			}
		}
	}

	public static void createDir(File fd) {

		if (!fd.exists())
			fd.mkdir();
	}

	/**
	 * @param parent
	 * @param filename
	 */
	public static void create(File parent, String filename) {
		File fd = new File(parent, filename);

		if (!fd.exists())
			return;

	}

	/**
	 * deletes a file or recursively calls its self it is a directory
	 * 
	 * @param parent
	 * @param filename
	 */
	public static void delete(File parent, String filename) {

		File fd = new File(parent, filename);

		// Make sure the file or directory exists and isn't write protected
		if (!fd.exists())
			return;
		/*
		 * throw new IllegalArgumentException(
		 * "Delete: no such file or directory: " + fd.getName());
		 */

		if (!fd.canWrite())
			return;
		/*
		 * throw new IllegalArgumentException("Delete: write protected: " +
		 * fd.getName());
		 */

		// If it is a directory, make sure it is empty
		if (fd.isDirectory()) {
			String[] files = fd.list();
			for (int i = 0; i < files.length; i++) {
				delete(fd, files[i]);
			}
		}

		// Attempt to delete it
		boolean success = fd.delete();

		if (!success)
			return;
		/*
		 * throw new IllegalArgumentException("Delete: deletion failed");
		 */
	}

	/**
	 * changes files last modified time
	 * 
	 * @param file
	 * @param lastModifiedDate
	 */
	public static void changeFileLastModified(File file, Date lastModifiedDate) {
		file.setLastModified(lastModifiedDate.getTime());
	}
}
