package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusEditer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AnnotationsManager {

    private CorpusEditer corpusEditer;
    private Tiger2Converter tiger2Converter;

    public AnnotationsManager(Tiger2Converter tiger2Converter){
         this.tiger2Converter = tiger2Converter;
    }

    public Tiger2Converter editAnnotation(String edition) throws JSONException {
        JSONObject jsonObject = new JSONObject(edition);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.editTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString(), jsonObject.get("feature_value").toString(), "");
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }

    public Tiger2Converter applyToAll(String feature) throws JSONException {

        JSONObject jsonObject = new JSONObject(feature);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());
        corpusEditer.applyToAll(jsonObject.get("word").toString(),jsonObject.get("feature").toString(),jsonObject.get("feature_value").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }
    public Tiger2Converter deleteTag(String edition) throws JSONException {
        JSONObject jsonObject = new JSONObject(edition);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.deleteTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }
    public Tiger2Converter addAnnotation(String edition) throws JSONException {
        JSONObject jsonObject = new JSONObject(edition);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.addTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString(), jsonObject.get("feature_value").toString());

        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }

    public Tiger2Converter addFeature(String feature) throws JSONException {

        JSONObject jsonObject = new JSONObject(feature);
        JSONArray arr = new JSONArray(jsonObject.get("values").toString());

        ArrayList<String> val = new ArrayList<String>();
        ArrayList<String> desc = new ArrayList<String>();

        for(int i = 0; i < arr.length(); i++){
            val.add(arr.getJSONObject(i).getString("value"));
            desc.add(arr.getJSONObject(i).getString("description"));
        }

        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.addnewFeature(jsonObject.get("featureName").toString(), val,desc);
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();

        return tiger2Converter;
    }

    public Tiger2Converter addFeatureValue(String fv) throws JSONException {
        JSONObject jsonObject = new JSONObject(fv);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.addnewFeaturevalue(jsonObject.get("feature").toString(), jsonObject.get("featureValue").toString(), jsonObject.get("feature_decscripion").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();

        return tiger2Converter;
    }

    public JSONObject AnnotationstoJSONconverter() throws JSONException { return tiger2Converter.AnnotationstoJSON(); }
}

