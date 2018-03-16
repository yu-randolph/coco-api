package dlsu.coco.coco_api.controller;

import dlsu.coco.coco_api.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;

@RestController
public class FileController {

    UploadedFile file;

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
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                String result = new String(bytes);
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


    @GetMapping("/get/{value}")
    public void get(HttpServletResponse response, @PathVariable String value)
    {
        try {

            response.setContentType(file.type);
            response.setContentLength(file.length);
            FileCopyUtils.copy(file.bytes, response.getOutputStream());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
