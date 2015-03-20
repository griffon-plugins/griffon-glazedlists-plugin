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
package griffon.plugins.glazedlists.gui;

import ca.odell.glazedlists.gui.WritableTableFormat;
import griffon.plugins.glazedlists.ColumnEdit;
import griffon.plugins.glazedlists.ColumnWriter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public class DefaultWritableTableFormat<E> extends DefaultAdvancedTableFormat<E> implements WritableTableFormat<E> {
    protected final ColumnWriter[] columnWriters;
    protected final ColumnEdit[] columnEdits;

    private static final String WRITER = "writer";
    private static final String EDITABLE = "editable";

    public DefaultWritableTableFormat(@Nonnull List<Map<String, Object>> options) {
        super(options);

        this.columnWriters = new ColumnWriter[options.size()];
        this.columnEdits = new ColumnEdit[options.size()];

        int i = 0;
        for (Map<String, Object> op : options) {
            if (op.containsKey(WRITER) && op.get(WRITER) instanceof ColumnWriter) {
                columnWriters[i] = (ColumnWriter) op.get(WRITER);
            } else {
                columnWriters[i] = ColumnWriter.DEFAULT;
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
    public boolean isEditable(E baseObject, int column) {
        return columnEdits[column].isEditable(baseObject, columnNames[column], column);
    }

    @Override
    public E setColumnValue(E baseObject, Object editedValue, int column) {
        columnWriters[column].setValue(baseObject, columnNames[column], column, editedValue);
        return baseObject;
    }
}
