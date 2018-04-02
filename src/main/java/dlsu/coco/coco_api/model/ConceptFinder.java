package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.Corpus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConceptFinder {

    private String pos,concept;
    private ArrayList<String> relatedWords,conceptResults;
    private ConceptNet conceptNet;
    private WordNet wordNet;
    private JSONObject wordnet;
    public ConceptFinder(String concept, String pos, String dictLocation) throws JSONException {

        this.pos = pos;
        this.concept = concept;
        this.conceptResults = new ArrayList<String>();
        conceptNet = new ConceptNet(concept);
        wordNet = new WordNet(System.getProperty("user.dir") + "\\WordNet-3.0\\WordNet-3.0\\dict", concept);
        this.getWordNetResult();

    }


    public JSONObject getWordNetResult() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if(pos.contains("Noun")) {
            jsonObject.put("NOUN_SYNONYM", wordNet.getNounSynonymJSONObject());
            jsonObject.put("NOUN_HYPONYM", wordNet.getNounHyponymJSONObject());
            jsonObject.put("NOUN_HYPERNYM", wordNet.getNounHypernymJSONObject());
            this.conceptResults.addAll(wordNet.relatedNouns());
        }
        else if(pos.contains("Verb")) {
            jsonObject.put("VERB_SYNONYM", wordNet.getVerbSynonymJSONObject());
            jsonObject.put("VERB_HYPONYM", wordNet.getVerbHyponymJSONObject());
            jsonObject.put("VERB_TROPONYM", wordNet.getVerbTroponymJSONObject());
            this.conceptResults.addAll(wordNet.relatedVerbs());
        }

//        jsonObject.put("ADJECTIVE_SYNONYM", wordNet.getAdjectiveSynonymJSONObject());
//        jsonObject.put("ADVERB_SYNONYM", wordNet.getAdverbSynonymJSONObject());

        wordnet = jsonObject;
        return jsonObject;
    }

    public JSONObject getAllResults() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("FORM_OF", conceptNet.getFormOfJSONObject());

//        jsonObject.put("RELATED_TO", conceptNet.getRelatedToJSONObject());

//        jsonObject.put("IS_A", conceptNet.getIsAJSONObject());
//        jsonObject.put("PART_OF", conceptNet.getPartOfJSONObject());
//        jsonObject.put("CREATED_BY", conceptNet.getCreatedByJSONObject());
        this.conceptResults.addAll(conceptNet.getContents());
        jsonObject.put("WORDNET", wordnet);
        System.out.println(jsonObject);
        return jsonObject;
    }
    public ArrayList<String> getConceptResults() {
        return this.conceptResults;
    }

}




//    public void getConceptNet(){
//        ConceptNet cp = new ConceptNet(this.concept);
//
//        for(String results : cp.getForms())
//             this.relatedWords.add(results);
//
//    }
//
//
//    public void getWordNet(){
//        WordNet wn = new WordNet("C:\\Users\\Micoh F Alvarez\\Desktop\\System needs\\WordNet-3.0\\WordNet-3.0\\dict",this.concept);
// C:\\Users\\Micoh\\Downloads\\WordNet-3.0\\WordNet-3.0\\dict"
//            for(String results : wn.getNounSynonym()){
//                this.relatedWords.add(results);
//            }
//    }
//
//    public void findConcepts(){
//
//            for(String relatedWord : relatedWords){
//                if(corpusWords.contains(relatedWord))
//                    this.conceptResults.add(relatedWord);
//
//            }
//    }
//

