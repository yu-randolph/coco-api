package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.CorpusCreater;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PatternFilter {

    private ArrayList<ConcordanceContent> patternList = new ArrayList<>();
    private ArrayList<ConcordanceContent> sentenceList = new ArrayList<>();
    private ArrayList<String> listID = new ArrayList<>();

    @SuppressWarnings("Duplicates")
    public void JSONparser(String sJsonContent)
    {
        try
        {
            System.out.println("PARSING JSON!");
            JSONObject jsonObject = new JSONObject(sJsonContent);

            JSONArray jsonConcordance = jsonObject.getJSONArray("concordances");

            for(int conCtr = 0; conCtr < jsonConcordance.length(); conCtr++)
            {
                JSONObject jsonConc = jsonConcordance.getJSONObject(conCtr);
                sentenceList.add(new ConcordanceContent());
                sentenceList.get(sentenceList.size()-1).readJSON(jsonConc);
            }

            //PATTERN IDs
            JSONArray jsonArray = jsonObject.getJSONArray("patternIds");
            if (jsonArray != null)
            {
                for (int i=0; i<jsonArray.length(); i++)
                {
                    listID.add(jsonArray.getString(i));
                }
            }

            //PATTERN LIST
            JSONObject jsonObjectPatterns = jsonObject.getJSONObject("patterns");
            JSONArray jsonArrayPatterns = jsonObjectPatterns.getJSONArray("patterns");
            for (int patternCtr = 0; patternCtr < jsonArrayPatterns.length(); patternCtr++)
            {
                ConcordanceContent pattern = new ConcordanceContent();
                JSONObject jsonPattern = jsonArrayPatterns.getJSONObject(patternCtr);
                pattern.readPatternJSON(jsonPattern);

                patternList.add(pattern);
                System.out.println("PATTERN CONTENT");
                pattern.printWordContents();
                System.out.println();
            }

            System.out.println("PATTERN LIST SIZE : " + patternList.size());
        } catch(JSONException e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("CONC SIZE : " + sentenceList.size());
        System.out.println("PAT SIZE :  " + patternList.size());
        System.out.println("ID SIZE : " + listID.size());
        System.out.println("");
    }

    public JSONObject getFilteredByID()  throws JSONException{
            JSONArray patterns = new JSONArray();
            ArrayList<String> originIDs = new ArrayList<>();

            for(ConcordanceContent pattern : patternList)
            {
                if(listID.contains(pattern.getSentenceId()))
                    originIDs.addAll(pattern.getPatternOrigin());
            }

            System.out.println("ORIGIN ID SIZE : " + originIDs.size());

        Set uniqueEntries = new HashSet();
        for (Iterator iter = originIDs.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (!uniqueEntries.add(element)) // if current element is a duplicate,
                iter.remove();                 // remove it
        }

        System.out.println("UNIQUE ID SIZE : " + uniqueEntries.size());

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for(ConcordanceContent concordance : sentenceList)
            {
                System.out.println("CONCORDANCE ID : " + concordance.getSentenceId());
                if(uniqueEntries.contains(concordance.getSentenceId()))
                {
                    jsonArray.put(concordance.getJSON());
                    uniqueEntries.remove(concordance.getSentenceId());
                    System.out.println("uniqueEntries : " + uniqueEntries.size());
                }
            }
            jsonObject.put("concordances", jsonArray);

            System.out.println(jsonObject.toString());
            return jsonObject;
    }
}
