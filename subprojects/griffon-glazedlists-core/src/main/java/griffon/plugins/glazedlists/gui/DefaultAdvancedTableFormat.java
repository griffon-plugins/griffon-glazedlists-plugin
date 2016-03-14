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
package griffon.plugins.glazedlists.gui;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import griffon.plugins.glazedlists.ColumnReader;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultAdvancedTableFormat<E> extends DefaultTableFormat<E> implements AdvancedTableFormat<E> {
    private static final Class DEFAULT_CLASS = Object.class;
    private static final Comparator<Object> DEFAULT_COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof Comparable && o2 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2);
            }
            return -1;
        }
    };

    private static final String CLASS = "class";
    private static final String COMPARATOR = "comparator";

    private final Class[] columnClasses;
    private final Comparator[] columnComparators;

    public DefaultAdvancedTableFormat(@Nonnull String[] columnNames) {
        super(columnNames);
        this.columnClasses = new Class[columnNames.length];
        this.columnComparators = new Comparator[columnNames.length];

        for (int i = 0; i < columnNames.length; i++) {
            columnClasses[i] = DEFAULT_CLASS;
            columnComparators[i] = DEFAULT_COMPARATOR;
        }
    }

    public DefaultAdvancedTableFormat(@Nonnull String[] columnNames, @Nonnull String[] columnTitles, @Nonnull ColumnReader[] columnReaders,
                                      @Nonnull Class[] columnClasses, @Nonnull Comparator[] columnComparators) {
        super(columnNames, columnTitles, columnReaders);
        this.columnClasses = requireNonNull(columnClasses, "Argument 'columnClasses' must not be null.");
        this.columnComparators = requireNonNull(columnComparators, "Argument 'columnComparators' must not be null.");
        requireState(columnNames.length == columnClasses.length,
            "Arguments 'columNames' and 'columnClasses' have different cardinality. " + columnNames.length + " != " + columnClasses.length);
        requireState(columnNames.length == columnComparators.length,
            "Arguments 'columNames' and 'columnComparators' have different cardinality. " + columnNames.length + " != " + columnComparators.length);
    }

    public DefaultAdvancedTableFormat(@Nonnull List<Map<String, Object>> options) {
        super(options);
        this.columnClasses = new Class[columnNames.length];
        this.columnComparators = new Comparator[columnNames.length];

        int i = 0;
        for (Map<String, Object> op : options) {
            if (op.containsKey(CLASS) && op.get(CLASS) instanceof Class) {
                columnClasses[i] = (Class) op.get(CLASS);
            } else {
                columnClasses[i] = DEFAULT_CLASS;
            }

            if (op.containsKey(COMPARATOR) && op.get(COMPARATOR) instanceof Comparator) {
                columnComparators[i] = (Comparator) op.get(COMPARATOR);
            } else {
                columnComparators[i] = DEFAULT_COMPARATOR;
            }

            i++;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        return columnClasses[column];
    }

    @Override
    public Comparator getColumnComparator(int column) {
        return columnComparators[column];
    }
}
