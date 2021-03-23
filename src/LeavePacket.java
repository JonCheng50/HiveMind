import java.io.Serializable;

public class LeavePacket implements Serializable {
    private Player p;

    public LeavePacket(Player p) {
        this.p = p;
    }

    public Player getP() {
        return p;
    }
}
