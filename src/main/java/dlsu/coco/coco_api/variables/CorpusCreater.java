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

    public Corpus getCorpus(){
        return this.corpus;
    }
    public CorpusCreater(String corpusContents) throws JSONException {
        JSONObject jsonObject2 = new JSONObject(corpusContents);
        corpusContents = corpusContents.substring(jsonObject2.get("Feature_Array").toString().length() + 2 + "Feature_Array".length() + 4);
        annotationContents =  new JSONArray(jsonObject2.get("Feature_Array").toString());

        JSONObject jsonObject3 = new JSONObject(corpusContents);
        sentenceContents=  new JSONArray(jsonObject3.get("Graph_Array").toString());

        featValues = new ArrayList<>();
        featDesc = new ArrayList<>();
        words = new ArrayList<>();
        cw = new CorpusWriter(this.corpus);
        cw.initCorpus();

    }

    public void annotationsToArrayList() throws JSONException {
        String featName = null;

        for (int i = 0; i < annotationContents.length(); i++) {
            JSONObject jObj = annotationContents.getJSONObject(i);

             featName =  jObj.getString("Annotation:");
            JSONArray jArr = new JSONArray(jObj.get("FeatureValues").toString());

            for(int j = 0; j < jArr.length(); j++){
                JSONObject jArr2 = jArr.getJSONObject(j);
                if(!jArr2.isNull("featDesc"))
                    featDesc.add(jArr2.getString("featDesc"));
                else
                    featDesc.add(" ");
                featValues.add(jArr2.getString("featValue"));

            }
            cw.writeFeature(featName,featValues,featDesc);
            featValues.clear();
            featDesc.clear();
        }
        ce = new CorpusEditer(cw.getCorpus());
    }

    public void sentencesToArrayList() throws JSONException {

       ArrayList<WordContent> words = new ArrayList<>();
       ArrayList<TagContent> tags;
        WordContent wc = null;
        TagContent tc = null;
        String graphId = null;

        for(int i = 0; i < sentenceContents.length(); i++) {
            JSONObject jObj = sentenceContents.getJSONObject(i);
            graphId = jObj.getString("Graph_ID");
            JSONArray jArr = new JSONArray(jObj.get("Terminal_Array").toString());
            for (int j = 0; j < jArr.length(); j++) {
                tags = new ArrayList<>();
                JSONObject obj = jArr.getJSONObject(j);

                System.out.println("obJ" + obj.toString());

                Iterator<?> keys = obj.keys();
                String word = (String) keys.next();
                word = obj.getString(word);

                String id = (String) keys.next();
                id = obj.getString(id);

                do{
                    String key = (String) keys.next();
                    System.out.println("KEYS " + key);
                    tc = new TagContent(key, obj.getString(key));
                    tags.add(tc);
                }while (keys.hasNext());

            wc = new WordContent(word, tags, id);

            words.add(wc);

        }

            cw.addGraph(graphId);

            for(WordContent word: words){
                cw.addTerminal(word.getWord(),word.getWordId());
            }
            for(WordContent word: words){

                for(TagContent tag: word.getTags()) {
                    System.out.println("HELLO: " + tag.getTagName());
                    ce.createTerminalAnnotation(word.getWordId(), tag.getTagName(), tag.getTagValue());
                }
            }
            words.clear();

        }

        this.corpus = ce.getCorpus();
    }


    public void createCorpus(){


    }
}
