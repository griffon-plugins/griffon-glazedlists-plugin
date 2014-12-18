/*
 * Copyright 2014 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static griffon.util.GriffonClassUtils.setPropertyValue;
import static griffon.util.GriffonNameUtils.uncapitalize;

/**
 * @author Andres Almiray
 */
public interface ColumnWriter<E> {
    public static final ColumnWriter DEFAULT = new ColumnWriter() {
        @Override
        public void setValue(@Nonnull Object baseObject, @Nonnull String columnName, int columnIndex, @Nullable Object value) {
            if (baseObject instanceof Map) {
                ((Map) baseObject).put(uncapitalize(columnName), value);
            } else {
                setPropertyValue(baseObject, uncapitalize(columnName), value);
            }
        }
    };

    void setValue(@Nonnull E baseObject, @Nonnull String columnName, int columnIndex, @Nullable Object value);
}
