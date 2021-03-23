import java.io.Serializable;

public class InitPacket implements Serializable {

    private int ID;
    private int conID;

    public InitPacket(int ID, int conID) {
        this.ID = ID;
        this.conID = conID;
    }

    public int getID() {
        return ID;
    }

    public int getConID() {
        return conID;
    }
}
