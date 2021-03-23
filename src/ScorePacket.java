import java.io.Serializable;
import java.util.HashMap;

public class ScorePacket implements Serializable {

    private int ID;
    private int score;
    private HashMap<Integer, Integer> scores;

    public ScorePacket(int ID, int score) {
        this.ID = ID;
        this.score = score;
    }

    public ScorePacket(HashMap<Integer, Integer> scores) {
        this.scores = scores;
    }

    public HashMap<Integer, Integer> getScores() {
        return scores;
    }

    public int getID() {
        return ID;
    }

    public int getScore() {
        return score;
    }
}
