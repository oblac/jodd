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

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Utilities for locks groups.
 */
public class Locks {

    /**
     * Calls <code>lock()</code> on all locks.
     */
    public static <L extends Lock> void lockAll(L... locks) {
        Deque<L> stack = new LinkedList<>();
        try {
            for (L lock : locks) {
                lock.lock();
                stack.push(lock);
            }
        } catch (RuntimeException rex) {
            unlockAll(stack);
            throw rex;
        }
    }

    /**
     * Calls <code>lockInterruptibly()</code> on all locks.
     */
    public static <L extends Lock> void lockInterruptiblyAll(L... locks) throws InterruptedException {
        Deque<L> stack = new LinkedList<>();
        try {
            for (L lock : locks) {
                lock.lockInterruptibly();
                stack.push(lock);
            }
        } catch (InterruptedException | RuntimeException e) {
            unlockAll(stack);
            throw e;
        }
    }

    /**
     * Calls <code>tryLock()</code> on all locks.
     */
    public static <L extends Lock> boolean tryLockAll(L... locks) {
        Deque<L> stack = new LinkedList<>();
        boolean success = false;
        try {
            for (L lock : locks) {
                success = lock.tryLock();
                if (success) {
                    stack.push(lock);
                } else {
                    break;
                }
            }
        } catch (RuntimeException e) {
            unlockAll(stack);
            throw e;
        }
        if (!success) {
            unlockAll(stack);
        }
        return success;
    }

    /**
     * Calls <code>tryLock()</code> on all locks.
     */
    public static <L extends Lock> boolean tryLockAll(long time, TimeUnit unit, L... locks) throws InterruptedException {
        Deque<L> stack = new LinkedList<>();
        boolean success = false;
        try {
            long limitNanos = unit.toNanos(time);
            long startNanos = System.nanoTime();
            for (L lock : locks) {
                long remainingNanos = !success
                        ? limitNanos // no need to calculate remaining time in first iteration
                        : limitNanos - (System.nanoTime() - startNanos); // recalculate in subsequent iterations

                // if remaining time is <= 0, we still try to obtain additional locks, supplying zero or negative
                // timeouts to those locks, which should treat it as a non-blocking tryLock() per API docs...
                success = lock.tryLock(remainingNanos, TimeUnit.NANOSECONDS);
                if (success) {
                    stack.push(lock);
                } else {
                    break;
                }
            }
        } catch (RuntimeException | InterruptedException e) {
            unlockAll(stack);
            throw e;
        }
        if (!success) {
            unlockAll(stack);
        }
        return success;
    }

    /**
     * Calls <code>unlock()</code> on all locks provided by the given iterable.
     */
    public static <L extends Lock> void unlockAll(L... locks) {
        for (L lock : locks) {
            lock.unlock();
        }
    }
    public static <L extends Lock> void unlockAll(Collection<L> locks) {
        for (L lock : locks) {
            lock.unlock();
        }
    }

}
