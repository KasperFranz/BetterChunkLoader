package guru.franz.mc.bcl.config.node;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ChunksAmountNode {

    @Setting("Personal")
    protected int personal = 0;
    @Setting("World")
    protected int world = 0;

    public int getPersonal() {
        return personal;
    }

    public void setPersonal(int personal) {
        this.personal = personal;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }
}
