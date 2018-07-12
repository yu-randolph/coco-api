package dlsu.coco.coco_api.controller;

import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusEditer;
import dlsu.coco.coco_api.model.*;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;
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
    private ConceptFinder cf;
    private Concordancer concordancer;
    public Controller() {
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
                return fileManager.getRawCorpus();
            }
        } else {
            System.out.println("You failed to upload " + name + " because the file was empty.");
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

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
                return fileManager.getRawCorpus();
            }


        } else {
            return "Unable to upload. File is empty.";
        }
        return "No upload";
    }

    @RequestMapping(value = "/editTag", method = RequestMethod.POST)
    public @ResponseBody
    String editTag(@RequestParam("tags") String tags) {
        String newTag = "";
        if (!tags.isEmpty()) {
            try {

                byte[] bytes = tags.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotationsManager.editAnnotation(result);


                System.out.println("You successfully uploaded " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to upl   oad " + newTag + " => " + e.getMessage());
                return "You failed to upload " + newTag + " => " + e.getMessage();
            }
        }
        return newTag;
    }

    @RequestMapping(value = "/addTag", method = RequestMethod.POST)
    public @ResponseBody
    String addTag(@RequestParam("tags") String tags) {
        String newTag = "";
        if (!tags.isEmpty()) {
            try {
                byte[] bytes = tags.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotationsManager.addAnnotation(result);

                System.out.println("You successfully uploaded " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to upl   oad " + newTag + " => " + e.getMessage());
                return "You failed to upload " + newTag + " => " + e.getMessage();
            }
        }
        return newTag;
    }

    @RequestMapping(value = "/deleteTag", method = RequestMethod.POST)
    public @ResponseBody
    String deleteTag(@RequestParam("tags") String tags) {
        String newTag = "";
        if (!tags.isEmpty()) {
            try {
                byte[] bytes = tags.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotationsManager.deleteTag(result);

                System.out.println("You successfully uploaded " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to upl   oad " + newTag + " => " + e.getMessage());
                return "You failed to upload " + newTag + " => " + e.getMessage();
            }
        }
        return newTag;
    }

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

                System.out.println("You successfully uploaded " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to upl   oad " + newTag + " => " + e.getMessage());
                return "You failed to upload " + newTag + " => " + e.getMessage();
            }
        }
        return newTag;
    }

    @RequestMapping(value = "/applyToAll", method = RequestMethod.POST)
    public @ResponseBody
    String applyToAll(@RequestParam("tags") String tags) {
        String newTag = "";
        if (!tags.isEmpty()) {
            try {

                byte[] bytes = tags.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                this.tiger2Converter = this.annotationsManager.applyToAll(result);


                System.out.println("You successfully uploaded " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to upl   oad " + newTag + " => " + e.getMessage());
                return "You failed to upload " + newTag + " => " + e.getMessage();
            }
        }
        return newTag;
    }



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

                System.out.println("You successfully uploaded " + newTag + " into " + result);
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return result;
            } catch (Exception e) {
                System.out.println("You failed to upl   oad " + newTag + " => " + e.getMessage());
                return "You failed to upload " + newTag + " => " + e.getMessage();
            }
        }
        return newTag;
    }

    @RequestMapping(value = "/getConcepts", method = RequestMethod.POST)
    public @ResponseBody
    String getConcepts(@RequestParam("feature") String feature) {
        String newTag = "";
        if (!feature.isEmpty()) {
            try {

                byte[] bytes = feature.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                JSONObject jsonObject = new JSONObject(result);
                System.out.println(result);
                this.cf = new ConceptFinder(jsonObject.get("concept").toString(),jsonObject.get("pos").toString(),"");
                result = cf.getAllResults().toString();

//                System.out.println("You successfully uploaded " + newTag + " into " + result);
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

    @RequestMapping(value = "/getConcordances", method = RequestMethod.POST)
    public @ResponseBody
    String getConcordances(@RequestParam("feature") String feature) {
        String newTag = "";
        if (!feature.isEmpty()) {
            try {

                byte[] bytes = feature.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println("pos" + result);

                concordancer = new Concordancer(tiger2Converter.getCorpus());
                concordancer.JSONtoArrayConceptLIst(result);
                result = concordancer.getConcordanceResult().toString();
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

    @RequestMapping(value = "/removeConcordance", method = RequestMethod.POST)
    public @ResponseBody
    String removeConcordance(@RequestParam("corpus") String corpus) {
        String newTag = "";
        if (!corpus.isEmpty()) {
            try {

                byte[] bytes = corpus.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println("pos" + result);

                concordancer = new Concordancer(tiger2Converter.getCorpus());
                concordancer.JSONtoArrayConceptLIst(result);
                result = concordancer.removeConcordance(result).toString();
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


    @RequestMapping(value = "/getAdvancedConcordances", method = RequestMethod.POST)
    public @ResponseBody
    String getAdvancedConcordances(@RequestParam("feature") String feature) {
        String newTag = "";
        if (!feature.isEmpty()) {
            try {

                byte[] bytes = feature.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                System.out.println(result);
                concordancer = new Concordancer(tiger2Converter.getCorpus());
                concordancer.JSONToArrayAdvancedConceptList(result);

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

    @GetMapping("/getContent")
    public String getContent() throws JSONException {

//       ConceptFinder fn = new ConceptFinder("Hello", "book");
            return fileManager.XMLtoJSONconverter().toString();
    }

    @GetMapping("/getTags")
    public String getTags() throws JSONException {
        // fileManager.tigerXMLChecker(new File("C:\\Users\\Micoh F Alvarez\\Desktop\\test.xml.tiger2"));
        // fileManager.tigerProcess(new File("C:\\Users\\Micoh F Alvarez\\Desktop\\test.xml.tiger2"));
        return annotationsManager.AnnotationstoJSONconverter().toString();
    }

    @RequestMapping(value = "/getPattern", method = RequestMethod.POST)
    public @ResponseBody
    String getPattern(@RequestParam("concordance") String concordance) throws JSONException {
        if (!concordance.isEmpty()) {
            try
            {
                byte[] bytes = concordance.getBytes();
                String result = new String(bytes);
                System.out.println(result);
                return fileManager.getPattern(result);
            } catch (Exception e) {
                System.out.println("You failed to upload " + concordance + " => " + e.getMessage());
                return "You failed to upload " + concordance + " => " + e.getMessage();
            }
        }
        return concordance;
    }

    @RequestMapping(value = "/getPatternFilteredByID", method = RequestMethod.POST)
    public @ResponseBody
    String getFilteredPatternByID(@RequestParam("sID") String sID) throws JSONException {
        if (!sID.isEmpty()) {
            try
            {
                byte[] bytes = sID.getBytes();
                String result = new String(bytes);
                return fileManager.getPattern(result);
            } catch (Exception e) {
                System.out.println("You failed to upload " + sID + " => " + e.getMessage());
                return "You failed to upload " + sID + " => " + e.getMessage();
            }
        }
        return sID;
    }

    @RequestMapping(value = "/getSuggestions", method = RequestMethod.POST)
    public @ResponseBody
    String getSuggestions(@RequestParam("jsonContent") String sJsonContent) {
        if (!sJsonContent.isEmpty()) {
            try
            {
                byte[] bytes = sJsonContent.getBytes();
                String result = new String(bytes);
                SuggestionFinder suggestionFinder = new SuggestionFinder();
                return suggestionFinder.getSuggestions(result).toString();
            } catch (Exception e) {
                System.out.println("You failed to upload " + sJsonContent + " => " + e.getMessage());
                return "You failed to upload " + sJsonContent + " => " + e.getMessage();
            }
        }
        return sJsonContent;
    }

//    @GetMapping("/getXML")
//    public String getXML() {
//        return fileManager.NLPprocessor("I happened to see a one day cricket match between Pakistan and Australia at Wankhade Stadium, Mumbai. " +
//                "I went for a fun. But I wit\u00ADnessed a horrible sight. Two thousand ticketless cricket fans gate crashed. " +
//                "There was a stampede. Three persons died and twenty were injured. Administration was responsible for it.");
//    }
}

