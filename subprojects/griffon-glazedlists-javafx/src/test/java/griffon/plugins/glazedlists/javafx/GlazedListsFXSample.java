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
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import griffon.plugins.glazedlists.javafx.gui.DefaultFXWritableTableFormat;
import griffon.plugins.glazedlists.javafx.gui.FXTableFormat;
import griffon.plugins.glazedlists.javafx.models.FXTableViewModel;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

import static griffon.plugins.glazedlists.javafx.GlazedListsJavaFX.eventTableViewModel;
import static griffon.plugins.glazedlists.javafx.GlazedListsJavaFX.propertyContainerConnector;
import static griffon.plugins.glazedlists.javafx.gui.FXTableFormat.option;
import static griffon.plugins.glazedlists.javafx.gui.FXTableFormat.options;

public class GlazedListsFXSample extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // non-observable beans
        ObservableList<Person> people = FXCollections.observableArrayList(
            new Person("Jamie", "Hyneman"),
            new Person("Adam", "Savage"),
            new Person("Tory", "Belleci"),
            new Person("Kari", "Byron"),
            new Person("Grant", "Imahara")
        );

        // observable beans
        EventList<ObservablePerson> evenList = GlazedLists.eventList(
            people.stream().map(ObservablePerson::new).collect(Collectors.<ObservablePerson>toList())
        );

        // a list that reacts to element updates
        EventList<ObservablePerson> op = new ObservableElementList<>(evenList, propertyContainerConnector());
        // bridge between EventList and ObservableList
        ObservableList<ObservablePerson> observablePeople = new EventObservableList<>(op);

        // define format options
        FXTableFormat<ObservablePerson> tableFormat = new DefaultFXWritableTableFormat<>(
            options(option("name", "name"), option("editable", true)),
            options(option("name", "lastname"), option("editable", true))
        );

        // table model backed by an ObservableList & TableFormat
        FXTableViewModel<ObservablePerson> tableModel = eventTableViewModel(observablePeople, tableFormat);
        // create the table
        TableView<ObservablePerson> tableView = new TableView<>();
        // attach the model to the table
        tableModel.attachTo(tableView);

        tableView.setEditable(true);

        // watch edits on tbale being pushed to list
        ListView<ObservablePerson> listView = new ListView<>();
        listView.setItems(observablePeople);

        // put everything in a Grid
        GridPane grid = new GridPane();
        grid.add(tableView, 0, 0);
        grid.add(listView, 1, 0);

        // show it
        Scene scene = new Scene(grid);
        stage.setTitle("MythBusters");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    // non-observable bean
    public static class Person {
        private final String name;
        private final String lastname;

        public Person(String name, String lastname) {
            this.name = name;
            this.lastname = lastname;
        }

        public String getName() {
            return name;
        }

        public String getLastname() {
            return lastname;
        }
    }

    // observable bean
    public static class ObservablePerson implements PropertyContainer {
        private StringProperty name;
        private StringProperty lastname;

        public ObservablePerson(Person person) {
            this(person.getName(), person.getLastname());
        }

        public ObservablePerson(String name, String lastname) {
            setName(name);
            setLastname(lastname);
        }

        @Override
        @Nonnull
        public Property<?>[] properties() {
            return new Property<?>[]{
                nameProperty(), lastnameProperty()
            };
        }

        public StringProperty nameProperty() {
            if (name == null) {
                name = new SimpleStringProperty(this, "name", "");
            }
            return name;
        }

        public StringProperty lastnameProperty() {
            if (lastname == null) {
                lastname = new SimpleStringProperty(this, "lastname", "");
            }
            return lastname;
        }

        public String getName() {
            return nameProperty().get();
        }

        public String getLastname() {
            return lastnameProperty().get();
        }

        public void setName(String name) {
            nameProperty().set(name);
        }

        public void setLastname(String lastname) {
            lastnameProperty().set(lastname);
        }

        @Override
        public String toString() {
            return name.get() + " " + lastname.get();
        }
    }
}
