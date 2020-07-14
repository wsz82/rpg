package io.wsz.model.item;

public class InDoor extends Door<InDoor> {
    private static final long serialVersionUID = 1L;

    public InDoor() {}

    public InDoor(InDoor prototype, String name, ItemType type, String path, Boolean visible) {
        super(prototype, name, type, path, visible);
    }

//    @Override
//    public void writeExternal(ObjectOutput out) throws IOException {
//        super.writeExternal(out);
//        out.writeLong(Sizes.VERSION);
//
//        out.writeObject(openImagePath);
//
//        out.writeBoolean(open);
//
//        out.writeObject(openDoorCoverLine);
//
//        out.writeObject(openDoorCollisionPolygons);
//    }
//
//    @Override
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        super.readExternal(in);
//        long ver = in.readLong();
//
//        openImagePath = (String) in.readObject();
//
//        open = in.readBoolean();
//
//        openDoorCoverLine = (List<Coords>) in.readObject();
//
//        openDoorCollisionPolygons = (List<List<Coords>>) in.readObject();
//    }
}
