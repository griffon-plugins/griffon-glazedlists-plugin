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

import griffon.plugins.glazedlists.javafx.gui.FXTableFormat;
import griffon.plugins.glazedlists.javafx.gui.FXWritableTableFormat;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultFXTableViewModel<E> implements FXTableViewModel<E> {
    private static final String ERROR_TABLE_VIEW_NULL = "Argument 'tableView' must not be null";
    private final ObservableList<E> source;
    private final FXTableFormat<? super E> format;
    private final Collection<TableColumn<E, ?>> columns = new ArrayList<>();

    public DefaultFXTableViewModel(@Nonnull ObservableList<E> source, @Nonnull FXTableFormat<? super E> format) {
        this.source = requireNonNull(source, "Argument 'source' must not be null");
        this.format = requireNonNull(format, "Argument 'format' must not be null");
        computeColumns();
    }

    private void computeColumns() {
        for (int i = 0; i < format.getColumnCount(); i++) {
            String columnName = format.getColumnName(i);
            TableColumn<E, ?> column = new TableColumn<>(columnName);

            processTableFormat(column, columnName, i);
            if (format instanceof FXWritableTableFormat) {
                processWritableTableFormat(column, columnName, i);
            }

            columns.add(column);
        }
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    protected <T> void processWritableTableFormat(@Nonnull TableColumn<E, T> column, String columnName, int columnIndex) {
        // empty
    }

    @SuppressWarnings("unchecked")
    protected <T> void processTableFormat(@Nonnull TableColumn<E, T> column, String columnName, int columnIndex) {
        column.setCellValueFactory(cell -> (ObservableValue<T>) format.getColumnObservableValue(cell.getValue(), columnIndex));
        column.setCellFactory(param -> format.getTableCellFactory(columnIndex).createTableCell(param));
    }

    @Nonnull
    public ObservableList<E> getSource() {
        return source;
    }

    @Nonnull
    public FXTableFormat<? super E> getFormat() {
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
