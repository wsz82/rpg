package editor.view.location;

import editor.view.IntegerField;
import editor.view.stage.ChildStage;
import io.wsz.model.Controller;
import io.wsz.model.location.CurrentLocation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LocationParametersStage extends ChildStage {
    private final AnchorPane root = new AnchorPane();
    private final IntegerField inputWidth = new IntegerField(1);
    private final IntegerField inputHeight = new IntegerField(1);
    private final Button accept = new Button("Ok");
    private final Button cancel = new Button("Cancel");

    public LocationParametersStage(Stage parent) {
        super(parent);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle("Parameters");
        setScene(scene);
        setHeight(200);

        HBox widthWithLabel = new HBox(10);
        Label widthLabel = new Label("Width");
        widthWithLabel.getChildren().addAll(widthLabel, inputWidth);
        inputWidth.setText("" + CurrentLocation.get().getLocation().getWidth());
        inputWidth.setPrefWidth(80);

        HBox heightWithLabel = new HBox(10);
        Label heightLabel = new Label("Height");
        heightWithLabel.getChildren().addAll(heightLabel, inputHeight);
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
        Controller.get().getCurrentLocation().setWidth(width);
        Controller.get().getCurrentLocation().setHeight(height);
    }
}