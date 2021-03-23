import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Connections provides the means by which games communicate with each other
 */
public class Connection implements Runnable {

    private boolean isServer;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private boolean running;
    private Game game;

    public Connection(Socket socket) {
        isServer = true;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public Connection(Game game, Socket socket) {
        isServer = false;
        this.game = game;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            Object obj;
            try {
                obj = inputStream.readObject();
                if (isServer) {
                    Server.packetReceived(obj);
                } else {
                    game.packetReceived(obj);
                }
            } catch (EOFException | SocketException e) {
                running = false;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPacket(Object obj) {
        try {
            outputStream.reset();
            outputStream.writeObject(obj);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        running = false;
        inputStream.close();
        outputStream.close();
    }
}
