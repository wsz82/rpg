package io.wsz.model.textures;

import io.wsz.model.animation.creature.CreatureBaseAnimation;
import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.sizes.Paths;

import java.io.File;

import static io.wsz.model.sizes.Paths.IDLE;

public class CreatureBase {
    private static final String BASE = "base";

    private static final CreatureBase XS_C = new CreatureBase("xs_c");
    private static final CreatureBase XS_E = new CreatureBase("xs_e");
    private static final CreatureBase XS_N = new CreatureBase("xs_n");

    private static final CreatureBase S_C = new CreatureBase("s_c");
    private static final CreatureBase S_E = new CreatureBase("s_e");
    private static final CreatureBase S_N = new CreatureBase("s_n");

    private static final CreatureBase M_C = new CreatureBase("m_c");
    private static final CreatureBase M_E = new CreatureBase("m_e");
    private static final CreatureBase M_N = new CreatureBase("m_n");

    private static final CreatureBase L_C = new CreatureBase("l_c");
    private static final CreatureBase L_E = new CreatureBase("l_e");
    private static final CreatureBase L_N = new CreatureBase("l_n");

    private static final CreatureBase XL_C = new CreatureBase("xl_c");
    private static final CreatureBase XL_E = new CreatureBase("xl_e");
    private static final CreatureBase XL_N = new CreatureBase("xl_n");

    private static final CreatureBase[] bases = new CreatureBase[]{XS_C, XS_E, XS_N, S_C, S_E, S_N, M_C, M_E, M_N,
            L_C, L_E, L_N, XL_C, XL_E, XL_N};

    public static CreatureBase getCreatureBase(CreatureSize size, CreatureControl control) {
        String fileName = "";
        fileName += switch (size) {
            case XS -> "xs";
            case S -> "s";
            case M -> "m";
            case L -> "l";
            case XL -> "xl";
        };
        fileName += "_";
        fileName += switch (control) {
            case CONTROL, CONTROLLABLE -> "c";
            case NEUTRAL -> "n";
            case ENEMY -> "e";
        };
        for (CreatureBase base : bases) {
            if (base.getFileName().equals(fileName)) return base;
        }
        return null;
    }

    public static CreatureBase[] getBases() {
        return bases;
    }

    private String fileName;
    private CreatureBaseAnimation animation;

    private CreatureBase(String fileName) {
        this.setFileName(fileName);
    }

    public void initAnimation(File programDir) {
        String animationDir = Paths.TEXTURES_DIR + File.separator + BASE + File.separator + fileName;
        animation = new CreatureBaseAnimation(animationDir, IDLE);
        animation.initAllAnimations(programDir);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CreatureBaseAnimation getAnimation() {
        return animation;
    }
}
