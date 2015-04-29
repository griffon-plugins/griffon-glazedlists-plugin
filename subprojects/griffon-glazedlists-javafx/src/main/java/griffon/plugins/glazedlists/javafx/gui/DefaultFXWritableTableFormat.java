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

import griffon.plugins.glazedlists.ColumnEdit;
import griffon.plugins.glazedlists.ColumnWriter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * Defines an editable {@code FXTableFormat}.
 *
 * @author Andres Almiray
 */
public class DefaultFXWritableTableFormat<E> extends DefaultFXTableFormat<E> implements FXWritableTableFormat<E> {
    protected final ColumnWriter[] columnWriters;
    protected final ColumnEdit[] columnEdits;

    private static final String WRITER = "writer";
    private static final String EDITABLE = "editable";

    /**
     * Creates a {@code FXTableFormat} based on the supplied options.
     * <p>
     * Valid option keys are <tt>name</tt>, <tt>title</tt>, <tt>reader</tt>,
     * <tt>writer</tt>, <tt>editable</tt> and <tt>tableCellFactory</tt>.
     * </p>
     *
     * @param options the options that configure this format
     */
    public DefaultFXWritableTableFormat(@Nonnull FXTableFormat.Options... options) {
        super(options);

        requireNonNull(options, "Argument 'options' must not be null");
        requireState(options.length > 0, "Argument 'options' must have at least one entry");

        this.columnWriters = new ColumnWriter[options.length];
        this.columnEdits = new ColumnEdit[options.length];

        int i = 0;
        for (Options opts : options) {
            for (Option opt : opts.options) {
                if (WRITER.equalsIgnoreCase(opt.name)) {
                    columnWriters[i] = (ColumnWriter) opt.value;
                } else if (EDITABLE.equalsIgnoreCase(opt.name)) {
                    if (opt.value instanceof ColumnEdit) {
                        columnEdits[i] = (ColumnEdit) opt.value;
                    }
                }
            }

            if (columnWriters[i] == null) {
                columnWriters[i] = DefaultJavaFXColumnWriter.INSTANCE;
            }
            if (columnEdits[i] == null) {
                columnEdits[i] = ColumnEdit.DEFAULT;
            }
            if (tableCellFactories[i] == null) {
                tableCellFactories[i] = TableCellFactory.DEFAULT_EDITABLE;
            }

            i++;
        }
    }

    /**
     * Creates a {@code FXTableFormat} based on the supplied options.
     * <p>
     * Valid option keys are <tt>name</tt>, <tt>title</tt>, <tt>reader</tt>,
     * <tt>writer</tt>, <tt>editable</tt> and <tt>tableCellFactory</tt>.
     * </p>
     *
     * @param options the options that configure this format
     */
    public DefaultFXWritableTableFormat(@Nonnull List<Map<String, Object>> options) {
        super(options);

        this.columnWriters = new ColumnWriter[options.size()];
        this.columnEdits = new ColumnEdit[options.size()];

        int i = 0;
        for (Map<String, Object> op : options) {
            if (op.containsKey(WRITER) && op.get(WRITER) instanceof ColumnWriter) {
                columnWriters[i] = (ColumnWriter) op.get(WRITER);
            } else {
                columnWriters[i] = DefaultJavaFXColumnWriter.INSTANCE;
            }

            if (op.containsKey(EDITABLE) && op.get(EDITABLE) instanceof ColumnEdit) {
                columnEdits[i] = (ColumnEdit) op.get(EDITABLE);
            } else {
                columnEdits[i] = ColumnEdit.DEFAULT;
            }

            i++;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEditable(@Nonnull E baseObject, int column) {
        return columnEdits[column].isEditable(baseObject, columnNames[column], column);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public E setColumnValue(@Nonnull E baseObject, Object editedValue, int column) {
        columnWriters[column].setValue(baseObject, columnNames[column], column, editedValue);
        return baseObject;
    }
}
