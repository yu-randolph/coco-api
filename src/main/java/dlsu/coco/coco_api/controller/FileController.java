package dlsu.coco.coco_api.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import dlsu.coco.coco_api.model.UploadedFile;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.util.FileCopyUtils;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/cont")
public class FileController {

    UploadedFile ufile;

    public FileController()
    {
        System.out.println("init RestController");
        ufile = new UploadedFile();
    }

    @RequestMapping(value = "/get/{value}", method = RequestMethod.GET)
    public void get(HttpServletResponse response, @PathVariable String value)
    {
        try {

            response.setContentType(ufile.type);
            response.setContentLength(ufile.length);
            FileCopyUtils.copy(ufile.bytes, response.getOutputStream());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody void upload(MultipartHttpServletRequest request, HttpServletResponse response)
    {
        Iterator<String> itr =  request.getFileNames();

        MultipartFile mpf = request.getFile(itr.next());
        System.out.println(mpf.getOriginalFilename() +" uploaded!");

        try {
            //just temporary save file info into ufile
            ufile.length = mpf.getBytes().length;
            ufile.bytes= mpf.getBytes();
            ufile.type = mpf.getContentType();
            ufile.name = mpf.getOriginalFilename();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(mpf.getContentType().equals(".xml"))
        {
            System.out.println("XML!");
        }
        else if(mpf.getContentType().equals(".txt"))
        {
            System.out.println("TXT!");
        }

    }
}

