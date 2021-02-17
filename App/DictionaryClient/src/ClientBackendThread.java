
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class ClientBackendThread extends Thread {
    private ClientGUIFunctionalities frame;

    private Socket socket;
    private BufferedReader inChannel;
    private BufferedWriter outChannel;
    public boolean connected;
    public String connectionAcceptedInfo;
    private boolean isRunning = true;
    private final int TIMEOUT = 300000;

    public ClientBackendThread(String serverAddress, int portNumber) throws IOException {
        connected = false;
        socket = new Socket(serverAddress, portNumber);
        inChannel = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        outChannel = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        System.out.println("connection request in the queue");

        System.out.println("waiting");
        connectionAcceptedInfo = inChannel.readLine();
        System.out.println("server reached, received greeting: "); //will first need to receiver and confirmation msg from server, then the client will officially started by instantiating ClientGUIFunctionalities
        System.out.println(connectionAcceptedInfo);
        connected = true;

        startClient(this);
    }

    public void run() {

        String msgFromServer;
        System.out.println("client start listening reply from server");

        try {
            socket.setSoTimeout(TIMEOUT);
            while (isRunning && (msgFromServer = inChannel.readLine()) != null) { // keep listening for the server response
                socket.setSoTimeout(TIMEOUT);
                System.out.println("reply received: " + msgFromServer);
                frame.showText(msgFromServer + "\n");
            }
//            System.out.println("server return null");
            connected = false;
            socket.close();
        } catch (SocketTimeoutException ste) {
            System.out.println("Client time out");
            System.exit(0);
        } catch (IOException ioe) {
            System.out.println("Problem with reaching the server: " + ioe.getMessage());
        }
    }

    private void startClient(ClientBackendThread communicator) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    frame = new ClientGUIFunctionalities(communicator);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void termiate() throws IOException {
        isRunning = false;
        if (socket != null) {
            socket.close();
        }
        this.interrupt();
    }

    public void send(String sendString) throws IOException {
        socket.setSoTimeout(0);
        System.out.println("sending:" + sendString);
        outChannel.write(sendString + '\n');
        outChannel.flush();
    }
}
