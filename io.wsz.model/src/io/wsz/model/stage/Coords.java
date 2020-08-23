package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.location.Location;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class Coords implements Externalizable {
    private static final long serialVersionUID = 1L;

    public static Coords parseCoords(String s, Controller controller) {
        Coords pos = new Coords();
        int stringEnd;
        String substring;
        if (s.startsWith("Location")) {
            s = s.replaceFirst("Location:", "");
            stringEnd = s.indexOf("X");
            substring = s.substring(0, stringEnd);
            String locName = substring;
            Location loc = new Location(locName);
            pos.setLocation(loc);
            controller.restoreCoordsOfLocation(pos);
            s = s.replaceFirst(locName + "X:", "");
        } else {
            s = s.replaceFirst("X:", "");
        }
        stringEnd = s.indexOf("Y");
        substring = s.substring(0, stringEnd);
        pos.x = Double.parseDouble(substring);

        s = s.replaceFirst(substring + "Y:", "");
        stringEnd = s.indexOf("Level");
        substring = s.substring(0, stringEnd);
        pos.y = Double.parseDouble(substring);

        s = s.replaceFirst(substring + "Level:", "");
        pos.level = Integer.parseInt(s);
        return pos;
    }

    public double x;
    public double y;
    public int level;
    private Location location;

    public Coords() {}

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coords(double x, double y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }

    public Coords(double x, double y, int level, Location location) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.location = location;
    }

    public boolean is0() {
        return x == 0 && y == 0;
    }

    public boolean isEmpty() {
        return x == 0 && y == 0 && level == 0 && location == null;
    }

    public Coords clonePos() {
        return new Coords(this.x, this.y, this.level, this.location);
    }

    public void add(Coords pos2) {
        this.x += pos2.x;
        this.y += pos2.y;
    }

    public void subtract(Coords pos2) {
        this.x -= pos2.x;
        this.y -= pos2.y;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        String locationName;
        if (location != null) {
            locationName = location.getName();
            return "Location:" + locationName + "X:" + x + "Y:" + y + "Level:" + level;
        } else {
            return "X:" + x + "Y:" + y + "Level:" + level;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coords)) return false;
        Coords pos = (Coords) o;
        Location loc = getLocation();
        Location posLoc = pos.getLocation();
        boolean locsEquals;
        if (loc == null && posLoc == null) {
            locsEquals = true;
        } else if (loc != null && posLoc != null) {
            locsEquals = Objects.equals(loc.getName(), posLoc.getName());
        } else {
            locsEquals = false;
        }
        return Double.compare(pos.x, x) == 0 &&
                Double.compare(pos.y, y) == 0 &&
                level == pos.level &&
                locsEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, level, location);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);

        out.writeDouble(x);

        out.writeDouble(y);

        out.writeInt(level);

        if (location != null) {
            out.writeUTF(location.getName());
        } else {
            out.writeUTF("");
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        x = in.readDouble();

        y = in.readDouble();

        level = in.readInt();

        String locationName = in.readUTF();
        if (!locationName.isEmpty()) {
            location = new Location(locationName);
        }
    }
}
