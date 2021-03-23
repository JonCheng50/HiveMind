import java.io.Serializable;

public class UpdatePacket implements Serializable {

    private int state;
    private boolean choose;

    public UpdatePacket(int state, boolean choose) {
        this.state = state;
        this.choose = choose;
    }

    public int getState() {
        return state;
    }

    public boolean isChoose() {
        return choose;
    }
}
