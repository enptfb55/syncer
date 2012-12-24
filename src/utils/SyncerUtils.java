package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author samato
 * 
 *         SyncerUtils handles syncing operations for both sides of the
 *         connection
 * 
 */
public class SyncerUtils {

	private static CustomQueue readQ;
	private static CustomQueue writeQ;
	private static SyncerProtocols sProtocol;
	private static File Dir;
	public String remoteFileSeperator = String.valueOf(File.separatorChar);

	public static enum SyncerProtocols {
		FS, HEARTBEAT, REPLY, INIT, REQUEST_PATH, SENDING_PATH, REQUEST_DIR, SENDING_DIR, REQUEST_FILE, SENDING_FILE, TINI, DELETE, EOF, CREATED, RENAMED
	}

	/**
	 * constructer for SyncerUtils
	 * 
	 * @param _Dir
	 *            root directory for the connection
	 * @param _readQ
	 *            read queue for the connection
	 * @param _writeQ
	 *            write queue for the connection
	 */
	public SyncerUtils(File _Dir, CustomQueue _readQ, CustomQueue _writeQ) {
		this.setReadQ(_readQ);
		this.setWriteQ(_writeQ);
		this.setDir(_Dir);
		sProtocol = null;
	}

	/**
	 * sends the initialize command to remote thread this begins the syncing
	 * process
	 */
	public void startInitSync() {
		sProtocol = SyncerProtocols.INIT;
		writeQ.add(sProtocol);
	}

	/**
	 * sends the initialize command for corresponding thread that sent init
	 * command
	 */
	public void startTiniSync() {
		sProtocol = SyncerProtocols.TINI;
		writeQ.add(sProtocol);
	}

	public void getRemoteFileSeperator() {
		remoteFileSeperator = (String) readQ.take();

	}

	public void sendLocalFileSeperator() {
		String localFileSeperator = String.valueOf(File.separatorChar);
		writeQ.add(localFileSeperator);
	}

	/**
	 * listener waits for command from remote thread
	 */
	public void listener() {

		boolean initSync = false;
		boolean tiniSync = false;
		FileWatcher fw = new FileWatcher(this);

		while (true) {

			sProtocol = (SyncerProtocols) readQ.take();

			switch (sProtocol) {
			case FS:
				getRemoteFileSeperator();
				break;

			case INIT:
				writeQ.add(SyncerProtocols.FS);
				sendLocalFileSeperator();
				initSync = this.requestRemotePath(".", Dir);
				break;

			case REQUEST_PATH:
				this.sendLocalPath();
				break;

			case TINI:
				writeQ.add(SyncerProtocols.FS);
				sendLocalFileSeperator();
				tiniSync = this.requestRemotePath(".", Dir);
				break;

			case CREATED:
				getRemoteCreate();
				break;

			case RENAMED:
				getRemoteRename();
				break;

			case DELETE:
				getRemoteDelete();
				break;

			default:
				break;
			}

			if (initSync) {
				new Thread(fw).start();
				this.startTiniSync();

				initSync = false;
			}

			if (tiniSync) {
				new Thread(fw).start();
				tiniSync = false;
			}

		}
	}

	/**
	 * sends a local directory
	 * 
	 * @param localDir
	 *            specified local directory to send
	 */
	public void sendLocalDir(File localDir) {

		writeQ.add(SyncerProtocols.SENDING_DIR);

		writeQ.add(FileUtils.getFolderList(localDir));

	}

