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
package griffon.plugins.glazedlists.javafx

import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.GlazedLists
import spock.lang.Specification

/**
 * @author Andres Almiray
 */
class EventObservableListSpec extends Specification {
    void 'Can handle removal of a single element'() {
        given:
        EventList<String> source = GlazedLists.eventListOf('1', '2', '3')
        EventObservableList<String> target = new EventObservableList<>(source)

        when:
        source.remove('2')

        then:
        target.size() == 2
        target.containsAll(['1', '3'])
    }

    void 'Can handle removal of a multiple elements'() {
        given:
        EventList<String> source = GlazedLists.eventListOf('1', '2', '3')
        EventObservableList<String> target = new EventObservableList<>(source)

        when:
        source.remove('2')
        source.remove('3')

        then:
        target.size() == 1
        target.containsAll(['1'])
    }

    void 'Can handle removal of all elements'() {
        given:
        EventList<String> source = GlazedLists.eventListOf('1', '2', '3')
        EventObservableList<String> target = new EventObservableList<>(source)

        when:
        source.clear()

        then:
        !target.size()
    }
}