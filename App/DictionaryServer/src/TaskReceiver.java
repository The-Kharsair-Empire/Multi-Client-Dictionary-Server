
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TaskReceiver {
    private TaskQueue taskQueue;
    private ServerSocket serverSocket;
    private int portNumber;
    private DictionaryDB dictionaryDB;
    private boolean isTakingTask = true;
    TaskReceiver(TaskQueue taskQueue, int portNumber, DictionaryDB dictionaryDB) {
        this.taskQueue = taskQueue;
        this.portNumber = portNumber;
        this.dictionaryDB = dictionaryDB;
    }

    void startTakingTask() throws IOException {
        serverSocket = new ServerSocket(portNumber);
        while (isTakingTask) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("remote client " + clientSocket + " connected");
            taskQueue.add(new Task(clientSocket, dictionaryDB)); //turn each incoming connection into a task and add to task queue
            System.out.println("Task queued, keep listening on port: " + portNumber + " for new connection");
        }
    }

    void stopTakingTask() throws IOException{

        isTakingTask = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

}

