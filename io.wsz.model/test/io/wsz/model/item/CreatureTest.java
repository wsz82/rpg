package io.wsz.model.item;

import io.wsz.model.dialog.Answer;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.stage.Coords;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatureTest {

    @Test
    public void testEquals() {
        List<Coords> pcl = new ArrayList<>(0);
        List<List<Coords>> pcp = new ArrayList<>(0);
        Creature p = new Creature(null, "Enemy", ItemType.CREATURE, "C:/enemy.png",
                true, null, pcl, pcp);
        List<Answer> answers = new ArrayList<>(1);
        Answer sample = new Answer("sample", new ArrayList<>(0));
        answers.add(sample);
        Dialog dialog = new Dialog(answers, 0);
        p.setDialog(dialog);

        Integer level = 0;

        List<Coords> acl = new ArrayList<>(0);
        List<List<Coords>> acp = new ArrayList<>(0);
        Creature a = new Creature(p, p.getName(), p.getType(), p.getRelativePath(),
                true, level, acl, acp);

        Inventory inventoryA = new Inventory(a);
        a.setInventory(inventoryA);

        a.getTask().setDest(new Coords(12, 7, null));

        List<Coords> bcl = new ArrayList<>(0);
        List<List<Coords>> bcp = new ArrayList<>(0);
        Creature b = new Creature(p, p.getName(), p.getType(), p.getRelativePath(),
                true, level, bcl, bcp);

        a.getTask().clone(b);

        Inventory inventoryB = new Inventory(b);
        b.setInventory(inventoryB);

        assertEquals(a, b);
    }
}