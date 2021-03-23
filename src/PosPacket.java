import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class PosPacket implements Serializable {

    private HashMap<Player, List<Integer>> playersPos;

    public PosPacket(){}

    public PosPacket(HashMap<Player, List<Integer>> playersPos) {
        this.playersPos = playersPos;
    }

    public HashMap<Player, List<Integer>> getPlayersPos() {
        return playersPos;
    }
}
