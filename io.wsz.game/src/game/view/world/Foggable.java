package game.view.world;

import io.wsz.model.item.Creature;

import java.util.List;

public interface Foggable {

    void drawFog(List<Creature> heroes, double width, double height);

}
