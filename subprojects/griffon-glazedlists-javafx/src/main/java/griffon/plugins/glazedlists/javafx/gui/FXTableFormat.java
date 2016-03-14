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
package griffon.plugins.glazedlists.javafx.gui;

import ca.odell.glazedlists.gui.TableFormat;
import javafx.beans.value.ObservableValue;

import javax.annotation.Nonnull;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public interface FXTableFormat<E> extends TableFormat<E> {
    /**
     * Gets the observable value of the specified field for the specified object. This
     * is the value that will be passed to the editor and renderer for the
     * column.
     */
    ObservableValue<?> getColumnObservableValue(E baseObject, int column);

    @Nonnull
    TableCellFactory getTableCellFactory(int column);

    @Nonnull
    public static Option option(@Nonnull String name, @Nonnull Object value) {
        return new Option(name, value);
    }

    @Nonnull
    public static Options options(@Nonnull Option... options) {
        return new Options(options);
    }

    class Option {
        public final String name;
        public final Object value;

        private Option(@Nonnull String name, @Nonnull Object value) {
            this.name = requireNonBlank(name, "Argument 'name' must not be blank");
            this.value = requireNonNull(value, "Argument 'value' must not be null");
        }
    }

    class Options {
        public final Option[] options;

        private Options(@Nonnull Option... options) {
            requireNonNull(options, "Argument 'value' must not be null");
            requireState(options.length > 0, "Argument 'options' must have at least one entry");
            this.options = options;
        }
    }
}