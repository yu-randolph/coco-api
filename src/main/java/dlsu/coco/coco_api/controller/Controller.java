package dlsu.coco.coco_api.controller;

import dlsu.coco.coco_api.model.ConceptFinder;
import dlsu.coco.coco_api.model.Concordancer;
import dlsu.coco.coco_api.model.FileManager;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@RestController
public class Controller {

    FileManager fileManager;

    public Controller() {
        fileManager = new FileManager();
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
        if (!file.isEmpty()) {
            try {
                name = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                receivedFile = new File("src/" + name);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(receivedFile));
                stream.write(bytes);
                stream.close();
                String result = new String(bytes);
                fileManager.setPath(name);
                System.out.println(receivedFile.getPath());

                System.out.println("You successfully uploaded " + name + " into " + name);
                System.out.println("Content Type: " + file.getContentType());
                System.out.println("Result: " + result);
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";

            } catch (Exception e) {
                System.out.println("You failed to upload " + name + " => " + e.getMessage());
                return "You failed to upload " + name + " => " + e.getMessage();
            } finally {
                fileManager.tigerXMLChecker(receivedFile);
                fileManager.tigerProcess(receivedFile);
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
                File receivedFile = null;
                try {
                    fileName = files[i].getOriginalFilename();
                    byte[] bytes = files[i].getBytes();
                    receivedFile = new File("src/" + fileName);
                    BufferedOutputStream buffStream =
                            new BufferedOutputStream(new FileOutputStream(receivedFile));
                    buffStream.write(bytes);
                    buffStream.close();
                    String result = new String(bytes);
                    corpus += result + "\n";

                } catch (Exception e) {
                    return "You failed to upload " + fileName + ": " + e.getMessage() + "<br/>";
                } finally {
                    fileManager.tigerXMLChecker(receivedFile);
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

                fileManager.editAnnotation(result);

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

                fileManager.addAnnotation(result);

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

                fileManager.addFeatureValue(result);

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
    String addFeature(@RequestParam("tags") String tags) {
        String newTag = "";
        if (!tags.isEmpty()) {
            try {

                byte[] bytes = tags.getBytes();
                String result = new String(bytes);
                newTag = result;
                System.out.println(result);

                fileManager.addFeature(result);

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
        return fileManager.AnnotationstoJSONconverter().toString();
    }

    @GetMapping("/getXML")
    public String getXML() {
        return fileManager.NLPprocessor("I happened to see a one day cricket match between Pakistan and Australia at Wankhade Stadium, Mumbai. " +
                "I went for a fun. But I wit\u00ADnessed a horrible sight. Two thousand ticketless cricket fans gate crashed. " +
                "There was a stampede. Three persons died and twenty were injured. Administration was responsible for it.");
    }
}

