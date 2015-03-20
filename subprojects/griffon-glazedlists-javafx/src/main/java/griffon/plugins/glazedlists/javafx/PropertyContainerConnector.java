/*
 * Copyright 2014-2015 the original author or authors.
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

import ca.odell.glazedlists.ObservableElementList;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.EventListener;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.sort;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class PropertyContainerConnector<T extends PropertyContainer> implements ObservableElementList.Connector<T> {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private ObservableElementList<? extends PropertyContainer> list;
    private final String[] propertyNames;

    public PropertyContainerConnector() {
        this.propertyNames = EMPTY_STRING_ARRAY;
    }

    public PropertyContainerConnector(@Nonnull String[] propertyNames) {
        requireNonNull(propertyNames, "Argument 'propertyNames' must not be null");
        this.propertyNames = copyOf(propertyNames, propertyNames.length);
        sort(this.propertyNames);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventListener installListener(T element) {
        ElementChangeHandler ecl = new ElementChangeHandler<>(element);
        for (Property<?> property : element.properties()) {
            if (matches(property.getName())) {
                property.addListener(ecl);
            }
        }
        return ecl;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void uninstallListener(T element, EventListener listener) {
        if (listener instanceof ChangeListener) {
            ChangeListener cl = (ChangeListener) listener;
            for (Property<?> property : element.properties()) {
                if (matches(property.getName())) {
                    property.removeListener(cl);
                }
            }
        }
    }

    private boolean matches(@Nonnull String propertyName) {
        return propertyNames.length == 0 || binarySearch(propertyNames, propertyName) > -1;
    }

    @Override
    public void setObservableElementList(ObservableElementList<? extends T> list) {
        this.list = list;
    }

    private class ElementChangeHandler<E> implements ElementChangeListener<E> {
        private final WeakReference<E> element;

        private ElementChangeHandler(@Nonnull E element) {
            this.element = new WeakReference<>(element);
        }

        @Override
        public void changed(ObservableValue<? extends E> observable, E oldValue, E newValue) {
            list.elementChanged(element.get());
        }
    }
}