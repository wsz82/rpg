package editor.view.asset;

import io.wsz.model.item.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.item.ItemType.*;

public class ObservableAssets {
    private static ObservableAssets singleton;
    private final ObservableList<Creature> creatures = FXCollections.observableArrayList();
    private final ObservableList<Landscape> landscapes = FXCollections.observableArrayList();
    private final ObservableList<Cover> covers = FXCollections.observableArrayList();
    private final ObservableList<Teleport> teleports = FXCollections.observableArrayList();

    public static ObservableAssets get() {
        if (singleton == null) {
            singleton = new ObservableAssets();
        }
        return singleton;
    }

    private ObservableAssets() {
    }

    public List<Asset> merge() {
        List<Asset> assets = new ArrayList<>(0);
        assets.addAll(creatures);
        assets.addAll(landscapes);
        assets.addAll(covers);
        assets.addAll(teleports);
        return assets;
    }

    public void clearLists() {
        creatures.clear();
        landscapes.clear();
        covers.clear();
        teleports.clear();
    }

    public void fillLists(List<Asset> assets) {
        List<Creature> tempCr = assets.stream()
                .filter(a -> a.getType().equals(CREATURE))
                .map(a -> (Creature) a)
                .collect(Collectors.toList());
        creatures.addAll(tempCr);
        List<Landscape> tempL = assets.stream()
                .filter(a -> a.getType().equals(LANDSCAPE))
                .map(a -> (Landscape) a)
                .collect(Collectors.toList());
        landscapes.addAll(tempL);
        List<Cover> tempC = assets.stream()
                .filter(a -> a.getType().equals(COVER))
                .map(a -> (Cover) a)
                .collect(Collectors.toList());
        covers.addAll(tempC);
        List<Teleport> tempT = assets.stream()
                .filter(a -> a.getType().equals(TELEPORT))
                .map(a -> (Teleport) a)
                .collect(Collectors.toList());
        teleports.addAll(tempT);
    }

    public ObservableList<Creature> getCreatures() {
        return creatures;
    }

    public ObservableList<Landscape> getLandscapes() {
        return landscapes;
    }

    public ObservableList<Cover> getCovers() {
        return covers;
    }

    public ObservableList<Teleport> getTeleports() {
        return teleports;
    }
}
