package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;
import javafx.scene.image.Image;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DialogItem implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String speakerName;
    private SpeakerMark speakerMark;
    private String text;
    private Image picture;

    public DialogItem() {}

    public DialogItem(SpeakerMark speakerMark, String speakerName, String text) {
        this.speakerMark = speakerMark;
        this.speakerName = speakerName;
        this.text = text;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public SpeakerMark getSpeakerMark() {
        return speakerMark;
    }

    public void setSpeakerMark(SpeakerMark speakerMark) {
        this.speakerMark = speakerMark;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Image getPicture() {
        return picture;
    }

    public void setPicture(Image picture) {
        this.picture = picture;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(speakerName);

        out.writeObject(speakerMark);

        out.writeUTF(text);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        speakerName = in.readUTF();

        speakerMark = (SpeakerMark) in.readObject();

        text = in.readUTF();
    }
}
