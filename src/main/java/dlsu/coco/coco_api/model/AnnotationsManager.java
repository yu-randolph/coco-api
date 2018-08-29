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

        corpusEditer.addnewFeaturevalue(jsonObject.get("name").toString(), jsonObject.get("value").toString(), jsonObject.get("description").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();

        return tiger2Converter;
    }

    public void setTiger(Tiger2Converter t2){
        this.tiger2Converter = t2;
    }

    public JSONObject AnnotationstoJSONconverter() throws JSONException {

        return tiger2Converter.AnnotationstoJSON(); }
}

