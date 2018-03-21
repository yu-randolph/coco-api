package dlsu.coco.coco_api;

import de.hu_berlin.german.korpling.tiger2.*;
import de.hu_berlin.german.korpling.tiger2.Corpus;
import de.hu_berlin.german.korpling.tiger2.Segment;
import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import dlsu.coco.coco_api.model.UploadedFile;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.JsonBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class FileManager {

    private String PATH = null;

    private Properties props;
    private StanfordCoreNLP pipeline;
    private Tiger2Converter tiger2Converter;

    public FileManager()
    {
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);

        tiger2Converter = new Tiger2Converter();
    }

    public void setPath(String path)
    {
        PATH = path;
    }

    //NLP Process
    private Annotation NLPprocessor(String content)
    {
        Annotation document;

        document = new Annotation(content);
        pipeline.annotate(document);

        return document;
    }

    public boolean tigerXMLChecker(File file)
    {
        return tiger2Converter.isTiger2(file);
    }

    public JSONObject XMLtoJSONconverter()
    {
        Corpus corpus = tiger2Converter.getCorpus();
        JSONObject jsonObject = new JSONObject();

        org.eclipse.emf.common.util.EList<Graph> content = corpus.getSegments().get(0).getGraphs();

        JSONArray listGraph = new JSONArray();
        //GET SENTENCE
        for(Graph itemGraph : content)
        {
            JSONObject Graph = new JSONObject();
            JSONArray listTerminal = new JSONArray();

            Graph.put("Graph_ID", itemGraph.getId().toString());

            //GET WORD
            for(Terminal itemTerminal : itemGraph.getTerminals())
            {
                JSONObject Terminal = new JSONObject();

                Terminal.put("Terminal_ID", itemTerminal.getId().toString());
                Terminal.put("Word", itemTerminal.getWord());

                //GET TAGS
                for(de.hu_berlin.german.korpling.tiger2.Annotation itemAnnotation : itemTerminal.getAnnotations())
                {
                    //itemAnnotation.getFeatureValueRef().getValue();
                    Terminal.put(itemAnnotation.getName(), itemAnnotation.getValue());
                }

                listTerminal.put(Terminal);
            }

            Graph.put("Terminal_Array", listTerminal);
            listGraph.put(Graph);
        }

        jsonObject.put("Graph_Array", listGraph);

        return jsonObject;
    }
}
