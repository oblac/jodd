// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class ReentrantReadWriteUpdateLock implements ReadWriteUpdateLock {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock updateMutex = new ReentrantLock();

    private final ReadLock readLock = new ReadLock();
    private final UpdateLock updateLock = new UpdateLock();
    private final WriteLock writeLock = new WriteLock();

    /**
     * Returns update lock.
     */
    @Override
    public Lock updateLock() {
        return updateLock;
    }

    /**
     * Returns read lock.
     */
    @Override
    public Lock readLock() {
        return readLock;
    }

    /**
     * Returns write lock.
     */
    @Override
    public Lock writeLock() {
        return writeLock;
    }

    static abstract class HoldCountLock implements Lock {

        static class HoldCount {
            int value;
        }

        private final ThreadLocal<HoldCount> threadHoldCount = new ThreadLocal<HoldCount>() {
            @Override
            protected HoldCount initialValue() {
                return new HoldCount();
            }
        };

        private final Lock backingLock;

        public HoldCountLock(Lock backingLock) {
            this.backingLock = backingLock;
        }

        protected HoldCount holdCount() {
            return threadHoldCount.get();
        }

        @Override
        public void lock() {
            validatePreconditions();
            backingLock.lock();
            holdCount().value++;
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            validatePreconditions();
            backingLock.lockInterruptibly();
            holdCount().value++;
        }

        @Override
        public boolean tryLock() {
            validatePreconditions();
            if (backingLock.tryLock()) {
                holdCount().value++;
                return true;
            }
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            validatePreconditions();
            if (backingLock.tryLock(time, unit)) {
                holdCount().value++;
                return true;
            }
            return false;
        }

        @Override
        public void unlock() {
            backingLock.unlock();
            holdCount().value--;
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("This lock does not support conditions");
        }

        protected abstract void validatePreconditions();
    }

    class ReadLock extends HoldCountLock {

        public ReadLock() {
            super(readWriteLock.readLock());
        }

        protected void validatePreconditions() {
            if (updateLock.holdCount().value > 0) {
                throw new IllegalStateException("Cannot acquire read lock as update lock is in use");
            }
        }
    }

    class UpdateLock extends HoldCountLock {

        public UpdateLock() {
            super(updateMutex);
        }

        protected void validatePreconditions() {
            if (readLock.holdCount().value > 0) {
                throw new IllegalStateException("Cannot acquire update lock as read lock is in use");
            }
        }
    }

    class WriteLock implements Lock {

        @Override
        public void lock() {
            validatePreconditions();
            // Acquire UPDATE lock again, even if calling thread might already hold it.
            // This allow threads to go from both NONE -> WRITE and from UPDATE -> WRITE.
            // This also ensures that only the thread holding the single UPDATE lock,
            // can request the WRITE lock...
            Locks.lockAll(updateLock, readWriteLock.writeLock());
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            validatePreconditions();
            Locks.lockInterruptiblyAll(updateLock, readWriteLock.writeLock());
        }

        @Override
        public boolean tryLock() {
            validatePreconditions();
            return Locks.tryLockAll(updateLock, readWriteLock.writeLock());
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            validatePreconditions();
            return Locks.tryLockAll(time, unit, updateLock, readWriteLock.writeLock());
        }

        @Override
        public void unlock() {
            Locks.unlockAll(readWriteLock.writeLock(), updateLock);
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("This lock does not support conditions");
        }

        void validatePreconditions() {
            if (readLock.holdCount().value > 0) {
                throw new IllegalStateException("Cannot acquire write lock as read lock already in use");
            }
        }
    }
}
