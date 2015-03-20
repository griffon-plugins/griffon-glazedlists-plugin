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
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 */
public interface FXTableViewModel<E> {
    @Nonnull
    ObservableList<E> getSource();

    @Nonnull
    FXTableFormat<? super E> getFormat();

    void attachTo(@Nonnull TableView<E> tableView);

    void detachFrom(@Nonnull TableView<E> tableView);
}
