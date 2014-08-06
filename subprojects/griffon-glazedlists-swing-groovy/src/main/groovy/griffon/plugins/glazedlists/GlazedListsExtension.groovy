/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.glazedlists

import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.util.concurrent.Lock

/**
 * @author Andres Almiray
 */
class GlazedListsExtension {
    static Object withLock(Lock self, Closure closure) {
        withLockHandler(self, closure)
    }

    static Object withReadLock(EventList self, Closure closure) {
        withLockHandler(self.readWriteLock.readLock(), closure)
    }

    static Object withWriteLock(Lock self, Closure closure) {
        withLockHandler(self.readWriteLock.writeLock(), closure)
    }

    private static final withLockHandler(Lock lock, Closure closure) {
        lock.lock()
        try {
            closure()
        } finally {
            lock.unlock()
        }
    }
}
