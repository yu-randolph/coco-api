package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusEditer;
import org.json.JSONException;
import org.json.JSONObject;

public class AnnotatingTool {

    private CorpusEditer corpusEditer;
    private Tiger2Converter tiger2Converter;

    public AnnotatingTool(Tiger2Converter tiger2Converter){
        this.tiger2Converter = tiger2Converter;
    }


    public Tiger2Converter editAnnotation(String annotation) throws JSONException {
        JSONObject jsonObject = new JSONObject(annotation);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.editTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString(), jsonObject.get("feature_value").toString(), "");
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }

    public Tiger2Converter applyToAll(String annotation) throws JSONException {

        JSONObject jsonObject = new JSONObject(annotation);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());
        corpusEditer.applyToAll(jsonObject.get("word").toString(),jsonObject.get("feature").toString(),jsonObject.get("feature_value").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }
    public Tiger2Converter deleteAnnotation(String annotation) throws JSONException {
        JSONObject jsonObject = new JSONObject(annotation);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());
        System.out.println("DELEING HERE");
        corpusEditer.deleteTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }
    public Tiger2Converter addAnnotation(String annotation) throws JSONException {
        JSONObject jsonObject = new JSONObject(annotation);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.addTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString(), jsonObject.get("feature_value").toString());

        tiger2Converter.setCorpus(corpusEditer.getCorpus());
        tiger2Converter.saveChanges();
        return tiger2Converter;

    }

}
