package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@RestController
public class FileController {

    //FOLDER LOCATION
    private static String UPLOADED_FOLDER = "C:\\Users\\asus\\Desktop\\asd";

    //MERGE FILE LOCATION
    private static String MERGED_FILE = null;

    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    //UPDATE FOLDER LOCATION
    @PostMapping
    public ResponseEntity<Object> update_folder(@RequestParam("String") String location) {
        logger.debug("folder location upload");

        Path file = new File(location).toPath();

        //Check if file is empty
        if (!Files.isDirectory(file)) {
            return new ResponseEntity<Object>("Please Select location", HttpStatus.OK);
        }

        //Update location
        UPLOADED_FOLDER = location;

        return new ResponseEntity("Successfully updated - ", new HttpHeaders(), HttpStatus.OK);
    }

    //UPLOAD SINGLE FILE
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.debug("Single file upload");

        //Check if file is empty
        if (file.isEmpty()) {
            return new ResponseEntity<Object>("Please Select File", HttpStatus.OK);
        }

        //Check if location is null
        if (UPLOADED_FOLDER.equals(null)) {
            return new ResponseEntity<Object>("Please Set Location", HttpStatus.OK);
        }

        //Save file
        try {
            fileReceiver(Arrays.asList(file));

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity("Successfully uploaded - " + file.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);
    }

    //UPLOAD MULTIPLE FILES
    @PostMapping("/upload/multi")
    public ResponseEntity<?> uploadFileMulti(@RequestParam("files") MultipartFile[] files) {

        logger.debug("Multiple file upload!");

        // Get file name
        String uploadedFileName = Arrays.stream(files).map(x -> x.getOriginalFilename()).filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

        //Check if file is empty
        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity("Please Select File", HttpStatus.OK);
        }

        //Check if location is null
        if (UPLOADED_FOLDER.equals(null)) {
            return new ResponseEntity<Object>("Please Set Location", HttpStatus.OK);
        }

        //Save file
        try {
            fileReceiver(Arrays.asList(files));

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity("Successfully uploaded - " + uploadedFileName, HttpStatus.OK);

    }

    //NLP Process
    private Annotation NLPprocessor(String content)
    {
        Properties props;
        StanfordCoreNLP pipeline;

        Annotation document;

        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);

        document = new Annotation(content);
        pipeline.annotate(document);

        return document;
    }

    //Save file
    private void fileReceiver(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files)
        {
            if (file.isEmpty())
            {
                continue;
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
        }

    }
}

