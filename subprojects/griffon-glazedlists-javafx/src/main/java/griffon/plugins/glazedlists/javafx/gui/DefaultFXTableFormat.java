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
package griffon.plugins.glazedlists.javafx.gui;

import griffon.plugins.glazedlists.ColumnReader;
import javafx.beans.value.ObservableValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.getNaturalName;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * Defines a read-only {@code FXTableFormat}.
 *
 * @author Andres Almiray
 */
public class DefaultFXTableFormat<E> implements FXTableFormat<E> {
    private static final String ERROR_COLUMN_NAMES_NULL = "Argument 'columnNames' must not be null.";

    protected final String[] columnNames;
    protected final String[] columnTitles;
    protected final ColumnReader[] columnReaders;
    protected final TableCellFactory[] tableCellFactories;

    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String READER = "reader";
    private static final String TABLE_CELL_FACTORY = "tableCellFactory";

    public DefaultFXTableFormat(@Nonnull String[] columnNames) {
        this.columnNames = requireNonNull(columnNames, ERROR_COLUMN_NAMES_NULL);
        this.columnTitles = new String[columnNames.length];
        this.columnReaders = new ColumnReader[columnNames.length];
        this.tableCellFactories = new TableCellFactory[columnNames.length];

        for (int i = 0; i < columnNames.length; i++) {
            columnTitles[i] = getNaturalName(columnNames[i]);
            columnReaders[i] = DefaultJavaFXColumnReader.INSTANCE;
            tableCellFactories[i] = TableCellFactory.DEFAULT_NON_EDITABLE;
        }
    }

    public DefaultFXTableFormat(@Nonnull String[] columnNames, @Nonnull String[] columnTitles, @Nonnull ColumnReader[] columnReaders) {
        this.columnNames = requireNonNull(columnNames, ERROR_COLUMN_NAMES_NULL);
        this.columnTitles = requireNonNull(columnTitles, "Argument 'columnTitles' must not be null");
        this.columnReaders = requireNonNull(columnReaders, "Argument 'columnReaders' must not be nul");
        this.tableCellFactories = new TableCellFactory[columnNames.length];

        requireState(columnNames.length == columnTitles.length,
            "Arguments 'columNames' and 'columnTitles' have different cardinality. " + columnNames.length + " != " + columnTitles.length);
        requireState(columnNames.length == columnReaders.length,
            "Arguments 'columNames' and 'columnReaders' have different cardinality. " + columnNames.length + " != " + columnReaders.length);

        for (int i = 0; i < columnNames.length; i++) {
            tableCellFactories[i] = TableCellFactory.DEFAULT_NON_EDITABLE;
        }
    }

    /**
     * Creates a {@code FXTableFormat} based on the supplied options.
     * <p>
     * Valid option keys are <tt>name</tt>, <tt>title</tt> and <tt>reader</tt>.
     * </p>
     *
     * @param options the options that configure this format
     */
    public DefaultFXTableFormat(@Nonnull FXTableFormat.Options... options) {
        requireNonNull(options, "Argument 'options' must not be null");
        requireState(options.length > 0, "Argument 'options' must have at least one entry");

        this.columnNames = new String[options.length];
        this.columnTitles = new String[options.length];
        this.columnReaders = new ColumnReader[options.length];
        this.tableCellFactories = new TableCellFactory[options.length];

        int i = 0;
        for (Options opts : options) {
            for (Option opt : opts.options) {
                if (NAME.equalsIgnoreCase(opt.name)) {
                    columnNames[i] = String.valueOf(opt.value);
                } else if (TITLE.equalsIgnoreCase(opt.name)) {
                    columnTitles[i] = String.valueOf(opt.value);
                } else if (READER.equalsIgnoreCase(opt.name)) {
                    columnReaders[i] = (ColumnReader) opt.value;
                } else if (TABLE_CELL_FACTORY.equalsIgnoreCase(opt.name)) {
                    tableCellFactories[i] = (TableCellFactory) opt.value;
                }
            }

            if (isBlank(columnNames[i])) {
                throw new IllegalArgumentException("Column " + i + " must have a value for name:");
            }
            if (isBlank(columnTitles[i])) {
                columnTitles[i] = getNaturalName(columnNames[i]);
            }
            if (columnReaders[i] == null) {
                columnReaders[i] = DefaultJavaFXColumnReader.INSTANCE;
            }

            i++;
        }
    }

    /**
     * Creates a {@code FXTableFormat} based on the supplied options.
     * <p>
     * Valid option keys are <tt>name</tt>, <tt>title</tt> and <tt>reader</tt>.
     * </p>
     *
     * @param options the options that configure this format
     */
    public DefaultFXTableFormat(@Nonnull List<Map<String, Object>> options) {
        requireNonNull(options, "Argument 'options' must not be null");
        requireState(options.size() > 0, "Argument 'options' must have at least one entry");

        this.columnNames = new String[options.size()];
        this.columnTitles = new String[options.size()];
        this.columnReaders = new ColumnReader[options.size()];
        this.tableCellFactories = new TableCellFactory[options.size()];

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
                columnReaders[i] = DefaultJavaFXColumnReader.INSTANCE;
            }

            if (op.containsKey(TABLE_CELL_FACTORY) && op.get(TABLE_CELL_FACTORY) instanceof TableCellFactory) {
                tableCellFactories[i] = (TableCellFactory) op.get(TABLE_CELL_FACTORY);
            } else {
                tableCellFactories[i] = TableCellFactory.DEFAULT_EDITABLE;
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

    @Override
    @SuppressWarnings("unchecked")
    public ObservableValue<?> getColumnObservableValue(E baseObject, int column) {
        return (ObservableValue<?>) columnReaders[column].getValue(baseObject, columnNames[column], column);
    }

    @Nonnull
    @Override
    public TableCellFactory getTableCellFactory(int column) {
        return tableCellFactories[column];
    }
}
