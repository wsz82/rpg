package io.wsz.model.item;

import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;

public class CreatureBase {
    private static final String CIRCLE = "circle";

    private static final CreatureBase XS_C = new CreatureBase("xs_c.png");
    private static final CreatureBase XS_E = new CreatureBase("xs_e.png");
    private static final CreatureBase XS_N = new CreatureBase("xs_n.png");

    private static final CreatureBase S_C = new CreatureBase("s_c.png");
    private static final CreatureBase S_E = new CreatureBase("s_e.png");
    private static final CreatureBase S_N = new CreatureBase("s_n.png");

    private static final CreatureBase M_C = new CreatureBase("m_c.png");
    private static final CreatureBase M_E = new CreatureBase("m_e.png");
    private static final CreatureBase M_N = new CreatureBase("m_n.png");

    private static final CreatureBase L_C = new CreatureBase("l_c.png");
    private static final CreatureBase L_E = new CreatureBase("l_e.png");
    private static final CreatureBase L_N = new CreatureBase("l_n.png");

    private static final CreatureBase XL_C = new CreatureBase("xl_c.png");
    private static final CreatureBase XL_E = new CreatureBase("xl_e.png");
    private static final CreatureBase XL_N = new CreatureBase("xl_n.png");

    private static final CreatureBase[] bases = new CreatureBase[]{XS_C, XS_E, XS_N, S_C, S_E, S_N, M_C, M_E, M_N,
            L_C, L_E, L_N, XL_C, XL_E, XL_N};

    private Image img;
    private String relativePath;

    public CreatureBase(String path) {
        this.setRelativePath(path);
    }

    public static CreatureBase getCreatureBase(CreatureSize size, CreatureControl control) {
        String path = "";
        path += switch (size) {
            case XS -> "xs";
            case S -> "s";
            case M -> "m";
            case L -> "l";
            case XL -> "xl";
        };
        path += "_";
        path += switch (control) {
            case CONTROL, CONTROLLABLE -> "c";
            case NEUTRAL -> "n";
            case ENEMY -> "e";
        };
        path += ".png";
        for (CreatureBase base : bases) {
            if (base.getRelativePath().equals(path)) return base;
        }
        return null;
    }

    public static CreatureBase[] getBases() {
        return bases;
    }

    public Image getImage(File programDir) {
        if (img == null) {
            setImg(ResolutionImage.loadImage(programDir, CIRCLE, getRelativePath()));
        }
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
