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
package griffon.plugins.glazedlists.javafx;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.impl.gui.ThreadProxyEventList;
import ca.odell.glazedlists.impl.swing.SwingThreadProxyEventList;
import griffon.plugins.glazedlists.ColumnReader;
import griffon.plugins.glazedlists.javafx.gui.DefaultFXTableFormat;
import griffon.plugins.glazedlists.javafx.gui.FXTableFormat;
import griffon.plugins.glazedlists.javafx.models.DefaultFXTableViewModel;
import griffon.plugins.glazedlists.javafx.models.FXTableViewModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import javax.annotation.Nonnull;
import java.util.List;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * A factory for creating all sorts of objects to be used with Glazed Lists and JavaFX.
 *
 * @author Andres Almriay
 */
public final class GlazedListsJavaFX {
    private GlazedListsJavaFX() {

    }

    @Nonnull
    public static <E extends PropertyContainer> ObservableElementList.Connector<E> propertyContainerConnector() {
        return new PropertyContainerConnector<>();
    }

    @Nonnull
    public static <E extends PropertyContainer> ObservableElementList.Connector<E> propertyContainerConnector(@Nonnull String... propertyNames) {
        return new PropertyContainerConnector<>(propertyNames);
    }

    /**
     * Wraps the source in an {@link EventList} that fires all of its update
     * events from the JavaFX application thread.
     *
     * @param source the {@link EventList} to be wrapped. Must not be null.
     * @return a wrapped {@link EventList} that fires all of its update events inside the JavaFX application thread.
     */
    @Nonnull
    public static <E> TransformedList<E, E> createJavaFXThreadProxyList(@Nonnull EventList<E> source) {
        return new JavaFXThreadProxyEventList<>(requireNonNull(source, "Argument 'source' must not be null"));
    }

    public static boolean isJavaFXThreadProxyList(EventList list) {
        return list instanceof JavaFXThreadProxyEventList;
    }

    /**
     * Creates a {@link griffon.plugins.glazedlists.javafx.gui.FXTableFormat}.
     */
    public static <T> FXTableFormat<T> tableFormat(@Nonnull String[] propertyNames) {
        return new DefaultFXTableFormat<>(propertyNames);
    }

    /**
     * Creates a {@link griffon.plugins.glazedlists.javafx.gui.FXTableFormat}.
     */
    public static <T> FXTableFormat<T> tableFormat(@Nonnull String[] propertyNames, @Nonnull String[] columnLabels, @Nonnull ColumnReader[] columnReaders) {
        return new DefaultFXTableFormat<>(propertyNames, columnLabels, columnReaders);
    }

    /**
     * Creates a {@link griffon.plugins.glazedlists.javafx.gui.FXTableFormat}.
     */
    public static <T> FXTableFormat<T> tableFormat(@Nonnull FXTableFormat.Options... options) {
        return new DefaultFXTableFormat<>(options);
    }

    /**
     * Creates a {@link griffon.plugins.glazedlists.javafx.gui.FXTableFormat}.
     */
    public static <T> FXTableFormat<T> tableFormat(@Nonnull List<FXTableFormat.Options> options) {
        requireNonNull(options, "Argument 'options' must not be null");
        requireState(options.size() > 0, "Argument 'options' must not be empty");
        return new DefaultFXTableFormat<>(options.toArray(new FXTableFormat.Options[options.size()]));
    }

    /**
     * Creates a new table model that extracts column data from the given
     * <code>source</code> using the the given <code>tableFormat</code>.
     * <p>
     * <p>The returned table model is <strong>not thread-safe</strong>. Unless otherwise
     * noted, all methods are only safe to be called from the JavaFX application thread.
     * To do this programmatically, use {@link javafx.application.Platform#runLater(Runnable)} and
     * wrap the source list (or some part of the source list's pipeline) using
     * {@link GlazedListsJavaFX#createJavaFXThreadProxyList(EventList)}.</p>
     *
     * @param source      the EventList that provides the row objects
     * @param tableFormat the object responsible for extracting column data
     *                    from the row objects
     */
    public static <E> FXTableViewModel<E> eventTableViewModel(@Nonnull EventList<E> source, @Nonnull FXTableFormat<? super E> tableFormat) {
        return eventTableViewModel(new EventObservableList<>(source), tableFormat);
    }

    /**
     * Creates a new table model that extracts column data from the given
     * <code>source</code> using the the given <code>tableFormat</code>.
     * <p>
     * <p>The returned table model is <strong>not thread-safe</strong>. Unless otherwise
     * noted, all methods are only safe to be called from the JavaFX application thread.
     * To do this programmatically, use {@link javafx.application.Platform#runLater(Runnable)} and
     * wrap the source list (or some part of the source list's pipeline) using
     * {@link GlazedListsJavaFX#createJavaFXThreadProxyList(EventList)}.</p>
     *
     * @param source      the ObservableList that provides the row objects
     * @param tableFormat the object responsible for extracting column data
     *                    from the row objects
     */
    public static <E> FXTableViewModel<E> eventTableViewModel(@Nonnull ObservableList<E> source, @Nonnull FXTableFormat<? super E> tableFormat) {
        return new DefaultFXTableViewModel<>(source, tableFormat);
    }

    /**
     * Creates a new table model that extracts column data from the given <code>source</code>
     * using the the given <code>tableFormat</code>. While holding a read lock,
     * this method wraps the source list using
     * {@link GlazedListsJavaFX#createJavaFXThreadProxyList(EventList)}.
     * <p>
     * The returned table model is <strong>not thread-safe</strong>. Unless otherwise noted, all
     * methods are only safe to be called from the event dispatch thread.
     * </p>
     *
     * @param source      the EventList that provides the row objects
     * @param tableFormat the object responsible for extracting column data from the row objects
     */
    public static <E> FXTableViewModel<E> eventTableViewModelWithThreadProxyList(@Nonnull EventList<E> source, @Nonnull FXTableFormat<? super E> tableFormat) {
        EventList<E> proxySource = createJavaFXThreadProxyList(source);
        return new DefaultFXTableViewModel<>(new EventObservableList<>(proxySource), tableFormat);
    }

    private static class JavaFXThreadProxyEventList<E> extends ThreadProxyEventList<E> {
        /**
         * Create a {@link JavaFXThreadProxyEventList} which delivers changes to the
         * given <code>source</code> on the JavaFX application thread.
         *
         * @param source the {@link EventList} for which to proxy events
         */
        private JavaFXThreadProxyEventList(@Nonnull EventList<E> source) {
            super(source);
        }

        @Override
        protected void schedule(Runnable runnable) {
            requireNonNull(runnable, "Argument 'runnable' must not be null");
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                Platform.runLater(runnable);
            }
        }
    }
}
