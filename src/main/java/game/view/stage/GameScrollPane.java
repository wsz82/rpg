package game.view.stage;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

class GameScrollPane extends ScrollPane {

    GameScrollPane(Node node){
        super(node);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
    }
}
