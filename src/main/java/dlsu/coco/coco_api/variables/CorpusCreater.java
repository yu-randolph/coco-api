package dlsu.coco.coco_api.variables;

import de.hu_berlin.german.korpling.tiger2.Corpus;
import de.hu_berlin.german.korpling.tiger2.Terminal;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusEditer;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class CorpusCreater {

    Corpus corpus;
    CorpusWriter cw;
    CorpusEditer ce;
    JSONArray annotationContents, sentenceContents;
    ArrayList<String> featValues, featDesc, words;

    public CorpusCreater(JSONObject corpusConcents) throws JSONException {
        this.annotationContents = new JSONArray(corpusConcents.get("Feature_Array").toString());
        this.sentenceContents = new JSONArray(corpusConcents.get("Graph_Array").toString());
        featValues = new ArrayList<>();
        featValues = new ArrayList<>();
        words = new ArrayList<>();
        cw = new CorpusWriter(this.corpus);
    }

    public void annotationsToArrayList() throws JSONException {
        String featName;
        for (int i = 0; i < annotationContents.length(); i++) {
            JSONObject jObj = annotationContents.getJSONObject(i);
             featName = jObj.getString("Annotation");
            JSONArray jArr = new JSONArray(jObj.get("FeatureValues").toString());

            for(int j = 0; j < jArr.length(); j++){
                JSONObject jArr2 = jArr.getJSONObject(j);
                featValues.add(jArr2.getString("featValue"));
                featDesc.add(jArr2.getString("featDesc"));
            }

        }

        // cw.writeFeature(featName,featValues,featDesc);
    }

    public void sentencesToArrayList() throws JSONException {

       ArrayList<WordContent> words = new ArrayList<>();
       ArrayList<TagContent> tags = new ArrayList<>();
        WordContent wc = null;
        TagContent tc = null;

        for(int i = 0; i < sentenceContents.length(); i++) {
            JSONObject jObj = sentenceContents.getJSONObject(i);
            JSONArray jArr = new JSONArray(jObj.get("Terminal_Array").toString());
            JSONObject obj = jArr.getJSONObject(i);
            Iterator<?> keys = obj.keys();

            String graphId = jObj.getString("Graph_ID");

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONArray contents = new JSONArray(obj.get(key).toString());
                for (int j = 0; j < contents.length(); j++) {
                    tc = new TagContent(obj.get(key).toString(),contents.get(j).toString());
                }
                tags.add(tc);
            }
            wc = new WordContent(obj.get("Word").toString(), tags, obj.get("Terminal_Id").toString());
            words.add(wc);
        }
            for(WordContent word: words){
//                    cw.addTerminal(word.getWord(),word.getWordId());
            }
            for(WordContent word: words){
                    for(TagContent tag: word.getTags())
                    ce.addTerminalAnnotation(word.getWordId(),tag.getTagName(),tag.getTagValue());
            }

    }

    public void createCorpus(){


    }
}
