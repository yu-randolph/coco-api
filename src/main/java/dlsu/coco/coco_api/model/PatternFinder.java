package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.PatternNode;
import dlsu.coco.coco_api.variables.WordContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatternFinder {

    private JSONArray jsonConcordance;
    private JSONObject jsonSuper;

    private PatternNode keyword = new PatternNode();
    private ArrayList<PatternNode> left = new ArrayList<>();
    private ArrayList<PatternNode> right = new ArrayList<>();

    private ArrayList<ConcordanceContent> listSentence;

    public PatternFinder(JSONObject jsonSuper) throws JSONException {
        this.listSentence = new ArrayList<>();
        this.jsonSuper = jsonSuper;

        this.concordanceParser();
        this.findPattern();
    }

    private void concordanceParser() throws JSONException {
        jsonConcordance = jsonSuper.getJSONArray("CONCORDANCE");

        for(int conCtr = 0; conCtr < jsonConcordance.length(); conCtr++)
        {
            JSONObject jsonObject = jsonConcordance.getJSONObject(conCtr);
            listSentence.add(new ConcordanceContent());
            listSentence.get(listSentence.size()-1).readJSON(jsonObject);
        }
    }

    private void findPattern()
    {
        PatternNode keyword = new PatternNode();

        ArrayList<PatternNode> left = new ArrayList<>();
        ArrayList<PatternNode> right = new ArrayList<>();

        for(ConcordanceContent item : listSentence)
        {
            ArrayList<WordContent> wordContents = item.getWords();
            Integer keywordIndex = item.getKeyword_Index();

            int leftIndex = 0;
            int rightIndex = 0;

            for(int leftCtr = keywordIndex - 1; leftCtr >= 0; leftCtr--)
            {
                if(left.get(leftIndex) == null)
                {
                    left.add(new PatternNode());
                }

                left.get(leftIndex).addItem(wordContents.get(leftCtr).getLemma());
                leftIndex++;
            }

            for(int rightCtr = keywordIndex + 1; rightCtr < wordContents.size(); rightCtr++)
            {
                if(left.get(rightIndex) == null)
                {
                    right.add(new PatternNode());
                }

                right.get(rightIndex).addItem(wordContents.get(rightCtr).getLemma());
                rightIndex++;
            }

            keyword.addItem(item.getKeyword());
        }

        keyword.sort();

        for(int leftCtr = 0; leftCtr < left.size(); leftCtr++)
        {
            left.get(leftCtr).sort();
        }

        for(int rightCtr = 0; rightCtr < left.size(); rightCtr++)
        {
            right.get(rightCtr).sort();
        }

        this.keyword = keyword;
        this.left = left;
        this.right = right;
    }

    public JSONObject getJSONpattern() throws JSONException {
        JSONObject jsonPattern = new JSONObject();
        JSONArray jsonLeft = new JSONArray();
        JSONArray jsonRight = new JSONArray();
        JSONObject jsonKey = new JSONObject();

        for(int leftCtr = 0; leftCtr < left.size(); leftCtr++)
        {
            JSONObject jsonLeftObject = new JSONObject();
            jsonLeftObject.put("degree", leftCtr+1);
            jsonLeftObject.put("items", left.get(leftCtr).getJSONPatternItems());
            jsonLeft.put(jsonLeftObject);
        }

        for(int rightCtr = 0; rightCtr < right.size(); rightCtr++)
        {
            JSONObject jsonRightObject = new JSONObject();
            jsonRightObject.put("degree", rightCtr+1);
            jsonRightObject.put("items", right.get(rightCtr).getJSONPatternItems());
            jsonRight.put(jsonRightObject);
        }

        jsonPattern.put("key",jsonKey);
        jsonPattern.put("left", jsonLeft);
        jsonPattern.put("right", jsonRight);

        return jsonPattern;
    }
}
