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

import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * @author Andres Almiray
 * @since 1.3.1
 */
public class TextInputControlMatcherEditor<E> extends TextMatcherEditor<E> {
    private final TextInputControl textInputControl;
    private boolean live;
    private final TextHandler textHandler = new TextHandler();

    public TextInputControlMatcherEditor(TextInputControl textInputControl, TextFilterator<? super E> textFilterator) {
        this(textInputControl, textFilterator, true);
    }

    public TextInputControlMatcherEditor(TextInputControl textInputControl, TextFilterator<? super E> textFilterator, boolean live) {
        super(textFilterator);
        this.textInputControl = textInputControl;
        this.live = live;
        registerListeners(live);
        refilter();
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        if (live == this.live) { return; }
        deregisterListeners(this.live);
        this.live = live;
        registerListeners(this.live);
    }

    private void registerListeners(boolean live) {
        if (live) {
            textInputControl.textProperty().addListener(textHandler);
        } else {
            EventHandler<? super KeyEvent> delegate = textInputControl.getOnKeyReleased();
            KeyHandler keyHandler = new KeyHandler(delegate);
            textInputControl.setOnKeyReleased(keyHandler);

        }
    }

    private void deregisterListeners(boolean live) {
        if (live) {
            textInputControl.textProperty().removeListener(textHandler);
        } else {
            EventHandler<? super KeyEvent> handler = textInputControl.getOnKeyReleased();
            if (handler instanceof TextInputControlMatcherEditor.KeyHandler) {
                textInputControl.setOnKeyReleased(((TextInputControlMatcherEditor.KeyHandler) handler).getDelegate());
            }
        }
    }

    public void dispose() {
        deregisterListeners(live);
    }

    private void refilter() {
        final int mode = getMode();
        final String text = textInputControl.getText();
        final String[] filters;

        // in CONTAINS mode we treat the string as whitespace delimited
        if (mode == CONTAINS) { filters = text.split("[ \t]"); }

        // in STARTS_WITH, REGULAR_EXPRESSION, or EXACT modes we use the string in its entirety
        else if (mode == STARTS_WITH || mode == REGULAR_EXPRESSION || mode == EXACT) {
            filters = new String[]{text};
        } else { throw new IllegalStateException("Unknown mode: " + mode); }

        setFilterText(filters);
    }

    private class TextHandler implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            refilter();
        }
    }

    private class KeyHandler extends DelegatingKeyHandler {
        public KeyHandler(EventHandler<? super KeyEvent> delegate) {
            super(delegate);
        }

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                refilter();
            }
        }
    }

    private static class DelegatingKeyHandler implements EventHandler<KeyEvent> {
        private EventHandler<? super KeyEvent> delegate;

        public DelegatingKeyHandler() {

        }

        public DelegatingKeyHandler(EventHandler<? super KeyEvent> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void handle(KeyEvent event) {
            delegate.handle(event);
        }

        protected EventHandler<? super KeyEvent> getDelegate() {
            return delegate;
        }
    }
}