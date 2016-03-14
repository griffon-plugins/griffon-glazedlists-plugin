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
package griffon.builder.javafx

import griffon.builder.javafx.factory.DefaultFXTableFormatFactory
import griffon.builder.javafx.factory.DefaultFXWritableTableFormatFactory
import griffon.builder.javafx.factory.DefaultTableViewModelFactory
import griffon.inject.DependsOn
import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer

import javax.inject.Named

/**
 * @author Andres Almiray
 */
@DependsOn('glazedlists-core')
@Named('glazedlists-javafx')
class GlazedlistsJavaFXBuilderCustomizer extends AbstractBuilderCustomizer {
    GlazedlistsJavaFXBuilderCustomizer() {
        setFactories([
            fxTableFormat         : new DefaultFXTableFormatFactory(),
            fxtWritableTableFormat: new DefaultFXWritableTableFormatFactory(),
            eventTableViewModel   : new DefaultTableViewModelFactory()
        ])
    }
}
