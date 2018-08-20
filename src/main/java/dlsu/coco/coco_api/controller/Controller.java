package dlsu.coco.coco_api.controller;

import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusEditer;
import dlsu.coco.coco_api.model.*;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
public class Controller {

    private FileManager fileManager;
    private Tiger2Converter tiger2Converter;
    private CorpusEditer corpusEditer;
    private AnnotationsManager annotationsManager;
    private AnnotatingTool annotatingTool;
    private ConceptFinder cf;
    private Concordancer concordancer;
    private PatternFilter patternFilter;
    private PatternFinder patternFinder;
    private CoCoNet ccn;
    public Controller() throws ParseException, JSONException, IOException {
        fileManager = new FileManager();
        this.tiger2Converter = new Tiger2Converter();

    }

    @GetMapping("/")
    public String index() {
        return "WELCOME";
    }

//    @PostMapping("/upload")
//    public @ResponseBody
//    void upload(MultipartHttpServletRequest request, HttpServletResponse response) {
//        System.out.println("Trial");
//        Iterator<String> itr = request.getFileNames();
//
//        MultipartFile mpf = request.getFile(itr.next());
//        System.out.println(mpf.getOriginalFilename() + " uploaded!");
//
//        try {
//            //just temporary save file info into ufile
//            file.length = mpf.getBytes().length;
//            file.bytes = mpf.getBytes();
//            file.type = mpf.getContentType();
//            file.name = mpf.getOriginalFilename();
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        if (mpf.getContentType().equals(".xml")) {
//            System.out.println("XML!");
//        } else if (mpf.getContentType().equals(".txt")) {
//            System.out.println("TXT!");
//        }
//    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    String handleFileUpload(
            @RequestParam("file") MultipartFile file) {
        String name = "temp";
        File receivedFile = null;
        String result = "";

        if (!file.isEmpty()) {
            try {
                System.out.println(file.getContentType());
                if (file.getContentType().equals("text/plain")) {
                    name = file.getOriginalFilename();
                    //result = new String(file.getBytes().toString());
                    result = new String(file.getBytes(), "UTF-8");
                    System.out.println("You successfully uploaded " + name + " into " + FilenameUtils.removeExtension(name) + ".tiger2");
                    System.out.println("Content Type: " + file.getContentType());
                    System.out.println("Result: " + result);
                } else if (file.getContentType().equals("application/octet-stream")) {
                    System.out.println("entered XML area");
                    name = file.getOriginalFilename();
                    byte[] bytes = file.getBytes();
                    receivedFile = new File("src/" + name);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(receivedFile));
                    stream.write(bytes);
                    stream.close();
                    result = new String(bytes);
                    fileManager.setPath(name);
                    System.out.println(receivedFile.getPath());
                    System.out.println("You successfully uploaded " + name + " into " + name);
                    System.out.println("Content Type: " + file.getContentType());
                    System.out.println("Result: " + result);
                }

//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";

            } catch (Exception e) {
                System.out.println("You failed to upload " + name + " => " + e.getMessage());
                return "You failed to upload " + name + " => " + e.getMessage();
            } finally {
                if (file.getContentType().equals("text/plain"))
                    receivedFile = fileManager.NLPprocessor(result, FilenameUtils.removeExtension(name));
                fileManager.tigerXMLChecker(receivedFile);
                System.out.println("RECEIVED FILE NAME: " + receivedFile.getName());
                fileManager.tigerProcess(receivedFile);
                tiger2Converter = fileManager.getTiger2Converter();
                this.annotationsManager = new AnnotationsManager(tiger2Converter);
                this.annotatingTool = new AnnotatingTool(tiger2Converter);
                return fileManager.getRawCorpus();
            }
        } else {
            System.out.println("You failed to upload " + name + " because the file was empty.");
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/multiple-upload", method = RequestMethod.POST)
    public @ResponseBody
    String multipleUpload(@RequestParam("files") MultipartFile[] files) {
        String fileName = null;
        String corpus = "";
        File output = null;
        if (files != null && files.length > 0) {


            for (int i = 0; i < files.length; i++) {
                String name = "temp";
                String result = "";
                File receivedFile = null;
                try {

                    System.out.println(files[i].getContentType());
                    if (files[i].getContentType().equals("text/plain")) {
                        name = files[i].getOriginalFilename();
                        //result = new String(file.getBytes().toString());
                        result = new String(files[i].getBytes(), "UTF-8");
                        System.out.println("You successfully uploaded " + name + " into " + FilenameUtils.removeExtension(name) + ".tiger2");
                        System.out.println("Content Type: " + files[i].getContentType());
                        System.out.println("Result: " + result);
                        corpus += result + "\n";
                    } else if (files[i].getContentType().equals("application/octet-stream")) {
                        System.out.println("entered XML file area");
                        name = files[i].getOriginalFilename();
                        byte[] bytes = files[i].getBytes();
                        receivedFile = new File("src/" + name);
                        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(receivedFile));
                        stream.write(bytes);
                        stream.close();
                        result = new String(bytes);
                        fileManager.setPath(name);
                        System.out.println(receivedFile.getPath());
                        System.out.println("You successfully uploaded " + name + " into " + name);
                        System.out.println("Content Type: " + files[i].getContentType());
                        System.out.println("Result: " + result);
                        corpus += result + "\n";
                    }

                } catch (Exception e) {
                    return "You failed to upload " + fileName + ": " + e.getMessage() + "<br/>";
                } finally {
                    if (files[i].getContentType().equals("text/plain"))
                        receivedFile = fileManager.NLPprocessor(result, FilenameUtils.removeExtension(name));
                    fileManager.tigerXMLChecker(receivedFile);
                    System.out.println("RECEIVED FILE NAME: " + receivedFile.getName());
                    output = receivedFile;
                }
            }

            if (output != null) {
                System.out.println("Result: " + corpus);
                fileManager.tigerProcess(output);
                tiger2Converter = fileManager.getTiger2Converter();
                this.annotationsManager = new AnnotationsManager(tiger2Converter);
                this.annotatingTool = new AnnotatingTool(tiger2Converter);
                return fileManager.getRawCorpus();
            }


        } else {
            return "Unable to upload. File is empty.";
        }
        return "No upload";
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/editTag", method = RequestMethod.POST)
    public @ResponseBody
    String editTag(@RequestParam("tagInfo") String tagInfo) {
        String newTag = "";
        if (!tagInfo.isEmpty()) {
            try {

                byte[] bytes = tagInfo.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotatingTool.editAnnotation(result);
                this.annotationsManager.setTiger(this.tiger2Converter);




                System.out.println("You successfully passed the " + newTag + " into " + result + "using the editTag method");
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to update " + newTag + " => " + e.getMessage() + "using the editTag method");
                return "You failed to update " + newTag + " => " + e.getMessage() + "using the editTag method";
            }
        }
        return newTag;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/addTag", method = RequestMethod.POST)
    public @ResponseBody
    String addTag(@RequestParam("tagInfo") String tagInfo) {
        String newTag = "";
        if (!tagInfo.isEmpty()) {
            try {
                byte[] bytes = tagInfo.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotatingTool.addAnnotation(result);
                this.annotationsManager.setTiger(this.tiger2Converter);

                System.out.println("You successfully uploaded " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to update " + newTag + " => " + e.getMessage() + "using the addTag method");
                return "You failed to update " + newTag + " => " + e.getMessage() + "using the addTag method";
            }
        }
        return newTag;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/deleteTag", method = RequestMethod.POST)
    public @ResponseBody
    String deleteTag(@RequestParam("tagInfo") String tagInfo) {
        String newTag = "";
        if (!tagInfo.isEmpty()) {
            try {
                byte[] bytes = tagInfo.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotatingTool.deleteAnnotation(result);
                this.annotationsManager.setTiger(this.tiger2Converter);

                System.out.println("You successfully updated the " + newTag + " into " + result);
                System.out.println("Result: " + result);

                return result;
            } catch (Exception e) {
                System.out.println("You failed to update " + newTag + " => " + e.getMessage() + "using the addTag method");
                return "You failed to update " + newTag + " => " + e.getMessage() + "using the addTag method";
            }
        }
        return newTag;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/addFeatValue", method = RequestMethod.POST)
    public @ResponseBody
    String addFeatValue(@RequestParam("tags") String tags) {
        String newTag = "";
        if (!tags.isEmpty()) {
            try {

                byte[] bytes = tags.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotationsManager.addFeatureValue(result);

                System.out.println("You successfully updated the " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to update " + newTag + " => " + e.getMessage() + "using the addFeatValue method");
                return "You failed to update " + newTag + " => " + e.getMessage() + "using the addTag method";
            }
        }
        return newTag;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/applyToAll", method = RequestMethod.POST)
    public @ResponseBody
    String applyToAll(@RequestParam("tagInfo") String tagInfo) {
        String newTag = "";
        if (!tagInfo.isEmpty()) {
            try {

                byte[] bytes = tagInfo.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotatingTool.applyToAll(result);
                this.annotationsManager.setTiger(this.tiger2Converter);

                System.out.println("You successfully updated the " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to update " + newTag + " => " + e.getMessage() + "using the applyAddToAll method");
                return "You failed to update " + newTag + " => " + e.getMessage() + "using the addTag method";
            }
        }
        return newTag;
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/addFeature", method = RequestMethod.POST)
    public @ResponseBody
    String addFeature(@RequestParam("feature") String feature) {
        String newTag = "";
        if (!feature.isEmpty()) {
            try {

                byte[] bytes = feature.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotationsManager.addFeature(result);

                System.out.println("You successfully updated the " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to update " + newTag + " => " + e.getMessage() + "using the addFeature method");
                return "You failed to update " + newTag + " => " + e.getMessage() + "using the addFeature method";
            }
        }
        return newTag;
    }
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getConcepts", method = RequestMethod.POST)
    public @ResponseBody
    String getConcepts(@RequestParam("searchInfo") String searchInfo) {
        String concepts = "";
        if (!searchInfo.isEmpty()) {
            try {

                byte[] bytes = searchInfo.getBytes();
                String result = new String(bytes);
                concepts = result;
                System.out.println(result);

                JSONObject jsonObject = new JSONObject(result);
                System.out.println(result);
                this.cf = new ConceptFinder(jsonObject.get("concept").toString(),jsonObject.get("pos").toString(),"");
                result = cf.getAllResults().toString();

                System.out.println("Result: " + result);

                return result;
            } catch (Exception e) {
                System.out.println("You failed to retrieve " + concepts + " => " + e.getMessage() + "using the getConcepts method");
                return "You failed to retrieve " + concepts + " => " + e.getMessage() + "using the getConcepts method";
            }
        }
        return concepts;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getConcordances", method = RequestMethod.POST)
    public @ResponseBody
    String getConcordances(@RequestParam("searchInfo") String searchInfo) {
        String data = "";
        if (!searchInfo.isEmpty()) {
            try {

                byte[] bytes = searchInfo.getBytes();
                String corpus = new String(bytes);
                data = corpus;
                System.out.println("pos" + corpus);

                concordancer = new Concordancer(tiger2Converter.getCorpus());
                concordancer.JSONtoArrayConceptLIst(corpus);
                corpus = concordancer.getConcordanceResult().toString();
                System.out.println("Result: " + corpus);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return corpus;
            } catch (Exception e) {
                System.out.println("You failed to retrieve " + data + " => " + e.getMessage() + "using the getConcordances method");
                return "You failed to upload " + data + " => " + e.getMessage();
            }
        }
        return data;
    }

//    @CrossOrigin(origins = "*")
//    @RequestMapping(value = "/getAdvancedConcepts", method = RequestMethod.POST)
//    public @ResponseBody
//    String getAdvancedConcepts(@RequestParam("jsonConcept_Corpus") String jsonConcept_Corpus) {
//        String concepts = "";
//        if (!jsonConcept_Corpus.isEmpty()) {
//            try {
//
//                byte[] bytes = jsonConcept_Corpus.getBytes();
//                String result = new String(bytes);
//                concepts = result;
//                System.out.println(result);
//
//                JSONObject jsonObject = new JSONObject(result);
//                System.out.println(result);
//                this.cf = new ConceptFinder(jsonObject.get("concept").toString(),jsonObject.get("pos").toString(),"");
//                result = cf.getAllResults().toString();
//
//                System.out.println("Result: " + result);
//
//                return result;
//            } catch (Exception e) {
//                System.out.println("You failed to retrieve " + concepts + " => " + e.getMessage() + "using the getConcepts method");
//                return "You failed to retrieve " + concepts + " => " + e.getMessage() + "using the getConcepts method";
//            }
//        }
//        return concepts;
//    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/saveConcepts", method = RequestMethod.POST)
    public @ResponseBody
    String saveConcepts(@RequestParam("savedConcepts") String savedConcepts) {
        String concepts = "";
        if (!savedConcepts.isEmpty()) {
            try {

                byte[] bytes = savedConcepts.getBytes();
                String result = new String(bytes);
                concepts = result;
                System.out.println("HELLO" + result);

                this.ccn = new CoCoNet();
                this.ccn.overwriteConcepts(result);

                return result;
            } catch (Exception e) {
                System.out.println("You failed to retrieve " + concepts + " => " + e.getMessage() + "using the getConcepts method");
                return "You failed to retrieve " + concepts + " => " + e.getMessage() + "using the getConcepts method";
            }
        }
        return concepts;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getAdvancedConcordances", method = RequestMethod.POST)
    public @ResponseBody
    String getAdvancedConcordances(@RequestParam("jsonConcept_Corpus") String searchInfo) {
        String newTag = "";
        if (!searchInfo.isEmpty()) {
            try {

                byte[] bytes = searchInfo.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                System.out.println(result);
                concordancer = new Concordancer(tiger2Converter.getCorpus());
                concordancer.JSONToArrayAdvancedConceptList(result);
                result = concordancer.getAdvancedResults().toString();
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to upload " + newTag + " => " + e.getMessage());
                return "You failed to upload " + newTag + " => " + e.getMessage();
            }
        }
        return newTag;
    }
//    @GetMapping("/get/{value}")
//    public void get(HttpServletResponse response, @PathVariable String value)
//    {
//        try {
//
//            response.setContentType(file.type);
//            response.setContentLength(file.length);
//            FileCopyUtils.copy(file.bytes, response.getOutputStream());
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAnnotations")
    public String getAnnotations() throws JSONException {

//       ConceptFinder fn = new ConceptFinder("Hello", "book");
            return fileManager.XMLtoJSONconverter().toString();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getTags")
    public String getTags() throws JSONException {
        // fileManager.tigerXMLChecker(new File("C:\\Users\\Micoh F Alvarez\\Desktop\\test.xml.tiger2"));
        // fileManager.tigerProcess(new File("C:\\Users\\Micoh F Alvarez\\Desktop\\test.xml.tiger2"));
        return annotationsManager.AnnotationstoJSONconverter().toString();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getPattern", method = RequestMethod.POST)
    public @ResponseBody
    String getPattern(@RequestParam("jsonConcordance") String patternInfo) {
        System.out.println(patternInfo);
        if (!patternInfo.isEmpty()) {
            try
            {
                byte[] bytes = patternInfo.getBytes();
                String result = new String(bytes);
                System.out.println(result);

                patternFinder = new PatternFinder(new JSONObject(patternInfo));
                return patternFinder.getJSONpattern().toString();
            } catch (Exception e) {
                System.out.println("FAILED TO FIND PATTERNS");
                System.out.println("You failed to upload " + patternInfo + " => " + e.getMessage());
                return "You failed to upload " + patternInfo + " => " + e.getMessage();
            }
        }
        else
        {
            System.out.println("EMPTY!");
        }
        return patternInfo;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/filterPatterns", method = RequestMethod.POST)
    public @ResponseBody
    String getFilteredPatternByID(@RequestParam("filters") String filters) throws JSONException {
        if (!filters.isEmpty()) {
            try
            {
                byte[] bytes = filters.getBytes();
                String result = new String(bytes);
                System.out.println("PATTENR FILTER ; JSONPARSER");
                patternFilter = new PatternFilter();
                patternFilter.JSONparser(filters);
                System.out.println("PATTENR FILTER ; GETFILTEREDPATTERNS");
                String filtered = patternFilter.getFilteredByID().toString();
                return filtered;
            } catch (Exception e) {
                System.out.println("You failed to upload " + filters + " => " + e.getMessage());
                return "You failed to upload " + filters + " => " + e.getMessage();
            }
        }
        return filters;
    }
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getSuggestions", method = RequestMethod.POST)
    public @ResponseBody
    String getSuggestions(@RequestParam("suggestionInfo") String suggestionInfo) {
        if (!suggestionInfo.isEmpty()) {
            try
            {
                byte[] bytes = suggestionInfo.getBytes();
                String result = new String(bytes);
                SuggestionFinder suggestionFinder = new SuggestionFinder(tiger2Converter.getCorpus());
                return suggestionFinder.getSuggestions(result).toString();
            } catch (Exception e) {
                System.out.println("You failed to upload " + suggestionInfo + " => " + e.getMessage());
                return "You failed to upload " + suggestionInfo + " => " + e.getMessage();
            }
        }
        return suggestionInfo;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getPatternSearch", method = RequestMethod.POST)
    public @ResponseBody
    String getPatternSearch(@RequestParam("jsonConcordance_PatternString") String jsonConcordance_PatternString) {
        if (!jsonConcordance_PatternString.isEmpty()) {
            try
            {
                byte[] bytes = jsonConcordance_PatternString.getBytes();
                String result = new String(bytes);
                PatternSearcher patternSearcher = new PatternSearcher(new JSONObject(jsonConcordance_PatternString));
                return patternSearcher.findPattern().toString();
            } catch (Exception e) {
                System.out.println("You failed to upload " + jsonConcordance_PatternString + " => " + e.getMessage());
                return "You failed to upload " + jsonConcordance_PatternString + " => " + e.getMessage();
            }
        }
        return jsonConcordance_PatternString;
    }

//    @GetMapping("/getXML")
//    public String getXML() {
//        return fileManager.NLPprocessor("I happened to see a one day cricket match between Pakistan and Australia at Wankhade Stadium, Mumbai. " +
//                "I went for a fun. But I wit\u00ADnessed a horrible sight. Two thousand ticketless cricket fans gate crashed. " +
//                "There was a stampede. Three persons died and twenty were injured. Administration was responsible for it.");
//    }
}

