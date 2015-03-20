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

import griffon.plugins.glazedlists.ColumnWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.plugins.glazedlists.javafx.gui.JavaFXPropertyExtractor.getProperty;

/**
 * @author Andres Almiray
 */
public class DefaultJavaFXColumnWriter<E> implements ColumnWriter<E> {
    public static final ColumnWriter INSTANCE = new DefaultJavaFXColumnWriter();

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(@Nonnull E baseObject, @Nonnull String columnName, int columnIndex, @Nullable Object value) {
        getProperty(baseObject, columnName).setValue(value);
    }
}
