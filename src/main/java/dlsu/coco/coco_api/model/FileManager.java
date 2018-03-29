package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusEditer;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.json.JSONObject;

import java.io.File;
import java.util.Properties;

public class FileManager {

    private String PATH = null;

    private Properties props;
    private StanfordCoreNLP pipeline;
    private Tiger2Converter tiger2Converter;
    private CorpusEditer corpusEditer;

    public FileManager()
    {
        tiger2Converter = new Tiger2Converter();
    }

    public void setPath(String path)
    {
        PATH = path;
    }

    //NLP Process
    public Annotation NLPprocessor(String content)
    {
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);

        Annotation document;

        document = new Annotation(content);
        pipeline.annotate(document);

        return document;
    }

    public void tigerXMLChecker(File file) {
        // CONVERT MODE = REMOVED
        // INPUT FILE WILL BE ADDED
        //add the file to the corpusList
        tiger2Converter.readFile(file);

    }

    public void tigerProcess(File file)
    {
        //pass here the output directory
        tiger2Converter.convertFiles(file.getParentFile(), Tiger2Converter.PARAMETERS.t2_t2);
        tiger2Converter.process();

    }

    public JSONObject XMLtoJSONconverter()
    {
        return tiger2Converter.XMLtoJSONconverter();
    }

    public JSONObject AnnotationstoJSONconverter(){ return tiger2Converter.AnnotationstoJSON(); }

    public String getRawCorpus(){

        return tiger2Converter.getRawText();
    }

    public void editAnnotation(String edition)
    {
        JSONObject jsonObject = new JSONObject(edition);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.editTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString(), jsonObject.get("feature_value").toString(), "");
        tiger2Converter.setCorpus(corpusEditer.getCorpus());

    }

    public void addAnnotation(String edition)
    {
        JSONObject jsonObject = new JSONObject(edition);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.addTerminalAnnotation(jsonObject.get("word_id").toString(), jsonObject.get("feature").toString(), jsonObject.get("feature_value").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());

    }

    public void addFeature(String feature){

    }

    public void addFeatureValue(String fv){
        JSONObject jsonObject = new JSONObject(fv);
        corpusEditer = new CorpusEditer(tiger2Converter.getCorpus());

        corpusEditer.addnewFeaturevalue(jsonObject.get("feature").toString(), jsonObject.get("featureValue").toString(), jsonObject.get("feature_decscripion").toString());
        tiger2Converter.setCorpus(corpusEditer.getCorpus());
    }
}

