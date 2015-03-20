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
package griffon.plugins.glazedlists.javafx.models;

import ca.odell.glazedlists.gui.TableFormat;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultTableViewModel<E> implements TableViewModel<E> {
    private static final String ERROR_TABLE_VIEW_NULL = "Argument 'tableView' must not be null";
    private final ObservableList<E> source;
    private final TableFormat<E> format;
    private final Collection<TableColumn<E, Object>> columns = new ArrayList<>();

    public DefaultTableViewModel(@Nonnull ObservableList<E> source, @Nonnull TableFormat<E> format) {
        this.source = requireNonNull(source, "Argument 'source' must not be null");
        this.format = requireNonNull(format, "Argument 'format' must not be null");
        computeColumns();
    }

    private void computeColumns() {
        for (int i = 0; i < format.getColumnCount(); i++) {
            final String columnName = format.getColumnName(i);
            TableColumn<E, Object> column = new TableColumn<>(columnName);

            final int columnIndex = i;
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<E, Object>, ObservableValue<Object>>() {
                @Override
                @SuppressWarnings("unchecked")
                public ObservableValue<Object> call(final TableColumn.CellDataFeatures<E, Object> cell) {
                    return new ReadOnlyObjectPropertyBase<Object>() {
                        @Override
                        public Object get() {
                            return format.getColumnValue(cell.getValue(), columnIndex);
                        }

                        @Override
                        public Object getBean() {
                            return cell.getValue();
                        }

                        @Override
                        public String getName() {
                            return columnName;
                        }
                    };
                }
            });

            columns.add(column);
        }
    }

    @Nonnull
    public ObservableList<E> getSource() {
        return source;
    }

    @Nonnull
    public TableFormat<E> getFormat() {
        return format;
    }

    @Override
    public void attachTo(@Nonnull TableView<E> tableView) {
        requireNonNull(tableView, ERROR_TABLE_VIEW_NULL);
        tableView.setItems(source);
        tableView.getColumns().addAll(columns);
    }

    @Override
    public void detachFrom(@Nonnull TableView<E> tableView) {
        requireNonNull(tableView, ERROR_TABLE_VIEW_NULL);
        tableView.setItems(FXCollections.<E>emptyObservableList());
        tableView.getColumns().removeAll(columns);
    }
}
