package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;

public interface Executable {

    void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary);

}
