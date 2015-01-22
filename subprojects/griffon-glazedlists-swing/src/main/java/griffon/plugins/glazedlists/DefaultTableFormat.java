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
package griffon.plugins.glazedlists;

import ca.odell.glazedlists.gui.TableFormat;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.getNaturalName;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultTableFormat<E> implements TableFormat<E> {
    private static final String ERROR_COLUMN_NAMES_NULL = "Argument 'columnNames' must not be null.";

    protected final String[] columnNames;
    protected final String[] columnTitles;
    protected final ColumnReader[] columnReaders;

    private static String NAME = "name";
    private static String TITLE = "title";
    private static String READER = "reader";

    public DefaultTableFormat(@Nonnull String[] columnNames) {
        this.columnNames = requireNonNull(columnNames, ERROR_COLUMN_NAMES_NULL);
        this.columnTitles = new String[columnNames.length];
        this.columnReaders = new ColumnReader[columnNames.length];

        for (int i = 0; i < columnNames.length; i++) {
            columnTitles[i] = getNaturalName(columnNames[i]);
            columnReaders[i] = ColumnReader.DEFAULT;
        }
    }

    public DefaultTableFormat(@Nonnull String[] columnNames, @Nonnull String[] columnTitles, @Nonnull ColumnReader[] columnReaders) {
        this.columnNames = requireNonNull(columnNames, ERROR_COLUMN_NAMES_NULL);
        this.columnTitles = requireNonNull(columnTitles, "Argument 'columnTitles' must not be null.");
        this.columnReaders = requireNonNull(columnReaders, "Argument 'columnReaders' must not be null.");
        requireState(columnNames.length == columnTitles.length,
            "Arguments 'columNames' and 'columnTitles' have different cardinality. " + columnNames.length + " != " + columnTitles.length);
        requireState(columnNames.length == columnReaders.length,
            "Arguments 'columNames' and 'columnReaders' have different cardinality. " + columnNames.length + " != " + columnReaders.length);
    }

    public DefaultTableFormat(@Nonnull List<Map<String, Object>> options) {
        requireNonNull(options, "Argument 'options' must not be null.");
        requireState(options.size() > 0, "Argument 'options' must have at least on entry.");
        this.columnNames = new String[options.size()];
        this.columnTitles = new String[options.size()];
        this.columnReaders = new ColumnReader[options.size()];

        int i = 0;
        for (Map<String, Object> op : options) {
            if (op.containsKey(NAME)) {
                columnNames[i] = String.valueOf(op.get(NAME));
            }
            if (isBlank(columnNames[i])) {
                throw new IllegalArgumentException("Column " + i + " must have a value for name:");
            }

            if (op.containsKey(TITLE)) {
                columnTitles[i] = String.valueOf(op.get(TITLE));
            } else {
                columnTitles[i] = getNaturalName(columnNames[i]);
            }

            if (op.containsKey(READER) && op.get(READER) instanceof ColumnReader) {
                columnReaders[i] = (ColumnReader) op.get(READER);
            } else {
                columnReaders[i] = ColumnReader.DEFAULT;
            }

            i++;
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnTitles[column];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getColumnValue(E baseObject, int column) {
        return columnReaders[column].getValue(baseObject, columnNames[column], column);
    }
}
