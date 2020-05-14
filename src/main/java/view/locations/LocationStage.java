package view.locations;

import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import view.stage.ChildStage;

import java.util.function.UnaryOperator;

public class LocationStage extends ChildStage {
    private final AnchorPane root = new AnchorPane();
    private final ChoiceBox<Location> locationChoiceBox = new ChoiceBox<>();
    private final TextField inputName = new TextField();
    private final TextField inputWidth = new TextField();
    private final TextField inputHeight = new TextField();
    private final Button accept = new Button("Ok");
    private final Button cancel = new Button("Cancel");

    public LocationStage(Stage parent) {
        super(parent);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        this.initOwner(parent);
        this.setTitle("Parameters");
        this.setScene(scene);

        HBox currentLocationWithLabel = new HBox(10);
        Label currentLocationLabel = new Label("Current");
        currentLocationWithLabel.getChildren().addAll(currentLocationLabel, locationChoiceBox);
        setUpLocationChoiceBox();

        HBox nameWithLabel = new HBox(10);
        Label nameLabel = new Label("New location");
        nameWithLabel.getChildren().addAll(nameLabel, inputName);

        HBox widthWithLabel = new HBox(10);
        Label widthLabel = new Label("Width");
        widthWithLabel.getChildren().addAll(widthLabel, inputWidth);
        filterTextFieldForPositiveNumber(inputWidth);
        inputWidth.setText("" + CurrentLocation.get().getLocation().getWidth());

        HBox heightWithLabel = new HBox(10);
        Label heightLabel = new Label("Height");
        heightWithLabel.getChildren().addAll(heightLabel, inputHeight);
        filterTextFieldForPositiveNumber(inputHeight);
        inputHeight.setText("" + CurrentLocation.get().getLocation().getHeight());

        VBox widthWithHeight = new VBox(10);
        widthWithHeight.getChildren().addAll(widthWithLabel, heightWithLabel);

        HBox acceptCancel = new HBox();
        acceptCancel.getChildren().addAll(accept, cancel);
        acceptCancel.setSpacing(10);

        FlowPane flowPane = new FlowPane(8, 4);
        flowPane.getChildren().addAll(currentLocationWithLabel, nameWithLabel, widthWithHeight);

        AnchorPane.setTopAnchor(flowPane, 10.0);
        AnchorPane.setLeftAnchor(flowPane, 10.0);
        AnchorPane.setBottomAnchor(acceptCancel, 10.0);
        AnchorPane.setRightAnchor(acceptCancel, 10.0);
        root.getChildren().addAll(flowPane, acceptCancel);

        cancel.cancelButtonProperty().set(true);
        cancel.setOnAction(event -> this.close());

        accept.defaultButtonProperty().set(true);
        accept.setOnAction(event -> {
            String name = inputName.getText();
            int width = Integer.parseInt(inputWidth.getText());
            int height = Integer.parseInt(inputHeight.getText());
            boolean nameInputExists = !name.isEmpty();
            if (nameInputExists) {
                tryToAddNewLocation(name, width, height);
            } else {
                changeCurrentLocationParameters(width, height);
            }
            this.close();
        });
    }

    private void tryToAddNewLocation(String name, int width, int height) {
        Location location;
        location = new Location(name);
        location.setWidth(width);
        location.setHeight(height);
        if (!LocationsList.isLocationExisting(name)) {
            addNewLocation(location);
        } else {
            alertLocationExists();
        }
    }

    private void changeCurrentLocationParameters(int width, int height) {
        Location location;
        location = CurrentLocation.get().getLocation();
        location.setWidth(width);
        location.setHeight(height);
        CurrentLocation.get().setCurrentWidth(width);
        CurrentLocation.get().setCurrentHeight(height);
    }

    private void addNewLocation(Location location) {
        LocationsList.get().add(location);
        CurrentLocation.get().setLocation(location);
    }

    private void alertLocationExists() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This location name already exists!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> this.close());
    }

    private void setUpLocationChoiceBox() {
        locationChoiceBox.setItems(LocationsList.get());
        locationChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Location location) {
                return location.getName();
            }

            @Override
            public Location fromString(String name) {
                return LocationsList.getLocation(name);
            }
        });
        locationChoiceBox.valueProperty().bindBidirectional(
                CurrentLocation.get().locationProperty()
        );
        locationChoiceBox.setOnAction(event -> {
            Location location = locationChoiceBox.getValue();
            inputWidth.setText("" + location.getWidth());
            inputHeight.setText("" + location.getHeight());
        });
    }

    private void filterTextFieldForPositiveNumber(TextField textField) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getControlNewText();
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return null;
            }
            return change;
        };
        textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 1, integerFilter));
        ChangeListener<String> positiveNumberListener = (observable, oldValue, newValue) -> {
            int newNumber = 1;

            try {
                newNumber = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                textField.setText("" + oldValue);
            }
            if (newNumber < 1) textField.setText("" + oldValue);
        };
        textField.textProperty().addListener(positiveNumberListener);
    }
}