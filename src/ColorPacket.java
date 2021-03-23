import java.io.Serializable;
import java.util.List;

public class ColorPacket implements Serializable {

    private List<String> colors;
    private List<Integer> chosen;

    public ColorPacket(List<String> colors, List<Integer> chosen) {
        this.colors = colors;
        this.chosen = chosen;
    }

    public List<String> getColors() {
        return colors;
    }

    public List<Integer> getChosen() {
        return chosen;
    }
}
