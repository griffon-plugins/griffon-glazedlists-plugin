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
package org.codehaus.griffon.runtime.glazedlists.javafx

import ca.odell.glazedlists.gui.TableFormat
import griffon.plugins.glazedlists.javafx.gui.DefaultFXTableFormat

//import griffon.plugins.glazedlists.gui.DefaultFXTableFormat
import griffon.transform.FXObservable
import spock.lang.Specification

class DefaultFXTableFormatSpec extends Specification {
    void 'Can create a TableFormat using just column names'() {
        given:
        Map props = [id: '1', name: 'Joe', lastname: 'Cool']
        Person person = new Person(props)

        when:
        TableFormat<Person> format = new DefaultFXTableFormat<Person>(props.keySet() as String[])

        then:
        format.columnCount == props.size()
        format.columnNames == (props.keySet() as String[])
        format.columnTitles == (props.keySet()*.capitalize() as String[])
        props.eachWithIndex  { k, v, i ->
            assert k.capitalize() == format.getColumnName(i)
            assert v == format.getColumnValue(person, i).get()
        }
    }

    void 'Can create a TableFormat using an options List'() {
        given:
        Map props = [id: '1', name: 'Joe', lastname: 'Cool']
        Person person = new Person(props)

        when:
        TableFormat<Person> format = new DefaultFXTableFormat<Person>([
            [name: 'id', title: 'Id'],
            [name: 'name'],
            [name: 'lastname']
        ])

        then:
        format.columnCount == props.size()
        format.columnNames == (props.keySet() as String[])
        format.columnTitles == (props.keySet()*.capitalize() as String[])
        props.eachWithIndex  { k, v, i ->
            assert k.capitalize() == format.getColumnName(i)
            assert v == format.getColumnValue(person, i).get()
        }
    }

    void 'Can create a TableFormat that handles a Map'() {
        given:
        Map props = [id: '1', name: 'Joe', lastname: 'Cool']
        Person person = new Person(props)

        when:
        TableFormat<Map> format = new DefaultFXTableFormat<Map>(props.keySet() as String[])

        then:
        format.columnCount == props.size()
        format.columnNames == (props.keySet() as String[])
        format.columnTitles == (props.keySet()*.capitalize() as String[])
        props.eachWithIndex  { k, v, i ->
            assert k.capitalize() == format.getColumnName(i)
            assert v == format.getColumnValue(person, i).get()
        }
    }

    @FXObservable
    static class Person {
        String id
        String name
        String lastname
    }
}
