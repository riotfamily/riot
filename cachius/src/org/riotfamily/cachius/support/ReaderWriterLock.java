package org.riotfamily.cachius.support;

import java.util.LinkedList;

/**
 * Class implementing a reader/writer lock with FIFO ordering.
 *
 * @author Felix Gnass
 */
public class ReaderWriterLock {

    private int activeReaders;
    private int waitingReaders;
    private int activeWriters;

    private final LinkedList writerLocks = new LinkedList();

    /**
     * Aquire a read lock. A call to this method will block until the
     * current writer (if any) has finished.
     */ 
    public synchronized void lockForReading() {
        if (activeWriters == 0 && writerLocks.size() == 0) {
            ++activeReaders;
        }
        else {
            ++waitingReaders;
            try { 
                wait(); 
            }
            catch (InterruptedException e) {
            }
        }
    }


    /**
     * Request a reader lock.
     *
     * @return true if you can safely read from the source
     *         false otherwise
     */
    public synchronized boolean tryLockForReading() {
        if (activeWriters == 0 && writerLocks.size() == 0) {
            ++activeReaders;
            return true;
        }
        return false;
    }


    /**
     * Release the read lock.
     */
    public synchronized void releaseReaderLock() {
        if (--activeReaders == 0) {
            notifyFirstWriter();
        }
    }


    /**
     * Request the write lock. The call blocks until a write operation 
     * can be performed safely. Write requests are guaranteed to be executed 
     * in the order received (FIFO). Pending read requests take precedence 
     * over all write requests.
     */
    public void lockForWriting() {
        Object lock = new Object();
        synchronized (lock) {
            synchronized (this) {
                boolean canWrite = writerLocks.size() == 0 
                        && activeReaders == 0 && activeWriters == 0;
                        
                if (canWrite) {
                    ++activeWriters;
                    return;
                }
                writerLocks.addLast(lock);
            }
            try { 
                lock.wait(); 
            } 
            catch (InterruptedException e) {
            }
        }
    }


    /**
     * Aquire a write lock (non-blocking). 
     */
    public synchronized boolean tryLockForWriting() {
        if (writerLocks.size() == 0  && activeReaders == 0 
                && activeWriters == 0) {
            
            ++activeWriters;
            return true;
        }
        return false;
    }



    /**
     * Release the write lock.
     */
    public synchronized void releaseWriterLock() {
        --activeWriters;
        if (waitingReaders > 0) {
            notifyReaders();
        }
        else {
            notifyFirstWriter();
        }
    }


    /**
     * Notify all threads waiting for read access.
     */
    private void notifyReaders() {
        activeReaders  += waitingReaders;
        waitingReaders = 0;
        notifyAll();
    }

    /**
     * Notify the first (oldest) writer.
     */
     private void notifyFirstWriter() {
         if (writerLocks.size() > 0) {
             Object oldest = writerLocks.removeFirst();
             ++activeWriters;
             synchronized (oldest) { 
                 oldest.notify(); 
             }
         }
     }
    
}
