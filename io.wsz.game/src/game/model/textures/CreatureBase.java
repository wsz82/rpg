package game.model.textures;

import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.sizes.Paths;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;

public class CreatureBase {
    private static final String BASE = "base";

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

    private ResolutionImage img;
    private String fileName;

    public CreatureBase(String fileName) {
        this.setFileName(fileName);
    }

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
        fileName += ".png";
        for (CreatureBase base : bases) {
            if (base.getFileName().equals(fileName)) return base;
        }
        return null;
    }

    public static CreatureBase[] getBases() {
        return bases;
    }

    public ResolutionImage getImage(File programDir) {
        if (img == null) {
            String path = programDir + Paths.TEXTURES_DIR + File.separator + BASE + File.separator + fileName;
            setImg(new ResolutionImage(path));
        }
        return img;
    }

    public void setImg(ResolutionImage img) {
        this.img = img;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
