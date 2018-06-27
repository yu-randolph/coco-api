package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusEditer;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.types.Tags;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class FileManager {

    private String PATH = null;

    private Properties props;
    private StanfordCoreNLP pipeline;
    private Tiger2Converter tiger2Converter;
    private PatternFinder patternFinder;
    private CorpusEditer corpusEditer;

    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    private  ConceptFinder cf;
    public FileManager()
    {
        tiger2Converter = new Tiger2Converter();
    }

    public void setPath(String path)
    {
        PATH = path;
    }

    public File NLPprocessor(String content, String filename)
    {
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(content);
        pipeline.annotate(document);

        return this.Text_to_TigerXML(document, filename);
    }

    public File Text_to_TigerXML(Annotation document, String filename)
    {
        try
        {
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        Document doc = docBuilder.newDocument();

        //CORPUS
        Element corpusElement = doc.createElement("corpus");
        corpusElement.setAttribute("xmlns", "http://korpling.german.hu-berlin.de/tiger2/V2.0.4/");
        corpusElement.setAttribute("xmlns:tiger2", "http://korpling.german.hu-berlin.de/tiger2/V2.0.4/");
        corpusElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        corpusElement.setAttribute("xml:id", "example_inline_coref_pro");
        corpusElement.setAttribute("xsi:schemaLocation", "http://korpling.german.hu-berlin.de/tiger2/V2.0.4/ http://korpling.german.hu-berlin.de/tiger2/V2.0.4/Tiger2.xsd");

        //HEAD
        Element headElement = doc.createElement("head");

        //BODY
        Element bodyElement = doc.createElement("body");

        //META
        Element metaElement = doc.createElement("meta");

        //ANNOTATION
        Element annotationElement = doc.createElement("annotation");

        //POS FEATURE
        Element pos_featureElement = doc.createElement("feature");
        pos_featureElement.setAttribute("name", "pos");
        pos_featureElement.setAttribute("domain", "t");
        annotationElement.appendChild(pos_featureElement);
        ArrayList<String> all_pos = new ArrayList<>();

        //NER FEATURE
        Element ner_featureElement = doc.createElement("feature");
        ner_featureElement.setAttribute("name", "ner");
        ner_featureElement.setAttribute("domain", "t");
        annotationElement.appendChild(ner_featureElement);
        ArrayList<String> all_ner = new ArrayList<>();

        Element segmentElement = doc.createElement("s");
        segmentElement.setAttribute("xml:id","s1");

        //LEMMA FEATURE
        Element lemmaElement = doc.createElement("feature");
        lemmaElement.setAttribute("name", "lemma");
        lemmaElement.setAttribute("domain", "t");
        annotationElement.appendChild(lemmaElement);

        //CONTENT
        for(int graph_id = 0; graph_id < sentences.size(); graph_id++)
        {
            System.out.println("xml:id" + "_" + "g" + graph_id);
            Element graphElement = doc.createElement("graph");
            //graphElement.setAttribute("root", segmentElement.getAttribute("xml:id") + "_" + "g" + graph_id);
            graphElement.setAttribute("xml:id", segmentElement.getAttribute("xml:id") + "_g" +graph_id);

            Element terminalElement = doc.createElement("terminals");

            Integer terminal_id = 1;
            for (CoreLabel token : sentences.get(graph_id).get(CoreAnnotations.TokensAnnotation.class))
            {
                //System.out.println(String.format("Print: word: [%s] pos: [%s] ner: [%s] Lemma: [%s]", word, pos, ner, lemma));
                //<t xml:id="s1_t1" tiger2:word="I" pos="PP" />
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);

                Element tElement = doc.createElement("t");

                if(!lemma.isEmpty())
                {
                    tElement.setAttribute("lemma", lemma);
                }
                if(!ner.isEmpty())
                {
                    tElement.setAttribute("ner", ner);
                    all_ner.add(ner);
                }
                if(!pos.isEmpty())
                {
                    tElement.setAttribute("pos", pos);
                    all_pos.add(pos);
                }
                if(!word.isEmpty())
                {
                    tElement.setAttribute("tiger2:word", word);
                }

                tElement.setAttribute("xml:id", "s" + graph_id + "_t" + terminal_id);

                terminalElement.appendChild(tElement);
                terminal_id++;
            }

            graphElement.appendChild(terminalElement);
            segmentElement.appendChild(graphElement);
        }

        Set<String> unique_pos = new HashSet<>(all_pos);
        Set<String> unique_ner = new HashSet<>(all_ner);

        for(String pos : unique_pos)
        {
            Element valueElement = doc.createElement("value");
            valueElement.setAttribute("name", pos);
            valueElement.setTextContent(this.pos_tag_definition(pos));
            pos_featureElement.appendChild(valueElement);
        }

        for(String ner : unique_ner)
        {
            Element valueElement = doc.createElement("value");
            valueElement.setAttribute("name", ner);
            valueElement.setTextContent(ner);
            ner_featureElement.appendChild(valueElement);
        }

        headElement.appendChild(annotationElement);
        bodyElement.appendChild(segmentElement);

        corpusElement.appendChild(headElement);
        corpusElement.appendChild(bodyElement);

        doc.appendChild(corpusElement);

        try
        {
            System.out.println("CREATING FILE");
            File file = new File("src/" + filename + ".tiger2");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            return file;
        }
        catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
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
    public Tiger2Converter getTiger2Converter() {
        return tiger2Converter;
    }

    public JSONObject XMLtoJSONconverter() throws JSONException {
        System.out.println(tiger2Converter.XMLtoJSONconverter().toString());
        return tiger2Converter.XMLtoJSONconverter();
    }

    public JSONObject AnnotationstoJSONconverter() throws JSONException { return tiger2Converter.AnnotationstoJSON(); }

    public String getRawCorpus(){

        return tiger2Converter.getRawText();
    }

    private String pos_tag_definition(String tag)
    {
        switch(tag.toUpperCase())
        {
            case "CC" : return "Coordinating conjunction";
            case "CD" : return "Cardinal number";
            case "DT" : return "Determiner";
            case "EX" : return "Existential there";
            case "FW" : return "Foreign word";
            case "IN" : return "Preposition or subordinating conjunction";
            case "JJ" : return "Adjective";
            case "JJR" : return "Adjective, comparative";
            case "JJS" : return "Adjective, superlative";
            case "LS" : return "List item marker";
            case "MD" : return "Modal";
            case "NN" : return "Noun, singular or mass";
            case "NNS" : return "Noun, plural";
            case "NNP" : return "Proper noun, singular";
            case "NNPS" : return "Proper noun, plural";
            case "PDT" : return "Predeterminer";
            case "POS" : return "Possessive ending";
            case "PRP" : return "Personal pronoun";
            case "PRP$" : return "Possessive pronoun";
            case "RB" : return "Adverb";
            case "RBR" : return "Adverb, comparative";
            case "RBS" : return "Adverb, superlative";
            case "RP" : return "Particle";
            case "SYM" : return "Symbol";
            case "TO" : return "to";
            case "UH" : return "Interjection";
            case "VB" : return "Verb, base form";
            case "VBD" : return "Verb, past tense";
            case "VBG" : return "Verb, gerund or present participle";
            case "VBN" : return "Verb, past participle";
            case "VBP" : return "Verb, non­3rd person singular present";
            case "VBZ" : return "Verb, 3rd person singular present";
            case "WDT" : return "Wh­determiner";
            case "WP" : return "Wh­pronoun";
            case "WP$" : return "Possessive wh­pronoun";
            case "WRB" : return "Wh­adverb";
            default: return "";
        }
    }

    public String getPattern(String concordance) throws JSONException {
        System.out.println("CONCORDANCE IN GET PATTERN : " + concordance);
        patternFinder = new PatternFinder(new JSONObject(concordance));
        patternFinder.printPattern();
        System.out.println("PATTERN : " + patternFinder.getJSONpattern().toString());
        return patternFinder.getJSONpattern().toString();
    }

    public String getFilteredPattternByID(String sID) throws JSONException {
        if(patternFinder != null)
        {
            return patternFinder.getFilteredByID(new JSONObject(sID)).toString();
        }
        else
        {
            return null;
        }
    }
}

// {"AnnotationsList":[{"annotation":"Adverb"},{"annotation":"Subject"}],"RelationList":[{"relation":"synonym"}"concept":"book","pos":"Noun"}


