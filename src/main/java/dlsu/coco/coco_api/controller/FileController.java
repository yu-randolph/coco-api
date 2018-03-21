package dlsu.coco.coco_api.controller;

import dlsu.coco.coco_api.model.FileManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@RestController
public class FileController {

    FileManager fileManager;

    public FileController()
    {
        fileManager= new FileManager();
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

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
            @RequestParam("file") MultipartFile file){
        String name = "test11";
        if (!file.isEmpty()) {
            try {
                name = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                File receivedFile = new File(name + "-uploaded");
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(receivedFile));
                stream.write(bytes);
                stream.close();
                String result = new String(bytes);

                fileManager.setPath(name);

                System.out.println("You successfully uploaded " + name + " into " + name + "-uploaded !");
                System.out.println("Content Type: " + file.getContentType());
                System.out.println("Result: " + result);
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                System.out.println("You failed to upload " + name + " => " + e.getMessage());
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            System.out.println("You failed to upload " + name + " because the file was empty.");
            return "You failed to upload " + name + " because the file was empty.";
        }
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
    public String getContent()
    {
        //pass here the file/s to be processed

        fileManager.tigerXMLChecker(new File("C:\\\\Users\\\\Micoh F Alvarez\\\\Desktop\\\\test.xml.tiger2"));
        fileManager.tigerProcess();
        return fileManager.XMLtoJSONconverter().toString();
    }
}

