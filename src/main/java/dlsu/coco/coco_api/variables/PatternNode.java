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

    public ArrayList<String> getWordItems() {
        return wordItems;
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

        if(patternItems.size() > 0)
        {
            return jsonArray;
        }
        else
        {
            return null;
        }
    }

    public void sort()
    {
        if(!wordItems.isEmpty())
        {
            patternItems.add(new PatternItem(wordItems.get(0)));

            for(int wordCtr = 1; wordCtr < wordItems.size(); wordCtr++)
            {
                boolean isUnique = true;
                for(int patCtr = 0; patCtr < patternItems.size(); patCtr++)
                {
                    if(wordItems.get(wordCtr).equals(patternItems.get(patCtr).getsWord()))
                    {
                        patternItems.get(patCtr).increaseFreq();
                        isUnique = false;
                    }
                }

                if(isUnique)
                {
                    patternItems.add(new PatternItem(wordItems.get(wordCtr)));
                }
            }

            for(int i = 0; i < patternItems.size(); i++)
            {
                System.out.println("WORD: " + patternItems.get(i).getsWord() + " || FREQ: " + patternItems.get(i).getnFreq());
            }
            System.out.println();
        }
    }
}
