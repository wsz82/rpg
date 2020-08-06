package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;
import javafx.scene.image.Image;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DialogItem implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String speaker;
    private String text;
    private Image picture;

    public DialogItem() {}

    public DialogItem(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
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

        out.writeUTF(speaker);

        out.writeUTF(text);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        speaker = in.readUTF();

        text = in.readUTF();
    }
}
