import java.io.Serializable;

public class QPacket implements Serializable {
    private Question question;
    private int type;

    public QPacket(Question question, int type) {
        this.question = question;
        this.type = type;
    }

    public Question getQuestion() {
        return question;
    }

    public int getType() {
        return type;
    }
}
