package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.*;
import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.CorpusCreater;
import dlsu.coco.coco_api.variables.TagContent;
import dlsu.coco.coco_api.variables.WordContent;
import org.eclipse.emf.common.util.EList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SuggestionFinder {

    private Corpus corpus;
    private ArrayList<ConcordanceContent> concordanceContents;

    private ArrayList<String> concept;
    private ConceptNet conceptNet;
    private ArrayList<String> concepts = new ArrayList<String>();
    private ArrayList<String> tags;
    private String pos;
    private String keyword;

    private ArrayList<ConcordanceContent> patternList = new ArrayList<>();

    public SuggestionFinder(Corpus corpus){
        this.corpus = corpus;
    }
    @SuppressWarnings("Duplicates")
    public void JSONparser(String sJsonContent)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(sJsonContent);
            JSONObject arr = new JSONObject(jsonObject.get("concepts").toString());
            JSONObject wn = new JSONObject(arr.get("wordnet").toString());

            Iterator<?> keys = wn.keys();
            ArrayList<String> conceptList = new ArrayList<>();

//            CorpusCreater cc = new CorpusCreater(tags,anno);
//            cc.annotationsToArrayList();
//            cc.sentencesToArrayList();
//            this.corpus = cc.getCorpus();

//        System.out.println("HELLO1 " + jsonObject.get("WORDNET").toString());
//        System.out.println("HELLO2 " + jsonObject.get("FORM_OF").toString());
//        System.out.println("HELLO3 " + concepts);

//        JSONObject jsonObject2 = new JSONObject(concepts);
//        concepts = concepts.substring(jsonObject2.get("Feature_Array").toString().length() + 2 + "Feature_Array".length() + 4);
//        JSONArray arr2 =  new JSONArray(jsonObject2.get("Feature_Array").toString());
//        System.out.println("HELLO4 " + concepts);
//        System.out.println("ARR2" + arr2);
//
//        JSONObject jsonObject3 = new JSONObject(concepts);
//        JSONArray arr3 =  new JSONArray(jsonObject3.get("Graph_Array").toString());
//
//        System.out.println("ARR3" + arr3);

            if(wn.toString().contains("NOUN")){
                pos = "NN";
            }
            else
                pos = "VB";


            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONArray contents = new JSONArray(wn.get(key).toString());
                for (int j = 0; j < contents.length(); j++) {
                    conceptList.add(contents.get(j).toString());
                }
            }

            JSONArray conceptNet = new JSONArray(arr.get("conceptnet").toString());

            for (int x = 0; x < conceptNet.length(); x++) {
                conceptList.add(conceptNet.get(x).toString());
            }
            this.concepts = conceptList;

            //PATTERN LIST
            //JSONObject jsonObjectPatterns = new JSONObject(jsonObject.get("patterns").toString());
            JSONArray jsonArrayPatterns = jsonObject.getJSONArray("patterns");
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

            keyword = jsonObject.getString("word");
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getSuggestions(String sJsonContent) throws JSONException
    {
        this.JSONparser(sJsonContent);

        ArrayList<String> finalSuggestions = new ArrayList<>();
        for(ConcordanceContent pattern : patternList)
        {
            System.out.println();
            System.out.println("PATTERN COMPARISON ! ");
            finalSuggestions.addAll(this.naive(pattern));
            System.out.println();
        }

        System.out.println("SUGGESTION FINAL");
        for(int i = 0; i < finalSuggestions.size(); i++)
        {
            System.out.println(finalSuggestions.get(i));
        }
        System.out.println();

        conceptNet = new ConceptNet(keyword);
        ArrayList<String> relatedTo = conceptNet.getRelatedContents();

        Segment corpusContent = corpus.getSegments().get(0);
        for(Graph sentence : corpusContent.getGraphs())
        {
            for(Terminal word : sentence.getTerminals())
            {
                if(relatedTo.contains(word.getWord().toLowerCase()))
                    finalSuggestions.add(word.getWord().toLowerCase());
            }
        }

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonSuggestions = new JSONArray();

        Set<String> uniqueSet = new HashSet<String>(finalSuggestions);
        for (String sKey : uniqueSet)
        {
            if(!concepts.contains(sKey))
            {
                System.out.println("Suggest : " + sKey + " : " + Collections.frequency(finalSuggestions, sKey));

                JSONObject jsonSuggestion = new JSONObject();
                jsonSuggestion.put("word", sKey);
                jsonSuggestion.put("frequency", Collections.frequency(finalSuggestions, sKey));
                jsonSuggestions.put(jsonSuggestion);
            }
        }

        jsonObject.put("suggestions", jsonSuggestions);
        System.out.println("SUGGESTIONS : " + jsonObject);
        return jsonObject;
    }

    public ArrayList<String> naive(ConcordanceContent pattern)
    {
        //COMPARE PATTERN WITH EACH SENTENCE IN THE CORPUS
        ArrayList<String> suggestions = new ArrayList<>();

        //EXTRACT EACH SENTENCE FROM THE CORPUS
        Segment corpusContent = corpus.getSegments().get(0);
        for(Graph sentence : corpusContent.getGraphs())
        {
            System.out.println();
            System.out.println("SENTENCE !");
            //BOYER MOORE ALGORITHM APPLICATION
            //BAD CHARACTER RULE
            EList<Terminal> sentenceWords = sentence.getTerminals();

            //TEXT = sentence
            //PATTERN = patternWords
            ArrayList<WordContent> patternWords = pattern.getWords();
            ArrayList<Integer> results = new ArrayList<>();

            if(patternWords.size() < sentenceWords.size())
            {
                int M = patternWords.size();
                int N = sentenceWords.size();

                for (int i = 0; i <= N - M; i++)
                {
                    int j;

                    for (j = 0; j < M; j++)
                    {
                        if (!compareWords(patternWords.get(j), sentenceWords.get(i + j)))
                            break;
                    }

                    if (j == M)
                    {
                        results.add(i);
                    }
                }

                //GET THE SUGGESTED WORD
                for(int resultCtr = 0; resultCtr < results.size(); resultCtr++)
                {
                    suggestions.add(sentenceWords.get(results.get(resultCtr) + pattern.getKeyword_Index()).getWord().toLowerCase());
                }
            }
        }

        return suggestions;
    }

    private Boolean compareWords(WordContent patternWord, Terminal corpusWord)
    {
        System.out.println("COMPARED : " + patternWord.getWord() + " && " + corpusWord.getWord());
        EList<Annotation> corpusTags = corpusWord.getAnnotations();
        ArrayList<TagContent> patternTags = patternWord.getTags();

        for(Annotation corpusTag : corpusTags)
        {
            for(TagContent patternTag : patternTags)
            {
                if(corpusTag.getName().equals(patternTag.getTagName()) && !corpusTag.getName().equals("lemma") && !patternTag.getTagName().equals("lemma") && !corpusTag.getName().equals("tiger2:word") && !corpusTag.getName().equals("xml:id"))
                {
                    if(!corpusTag.getValue().equals(patternTag.getTagValue()))
                    {
                        System.out.println("FALSE!");
                        return false;
                    }
                }
            }
        }

        System.out.println("TRUE!");
        return true;
    }
}
