import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {

    private String username;
    private int color;
    private int ID;
    private int conID;
    private int cIndex;
    private boolean shiny;

    public Player(String username, int color, int ID) {
        this.username = username;
        this.color = color;
        this.ID = ID;
        shiny = false;
    }

    public Player(int ID) {
        this.ID = ID;
    }

    //Player equality is based solely on ID
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Player) {
            Player p = (Player) obj;
            return p.ID == this.ID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public String getUsername() {
        return username;
    }

    public int getColor() {
        return color;
    }

    public int getID() {
        return ID;
    }

    public void setcIndex(int x) {
        cIndex = x;
    }

    public int getcIndex() {
        return cIndex;
    }

    public void setID(int x) {
        ID = x;
    }

    public void setConID(int x) {
        conID = x;
    }

    public int getConID() {
        return conID;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean getShiny() {
        return shiny;
    }

    public void setShiny() {
        shiny = true;
    }
}
