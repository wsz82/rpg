import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

class LayersTableView extends TableView<Layer> {
    private final ObservableList<Layer> layers = FXCollections.observableArrayList();

    LayersTableView() {
        super();
        initTable();
    }

    private void initTable() {
        this.setItems(layers);

        TableColumn<Layer, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));

        TableColumn<Layer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Layer, Boolean> visibleCol = new TableColumn<>("Visibility");
        visibleCol.setCellValueFactory(new PropertyValueFactory<>("visible"));

        ObservableList<TableColumn<Layer, ?>> columns = this.getColumns();
        columns.add(0, levelCol);
        columns.add(1, nameCol);
        columns.add(2, visibleCol);
    }

    void add(Layer layer) {
        layers.add(layer);
    }

    void removeLayer() {
        Layer layer = this.getSelectionModel().getSelectedItem();
        layers.remove(layer);
    }
}
