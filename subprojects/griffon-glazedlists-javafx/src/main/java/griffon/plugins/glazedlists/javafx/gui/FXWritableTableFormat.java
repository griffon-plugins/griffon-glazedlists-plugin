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

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 */
public interface FXWritableTableFormat<E> extends FXTableFormat<E> {
    /**
     * For editing fields. This returns true if the specified Object in the
     * specified column can be edited by the user.
     *
     * @param column the column to test.
     * @return true if the object and column are editable, false otherwise.
     */
    boolean isEditable(@Nonnull E baseObject, int column);

    /**
     * Sets the specified field of the base object to the edited value. When
     * a column of a table is edited, this method is called so that the user
     * can specify how to modify the base object for each column.
     *
     * @param baseObject  the Object to be edited. This will be the original
     *                    Object from the source list.
     * @param editedValue the newly constructed value for the edited column
     * @param column      the column which has been edited
     * @return the revised Object, or null if the revision shall be discarded.
     * If not null, the EventTableModel will set() this revised value in
     * the list and overwrite the previous value.
     */
    @Nonnull
    E setColumnValue(@Nonnull E baseObject, Object editedValue, int column);
}
