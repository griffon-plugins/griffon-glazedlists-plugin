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

import griffon.exceptions.InstanceMethodInvocationException;
import javafx.beans.property.Property;

import javax.annotation.Nonnull;

import static griffon.util.GriffonClassUtils.getGetterName;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
final class JavaFXPropertyExtractor {
    private static final String PROPERTY_SUFFIX = "Property";

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <B> Property getProperty(@Nonnull B bean, @Nonnull String propertyName) {
        requireNonNull(bean, "Argument 'bean' must not be null");
        requireNonBlank(propertyName, "Argument 'propertyName' must not be null");

        if (!propertyName.endsWith(PROPERTY_SUFFIX)) {
            propertyName += PROPERTY_SUFFIX;
        }

        InstanceMethodInvocationException imie;
        try {
            // 1. try <columnName>Property() first
            return (Property<?>) invokeExactInstanceMethod(bean, propertyName);
        } catch (InstanceMethodInvocationException e) {
            imie = e;
        }

        // 2. fallback to get<columnName>Property()
        try {
            return (Property<?>) invokeExactInstanceMethod(bean, getGetterName(propertyName));
        } catch (InstanceMethodInvocationException e) {
            throw imie;
        }
    }
}
