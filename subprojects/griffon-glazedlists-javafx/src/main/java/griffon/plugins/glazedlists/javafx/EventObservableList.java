/*
 * Copyright 2014-2016 the original author or authors.
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
package griffon.plugins.glazedlists.javafx;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

import static ca.odell.glazedlists.event.ListEvent.DELETE;
import static ca.odell.glazedlists.event.ListEvent.INSERT;
import static ca.odell.glazedlists.event.ListEvent.UPDATE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.synchronizedList;
import static java.util.Objects.requireNonNull;

/**
 * An adapter from {@link EventList} to {@link ObservableList}.
 * <p>
 * <p>Reordering events are currently ignored.</p>
 *
 * @author Andres Almiray
 */
public class EventObservableList<E> extends AbstractList<E> implements ObservableList<E> {
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' must not be null";
    private static final int[] EMPTY_INT_ARRAY = new int[0];

    private final EventList<E> delegate;
    private final List<InvalidationListener> invalidationListeners = synchronizedList(new ArrayList<>());
    private final List<ListChangeListener<? super E>> listChangeListeners = synchronizedList(new ArrayList<>());

    private final ListEventListener<E> listEventListener = changes -> {
        synchronized (invalidationListeners) {
            for (Iterator<InvalidationListener> it = new ReverseIterator<>(invalidationListeners); it.hasNext(); ) {
                it.next().invalidated(EventObservableList.this);
            }
        }

        synchronized (listChangeListeners) {
            for (Iterator<ListChangeListener<? super E>> it = new ReverseIterator<>(listChangeListeners); it.hasNext(); ) {
                ListEvent<E> changes_copy = changes.copy();

                // TODO: handle reordering

                ListChangeListener.Change<E> change = new ChangeAdapter(EventObservableList.this, changes_copy);

                if (it.hasNext()) {
                    it.next().onChanged(change);
                }
            }
        }
    };

    public EventObservableList(@Nonnull EventList<E> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
        this.delegate.addListEventListener(listEventListener);
    }

    /**
     * Disposing an EventList will make it eligible for garbage collection.
     * Some EventLists install themselves as listeners to related objects so
     * disposing them is necessary.
     * <p>
     * <p><strong><font color="#FF0000">Warning:</font></strong> It is an error
     * to call any method on an {@link EventList} after it has been disposed.
     */
    public void dispose() {
        delegate.removeListEventListener(listEventListener);
    }

    private <T> T supplyInsideLock(@Nonnull Supplier<T> supplier) {
        delegate.getReadWriteLock().writeLock().lock();
        try {
            return supplier.get();
        } finally {
            delegate.getReadWriteLock().writeLock().unlock();
        }
    }

    private <T> void runInsideLock(@Nonnull Runnable runnable) {
        delegate.getReadWriteLock().writeLock().lock();
        try {
            runnable.run();
        } finally {
            delegate.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ListChangeListener<? super E> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (!listChangeListeners.contains(listener)) {
            listChangeListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ListChangeListener<? super E> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        listChangeListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(InvalidationListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (!invalidationListeners.contains(listener)) {
            invalidationListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        invalidationListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(E... elements) {
        return supplyInsideLock(() -> delegate.addAll(asList(elements)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean setAll(E... elements) {
        return setAll(asList(elements));
    }

    @Override
    public boolean setAll(Collection<? extends E> elements) {
        return supplyInsideLock(() -> {
            clear();
            return addAll(elements);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(E... elements) {
        return removeAll(asList(elements));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(E... elements) {
        return retainAll(asList(elements));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return supplyInsideLock(() -> delegate.addAll(c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return supplyInsideLock(() -> delegate.removeAll(c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return supplyInsideLock(() -> delegate.retainAll(c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int from, int to) {
        runInsideLock(() -> {
            if ((to - from) <= 0) {
                return;
            }
            for (int i = (to - 1); i >= from; i--) {
                remove(i);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return supplyInsideLock(() -> delegate.get(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return supplyInsideLock(delegate::size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return supplyInsideLock(delegate::isEmpty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return supplyInsideLock(() -> delegate.contains(o));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public Object[] toArray() {
        return supplyInsideLock(delegate::toArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"NullableProblems", "SuspiciousToArrayCall"})
    public <T> T[] toArray(T[] a) {
        return supplyInsideLock(() -> delegate.toArray(a));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return supplyInsideLock(() -> delegate.remove(o));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public boolean containsAll(Collection<?> c) {
        return supplyInsideLock(() -> delegate.containsAll(c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e) {
        return supplyInsideLock(() -> delegate.add(e));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        return supplyInsideLock(() -> delegate.set(index, element));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        runInsideLock(() -> delegate.add(index, element));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        return supplyInsideLock(() -> delegate.remove(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return supplyInsideLock(() -> delegate.indexOf(o));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        return supplyInsideLock(() -> delegate.lastIndexOf(o));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        runInsideLock(delegate::clear);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public boolean addAll(int index, Collection<? extends E> c) {
        return supplyInsideLock(() -> delegate.addAll(index, c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public Iterator<E> iterator() {
        return supplyInsideLock(delegate::iterator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public ListIterator<E> listIterator() {
        return supplyInsideLock(() -> delegate.listIterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public ListIterator<E> listIterator(int index) {
        return supplyInsideLock(() -> delegate.listIterator(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public List<E> subList(int fromIndex, int toIndex) {
        return supplyInsideLock(() -> delegate.subList(fromIndex, toIndex));
    }

    private static class ReverseIterator<T> implements Iterator<T>, Iterable<T> {
        private final ListIterator<T> delegate;

        public ReverseIterator(List<T> list) {
            this.delegate = list.listIterator(list.size());
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasPrevious();
        }

        @Override
        public T next() {
            return delegate.previous();
        }

        @Override
        public void remove() {
            delegate.remove();
        }
    }

    private class ChangeAdapter extends ListChangeListener.Change<E> {
        private final ListEvent<E> changes;

        public ChangeAdapter(EventObservableList<E> list, ListEvent<E> changes) {
            super(list);
            this.changes = changes;
        }

        @Override
        public boolean next() {
            return changes.nextBlock();
        }

        @Override
        public void reset() {
            changes.reset();
        }

        @Override
        public int getFrom() {
            return changes.getBlockStartIndex();
        }

        @Override
        public int getTo() {
            return changes.getBlockEndIndex() + 1;
        }

        @Override
        public List<E> getRemoved() {
            return singletonList(changes.getOldValue());
        }

        @Override
        public boolean wasAdded() {
            return changes.getType() == INSERT;
        }

        @Override
        public boolean wasRemoved() {
            return changes.getType() == DELETE;
        }

        @Override
        public boolean wasUpdated() {
            return changes.getType() == UPDATE;
        }

        @Override
        protected int[] getPermutation() {
            return EMPTY_INT_ARRAY;
        }
    }
}