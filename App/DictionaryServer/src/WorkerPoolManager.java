
import java.io.*;
import java.net.BindException;
import java.net.SocketException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;



public class WorkerPoolManager extends Thread {
    private TaskQueue taskQueue;
    private Worker[] workerPool;
    private int portNumber, workerPoolSize;
    private TaskReceiver taskReceiver;
    private DictionaryDB dictionaryDB;
    private String fileName;

    WorkerPoolManager(int portNumber, String dictionaryFile, int workerPoolSize) throws ClassCastException, IOException, ParseException {
        this.portNumber = portNumber;
        this.workerPoolSize = workerPoolSize;
        taskQueue = new TaskQueue();
        fileName = dictionaryFile;

        //initialize the worker pool;
        workerPool = new Worker[workerPoolSize];
        for (int i = 0; i < workerPoolSize; i++) {
            workerPool[i] = new Worker(taskQueue);
            workerPool[i].start();
        }

        //open the dictionary file and create dictionary data structure

        DictionaryDB dictionaryDB = new DictionaryDB();
        FileReader file = new FileReader(fileName);
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(file);
        JSONArray content = (JSONArray) obj.get("content");
        if (content != null/* && content.size() != 0*/) {
            for (Object eachRow : content) {
                dictionaryDB.put((String) ((JSONObject) eachRow).get("word"), (JSONArray) ((JSONObject) eachRow).get("meanings"));
            }
        } else System.out.println("given file is empty json");

        file.close();

        this.dictionaryDB = dictionaryDB;
    }

    public void run(){
        try {
            taskReceiver = new TaskReceiver(taskQueue, portNumber, dictionaryDB);
            taskReceiver.startTakingTask(); //start accepting incoming task
        } catch (BindException be) {
            System.out.println("Port number already in use: " + be.getMessage());
            System.exit(0);
        } catch (SocketException se) {
            System.out.println("Socket Exception: " + se.getMessage());
        } catch (UnsupportedEncodingException uste) {
            uste.printStackTrace();
            System.out.println("catching error in input/output stream encoding: "+uste.getMessage());
        } catch (IOException ioe) {
            System.out.println("Task Manager failing: " + ioe.getMessage());
        }
    }

    void killAllandSaveFile() throws IOException{ //this will be called when the terminate button is clicked

        for (int i = 0; i < workerPoolSize; i++) {
            workerPool[i].stopHandlingTask();
            workerPool[i].interrupt();

        }
        taskReceiver.stopTakingTask();

        //write file.
        FileWriter file = null;
        try {
            file = new FileWriter(fileName);
            file.write(dictionaryDB.toString());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (file != null){
                file.close();
            }
        }

        this.interrupt();
    }
}






