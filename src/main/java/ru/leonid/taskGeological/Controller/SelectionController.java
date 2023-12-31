package ru.leonid.taskGeological.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.leonid.taskGeological.Model.Selection;
import ru.leonid.taskGeological.Service.SelectionService;
import ru.leonid.taskGeological.Service.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SelectionController {
    private AtomicInteger num = new AtomicInteger(1);
    @Autowired
    SelectionService selectionService;

    @GetMapping("/sections/by-code")
    ResponseEntity<List<Selection>> getSelectionsByCode(@RequestParam("code") String code){
        return new ResponseEntity<>(selectionService.getSelectionByCode(code), HttpStatus.OK);
    }

    @PostMapping("/import")
    synchronized ResponseEntity importFile( @RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file. Less than 30 Mb.", HttpStatus.OK);
        }
        selectionService.importToDB(file.getInputStream(), num.get());
        return new ResponseEntity<>(num.getAndIncrement(), HttpStatus.OK);
    }

    @GetMapping("/import/{id}")
    ResponseEntity<String> statusImport(@PathVariable("id") Integer id){
        return new ResponseEntity<>(selectionService.getStatusOfTask(id), HttpStatus.OK);
    }

    @GetMapping("/export")
    synchronized ResponseEntity exportFromDB(){
        selectionService.exportFromDB(num.get());
        return new ResponseEntity<>(num.getAndIncrement(), HttpStatus.OK);
    }

    @GetMapping("/export/{id}")
    ResponseEntity<String> statusOfExport(@PathVariable("id") Integer id){
        return new ResponseEntity<>(selectionService.getStatusOfTask(id), HttpStatus.OK);
    }

    @GetMapping("/export/{id}/file")
    ResponseEntity returnFile(@PathVariable Integer id) throws IOException {
        if(!(selectionService.getStatusOfTask(id)== TaskStatus.DONE.getTitle())){
            return new ResponseEntity<>("File export" + id + ".xls not found", HttpStatus.NOT_FOUND);
        }
        File file = new File("/tmp/export" + id + ".xls");
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export" + id + ".xls");
        return new ResponseEntity<>(new ByteArrayResource(resource.getByteArray()), headers, HttpStatus.CREATED);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity handlerException(IOException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
    }
}