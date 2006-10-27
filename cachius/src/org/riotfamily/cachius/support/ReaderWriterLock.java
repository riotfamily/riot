/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
