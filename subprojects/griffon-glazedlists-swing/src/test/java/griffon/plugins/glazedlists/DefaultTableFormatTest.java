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

import ca.odell.glazedlists.gui.TableFormat;
import griffon.util.CollectionUtils;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DefaultTableFormatTest {
    @Test
    public void basicSetupWorksWithPOJO() {
        // given:
        TableFormat<Person> tf = new DefaultTableFormat<>(new String[]{"name", "lastname"});
        Person person = new Person(1, "John", "Doe");

        //expect:
        assertEquals(2, tf.getColumnCount());
        assertEquals("Name", tf.getColumnName(0));
        assertEquals("Lastname", tf.getColumnName(1));
        assertEquals("John", tf.getColumnValue(person, 0));
        assertEquals("Doe", tf.getColumnValue(person, 1));
    }

    @Test
    public void customSetupWorksWithPOJO() {
        // given:
        TableFormat<Person> tf = new DefaultTableFormat<>(
            new String[]{"id", "name", "lastname"},
            new String[]{"ID", "Name", "Lastname"},
            new ColumnReader[]{
                ColumnReader.DEFAULT,
                new ColumnReader() {
                    @Nullable
                    @Override
                    public Object getValue(@Nonnull Object baseObject, @Nonnull String columnName, int columnIndex) {
                        return "Hodor";
                    }
                },
                ColumnReader.DEFAULT
            });
        Person person = new Person(1, "John", "Doe");

        //expect:
        assertEquals(3, tf.getColumnCount());
        assertEquals("ID", tf.getColumnName(0));
        assertEquals("Name", tf.getColumnName(1));
        assertEquals("Lastname", tf.getColumnName(2));
        assertEquals("Hodor", tf.getColumnValue(person, 1));
        assertEquals("Doe", tf.getColumnValue(person, 2));
    }

    @Test
    public void customMapSetupWorksWithPOJO() {
        // given:
        DefaultTableFormat<Person> tf = new DefaultTableFormat<>(CollectionUtils.<Map<String, Object>>list()
            .e(CollectionUtils.<String, Object>map().e("name", "id")
                .e("title", "ID")
                .e("reader", ColumnReader.DEFAULT))
            .e(CollectionUtils.<String, Object>map().e("name", "name")
                .e("title", "Name")
                .e("reader", new ColumnReader() {
                    @Nullable
                    @Override
                    public Object getValue(@Nonnull Object baseObject, @Nonnull String columnName, int columnIndex) {
                        return "Hodor";
                    }
                }))
            .e(CollectionUtils.<String, Object>map().e("name", "lastname")
                .e("title", "Lastname")
                .e("reader", ColumnReader.DEFAULT))
        );
        Person person = new Person(1, "John", "Doe");

        //expect:
        assertEquals(3, tf.getColumnCount());
        assertEquals("ID", tf.getColumnName(0));
        assertEquals("Name", tf.getColumnName(1));
        assertEquals("Lastname", tf.getColumnName(2));
        assertEquals("Hodor", tf.getColumnValue(person, 1));
        assertEquals("Doe", tf.getColumnValue(person, 2));
    }
}
