import java.io.Serializable;
import java.util.List;

public class pDownPacket implements Serializable {

    private List<Integer> pDown;
    private boolean up;

    public pDownPacket(List<Integer> pDown, boolean up) {
        this.pDown = pDown;
        this.up = up;
    }

    public List<Integer> getpDown() {
        return pDown;
    }

    public boolean isUp() {
        return up;
    }
}
