package dlsu.coco.coco_api.variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatternNode {
    private ArrayList<String> wordItems;
    private ArrayList<PatternItem> patternItems;

    public PatternNode()
    {
        wordItems = new ArrayList<>();
        patternItems = new ArrayList<>();
    }

    public void addItem(String word)
    {
        this.wordItems.add(word);
    }

    public ArrayList<PatternItem> getPatternItems() {
        return patternItems;
    }

    public JSONArray getJSONPatternItems() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(int ctr = 0; ctr < patternItems.size(); ctr++)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("word", patternItems.get(ctr).getsWord());
            jsonObject.put("freq", patternItems.get(ctr).getnFreq());
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    public void sort()
    {
        for(int wordCtr = 0; wordCtr < wordItems.size(); wordCtr++)
        {
            boolean bUnique = true;
            for(int ctr = 0; ctr < patternItems.size(); ctr++)
            {
                if(patternItems.get(ctr).equals(wordItems.get(wordCtr)))
                {
                    patternItems.get(ctr).increaseFreq();
                    bUnique = false;
                }
            }

            if(bUnique)
            {
                patternItems.add(new PatternItem(wordItems.get(wordCtr)));
            }
        }
    }
}
