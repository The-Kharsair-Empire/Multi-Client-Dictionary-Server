
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Task implements Runnable {
    private Socket socket;
    private DictionaryDB db;
    private BufferedReader inChannel;
    private BufferedWriter outChannel;

    public Task(Socket socket, DictionaryDB dictionaryDB) throws IOException{
        this.socket = socket;
        this.db = dictionaryDB;
        inChannel = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        outChannel = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
    }

    public void run(){ //the task is just listening from client for operation, query in the thread-safe database and give response
        System.out.println("task is running");
        try {
            outChannel.write("Your connection request is established, now you can do operation on the dictionary database\n");
            System.out.println("Greeting to client is sent");
            outChannel.flush();
            JSONParser jsonParser = new JSONParser();
            JSONObject objectFromClient;
            String response, msgFromClient, op, word;
            JSONArray meanings;
            System.out.println("starting taking msg from client");
            while ((msgFromClient = inChannel.readLine()) != null) {
                System.out.println("request from client: " + msgFromClient);
                objectFromClient = (JSONObject) jsonParser.parse(msgFromClient);
                op = (String) objectFromClient.get("op");
                word = (String) objectFromClient.get("word");
                if (op.equals("QUERY")) {
                    meanings = db.get(word);
                    if (meanings == null) {
                        response = "word not found, no such word in the dictionary";
                    } else {
                        response = meanings.size() + (meanings.size() > 1?" meanings are ": " meaning is ") + "found:";
                        int count = 1;
                        for (Object meaning: meanings) {
                            response = response + "   " +count + " : " + meaning;
                            count++;
                        }
                    }
                } else if (op.equals("ADD")) {
                    meanings = new JSONArray();
                    meanings.add(objectFromClient.get("meaning"));
                    if (db.putIfAbsent(word, meanings) == null) {
                        response = "word " + word + " added successfully!";
                    } else {
                        response = "Failed, word already existed";
                    }

                } else if (op.equals("REMOVE")) {
                    if (db.remove(word) == null) {
                        response = "word not found!!";
                    } else {
                        response = "word removed!";
                    }

                } else if (op.equals("ADDADDITIONAL")) {
                    meanings = db.get(word);
                    if (meanings == null) {
                        response = "word not found, you must enter a existed word to add additional meaning!";

                    } else {
                        meanings.add(objectFromClient.get("meaning"));
                        //db.put(word, meanings); really need this one??
                        response = "additional meaning added";
                    }

                } else {
                    response = "invalid operation";
                }
                System.out.println("giving response: " + response);
                outChannel.write("Msg From Server: " + response +"\n");
                outChannel.flush();

            }
            socket.close();

        } catch (SocketException soe){
            System.out.println("the Task isn't connected to any client: " + soe.getMessage());
        } catch (ParseException pe) {
            System.out.println("some formatting issue in the incoming json string from client: " + socket);
        } catch (IOException ioe) {
            System.out.println("Some IO error happens during msg sending/receiving: " + ioe.getMessage());
            System.out.println("Possibly the queued client has already disconnected");
        }
    }
}