	/**
	 * sends local file
	 * 
	 * @param localFile
	 *            specified local file to send
	 */
	public void sendLocalFile(File localFile) {

		FileInputStream fstream = null;

		writeQ.add(SyncerProtocols.SENDING_FILE);

		if (!localFile.exists()) {
			// send error
			return;
		}
		try {
			fstream = new FileInputStream(localFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Long fileSize = localFile.length();

		byte[] buffer = new byte[1024];

		Integer bytesRead = 0;
		Integer totalBytesRead = 0;

		try {
			while ((bytesRead = fstream.read(buffer)) > 0) {

				totalBytesRead = totalBytesRead + bytesRead;

				writeQ.add(buffer);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		writeQ.add(SyncerProtocols.EOF);
	}

	/**
	 * gets requested path then calls sendLocalDir or sendLocalFile depending on
	 * whether it is a file or a directory
	 */
	public void sendLocalPath() {
		File localFile = null;
		String requested_path = null;

		requested_path = (String) readQ.take();

		if (requested_path.equals(".")) {
			localFile = Dir;
		} else {
			localFile = new File(Dir, requested_path);
		}

		if (localFile.exists()) {
			if (localFile.isDirectory()) {
				sendLocalDir(localFile);
			} else if (localFile.isFile()) {
				sendLocalFile(localFile);
			}

		}

	}

	/**
	 * request remote file
	 * 
	 * @param remoteFile
	 *            path of remote file
	 * @param LocalDir
	 *            File object of the new local file
	 * @return
	 */
	public boolean requestRemoteFile(String remoteFile, File LocalDir) {

		FileOutputStream fstream = null;

		try {
			fstream = new FileOutputStream(LocalDir);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer bytesRead = 0;
		byte[] buffer = new byte[1024];

		while (true) {
			Object obj = readQ.take();

			if (obj instanceof byte[]) {
				buffer = (byte[]) obj;
			} else if (obj instanceof SyncerProtocols) {
				break;
			}

			bytesRead = buffer.length;
			try {
				fstream.write(buffer, 0, bytesRead);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// e.printStackTrace();
			}

		}

		try {
			fstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {

		}

		return true;
	}

	/**
	 * request and creates remote directory localy
	 * 
	 * @param remoteDir
	 *            path of the remote directory
	 * @param LocalDir
	 *            File object of the new local directory
	 * @return true false based on sucess
	 */
	public boolean requestRemoteDir(String remoteDir, File LocalDir) {

		FileUtils.createDir(LocalDir);

		String[] RemoteDir = null;

		RemoteDir = getRemoteDir();

		for (int i = 0; i < RemoteDir.length; i++) {
			if (RemoteDir[i] != null)
				requestRemotePath(remoteDir + remoteFileSeperator
						+ (RemoteDir[i]), new File(LocalDir, RemoteDir[i]));
		}
		return true;

	}

	/**
	 * requests remote path then based on whether it is a Directory or a file it
	 * calles requestRemoteDir or requestRemoteFile
	 * 
	 * @param remotePath
	 * @param LocalDir
	 * @return
	 */
	public boolean requestRemotePath(String remotePath, File LocalDir) {
		if (!LocalDir.isDirectory() & LocalDir.exists())
			return true;

		writeQ.add(SyncerProtocols.REQUEST_PATH);
		writeQ.add(remotePath);

		sProtocol = (SyncerProtocols) readQ.take();

		if (sProtocol == SyncerProtocols.SENDING_DIR) {
			return requestRemoteDir(remotePath, LocalDir);
		} else if (sProtocol == SyncerProtocols.SENDING_FILE) {
			return requestRemoteFile(remotePath, LocalDir);
		}

		return true;
	}

	/**
	 * sends delete command and then sends the path of the remote path to delete
	 * 
	 * @param remotePath
	 */
	public void deleteRemote(String remotePath) {

		writeQ.add(SyncerProtocols.DELETE);

		writeQ.add(remotePath);

	}

	/**
	 * sends create command and then sends path to be requested on remote end
	 * 
	 * @param remotePath
	 */
	public void createRemote(String remotePath) {
		writeQ.add(SyncerProtocols.CREATED);

		writeQ.add(remotePath);
	}

	/**
	 * sends renamed command then send the old remote path and the new remote
	 * path
	 * 
	 * @param oldRemotePath
	 * @param newRemotePath
	 */
	public void renameRemote(String oldRemotePath, String newRemotePath) {
		writeQ.add(SyncerProtocols.RENAMED);

		writeQ.add(oldRemotePath);

		writeQ.add(newRemotePath);
	}

	/**
	 * gets the local path to delete
	 */
	public void getRemoteDelete() {

		String path = (String) readQ.take();

		FileUtils.delete(Dir, path);

	}

	/**
	 * gets remote path to create localy then calls the requestRemotePath
	 * function
	 */
	public void getRemoteCreate() {
		String remotePath = (String) readQ.take();
		File path = new File(Dir, remotePath);
		requestRemotePath(remotePath, path);
	}

	/**
	 * gets the path to be renamed
	 */
	public void getRemoteRename() {
		String oldRemotePath = (String) readQ.take();
		String newRemotePath = (String) readQ.take();
		File oldPath = new File(Dir, oldRemotePath);
		File newPath = new File(Dir, newRemotePath);
		if (newPath.exists())
			return;

		if (oldPath.exists())
			oldPath.renameTo(newPath);

	}

	/**
	 * gets the file list from the remote connection
	 * 
	 * @return
	 */
	public String[] getRemoteDir() {
		String[] filelist = null;

		filelist = (String[]) readQ.take();

		return filelist;
	}

	/**
	 * not used anymore
	 * 
	 * @param LocalDir
	 * @param RemoteDir
	 * @return
	 */
	public String[] compareDirs(String[] LocalDir, String[] RemoteDir) {
		String[] masterDir = null;
		String[] slaveDir = null;

		if (LocalDir.length < RemoteDir.length) {
			masterDir = RemoteDir;
			slaveDir = LocalDir;
		} else {
			masterDir = LocalDir;
			slaveDir = RemoteDir;
		}

		boolean found = false;
		for (int i = 0; i < slaveDir.length; i++) {
			for (int j = 0; j < masterDir.length; j++) {
				if (masterDir[j] == null) {
					break;
				}
				if (masterDir[j].equals(slaveDir[i])) {
					found = true;
					break;
				}
			}
			if (!found & slaveDir != null) {

				masterDir = insertIntoFileArray(masterDir, slaveDir[i]);
				found = false;
			}

		}
		return masterDir;

	}

	/**
	 * not used anymore
	 * 
	 * @param list
	 * @param file
	 * @return
	 */
	public String[] insertIntoFileArray(String[] list, String file) {
		String[] newList = Arrays.copyOf(list, list.length + 1);
		newList[newList.length - 1] = file;
		return newList;
	}

	/**
	 * gets read queue
	 * 
	 * @return
	 */
	public static CustomQueue getReadQ() {
		return readQ;
	}

	/**
	 * sets read queue
	 * 
	 * @param _readQ
	 */
	public void setReadQ(CustomQueue _readQ) {
		readQ = _readQ;
	}

	/**
	 * gets write queue
	 * 
	 * @return
	 */
	public CustomQueue getWriteQ() {
		return writeQ;
	}

	/**
	 * sets the write queue
	 * 
	 * @param _writeQ
	 */
	public void setWriteQ(CustomQueue _writeQ) {
		writeQ = _writeQ;
	}

	/**
	 * get directory
	 * 
	 * @return
	 */
	public File getDir() {
		return Dir;
	}

	/**
	 * set directory
	 * 
	 * @param dir
	 */
	public void setDir(File dir) {
		Dir = dir;
	}

}
