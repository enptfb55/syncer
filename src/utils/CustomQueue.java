package utils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author samato
 * 
 *         CustomQueue is basically used as a interface to the
 *         ArrayBlockingQueue
 * 
 *         TODO: needs to be changed to extends ArrayBlockingQueue
 */
public class CustomQueue {
	private ArrayBlockingQueue<Object> queue = null;
	private static Lock lock = new ReentrantLock();
	private static Condition newObjectInQueue = lock.newCondition();

	/**
	 * Constructor for CustomQueue creates the ArrayBlockingQueue size 10000000
	 */
	public CustomQueue() {
		this.queue = new ArrayBlockingQueue<Object>(1000);

	}

	/**
	 * @return gets the queue
	 */
	public Queue<Object> getQueue() {
		return queue;
	}

	/**
	 * sets the queue
	 * 
	 * @param _queue
	 */
	public void setQueue(ArrayBlockingQueue<Object> _queue) {
		queue = _queue;
	}

	/**
	 * getter for the lock
	 * 
	 * @return
	 */
	public static Lock getLock() {
		return lock;
	}

	/**
	 * setter for the lock
	 * 
	 * @param _lock
	 */
	public static void setLock(Lock _lock) {
		lock = _lock;
	}

	/**
	 * getter for the condition
	 * 
	 * @return
	 */
	public static Condition getNewObjectInQueue() {
		return newObjectInQueue;
	}

	/**
	 * setter for the condition
	 * 
	 * @param _newObjectInQueue
	 */
	public static void setNewObjectInQueue(Condition _newObjectInQueue) {
		newObjectInQueue = _newObjectInQueue;
	}

	/**
	 * adds the object to the queue
	 * 
	 * @param obj
	 */
	public void add(Object obj) {
		lock.lock();

		queue.add(obj);
		newObjectInQueue.signalAll();

		lock.unlock();

	}

	/**
	 * waits till there is an object in the poll and then polls it
	 * 
	 * @return
	 */
	public Object waitAndPoll() {
		if (this.queue.isEmpty())
			this.waitForNewObject();

		return this.poll();

	}

	/**
	 * polls an object from the queue
	 * 
	 * @return
	 */
	public Object poll() {
		lock.lock();

		Object obj = this.queue.poll();

		lock.unlock();

		return obj;
	}

	/**
	 * polls an object from the queue timesout
	 * 
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public Object poll(long timeout, TimeUnit unit) {
		try {
			return queue.poll(timeout, unit);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * take waits till a new object is added then polls it
	 * 
	 * @return
	 */
	public Object take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * wait for new object
	 * 
	 * @return
	 */
	public boolean waitForNewObject() {
		lock.lock();
		try {
			newObjectInQueue.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return false;
		} finally {
			lock.unlock();
		}
		return true;
	}
}
