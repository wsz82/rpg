package editor.view.location;

import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import model.location.CurrentLocation;
import editor.view.stage.ChildStage;

import java.util.function.UnaryOperator;

public class LocationParametersStage extends ChildStage {
    private final AnchorPane root = new AnchorPane();
    private final TextField inputWidth = new TextField();
    private final TextField inputHeight = new TextField();
    private final Button accept = new Button("Ok");
    private final Button cancel = new Button("Cancel");

    public LocationParametersStage(Stage parent) {
        super(parent);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        initOwner(parent);
        setTitle("Parameters");
        setScene(scene);
        setHeight(200);

        HBox widthWithLabel = new HBox(10);
        Label widthLabel = new Label("Width");
        widthWithLabel.getChildren().addAll(widthLabel, inputWidth);
        filterTextFieldForPositiveNumber(inputWidth);
        inputWidth.setText("" + CurrentLocation.get().getLocation().getWidth());
        inputWidth.setPrefWidth(80);

        HBox heightWithLabel = new HBox(10);
        Label heightLabel = new Label("Height");
        heightWithLabel.getChildren().addAll(heightLabel, inputHeight);
        filterTextFieldForPositiveNumber(inputHeight);
        inputHeight.setText("" + CurrentLocation.get().getLocation().getHeight());
        inputHeight.setPrefWidth(80);

        VBox allParameters = new VBox(10);
        allParameters.getChildren().addAll(widthWithLabel, heightWithLabel);

        HBox acceptCancel = new HBox();
        acceptCancel.getChildren().addAll(accept, cancel);
        acceptCancel.setSpacing(10);

        AnchorPane.setTopAnchor(allParameters, 10.0);
        AnchorPane.setLeftAnchor(allParameters, 10.0);
        AnchorPane.setBottomAnchor(acceptCancel, 10.0);
        AnchorPane.setRightAnchor(acceptCancel, 10.0);
        root.getChildren().addAll(allParameters, acceptCancel);

        cancel.cancelButtonProperty().set(true);
        cancel.setOnAction(event -> close());

        accept.defaultButtonProperty().set(true);
        accept.setOnAction(event -> {
            int width = Integer.parseInt(inputWidth.getText());
            int height = Integer.parseInt(inputHeight.getText());
            changeCurrentLocationParameters(width, height);
            close();
        });
    }

    private void changeCurrentLocationParameters(int width, int height) {
        CurrentLocation.get().setCurrentWidth(width);
        CurrentLocation.get().setCurrentHeight(height);
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