
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.TreeMap;


public class DictionaryDB extends TreeMap<String, JSONArray> {//just a thread-safe TreeMap with its methods overriden with 'synchronized' keyword

    DictionaryDB(){
        super();
    }

    public synchronized JSONArray get(String word){
        return super.get(word);
    }

    public synchronized JSONArray put(String word, JSONArray meaning){
        return super.put(word, meaning);
    }

    public synchronized JSONArray putIfAbsent(String word, JSONArray meaning){
        return super.putIfAbsent(word, meaning);
    }

    public synchronized JSONArray remove(String word) {
        return super.remove(word);
    }

    public synchronized String toString(){
        JSONObject jsonString = new JSONObject();
        JSONArray content = new JSONArray();
        for (Map.Entry<String, JSONArray> eachEntry: this.entrySet()){
            JSONObject eachLine = new JSONObject();
            eachLine.put("word", eachEntry.getKey());
            eachLine.put("meanings", eachEntry.getValue());
            content.add(eachLine);
        }
        jsonString.put("content", content);

        return jsonString.toString();
    }

}