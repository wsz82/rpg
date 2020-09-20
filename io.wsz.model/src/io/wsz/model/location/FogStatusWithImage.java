package io.wsz.model.location;

import io.wsz.model.stage.ResolutionImage;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FogStatusWithImage implements Externalizable {
    private static final long serialVersionUID = 1L;

    private FogStatus status;
    private ResolutionImage image;

    public FogStatusWithImage() {
    }

    public FogStatusWithImage(FogStatus status, ResolutionImage image) {
        this.status = status;
        this.image = image;
    }

    public FogStatus getStatus() {
        return status;
    }

    public void setStatus(FogStatus status) {
        this.status = status;
    }

    public ResolutionImage getImage() {
        return image;
    }

    public void setImage(ResolutionImage image) {
        this.image = image;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(status);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        status = (FogStatus) in.readObject();
    }
}
