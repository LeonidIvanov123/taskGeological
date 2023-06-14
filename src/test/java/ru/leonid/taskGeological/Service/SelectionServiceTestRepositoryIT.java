package ru.leonid.taskGeological.Service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.leonid.taskGeological.Model.Selection;
import ru.leonid.taskGeological.Repository.SelectionRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class SelectionServiceTestRepositoryIT {

    @Autowired
    SelectionRepository selectionRepository;
    @Autowired
    SelectionService selectionService;

    //тестируем custom запрос к БД
    @Test
    void importToDBTest_ImportToDbAndCheck() throws IOException, ExecutionException, InterruptedException {
        Resource resource = new ClassPathResource("test_document.xls");
        File file = resource.getFile();
        System.out.println(file.exists());

        CompletableFuture<String> completableFuture = selectionService.importToDB(new FileInputStream(file), 1);
        completableFuture.get(); //Ждем завершения записи в тестовую БД

        Assertions.assertEquals(selectionService.getStatusOfTask(1), "DONE");
        List<Selection> selectionList = selectionService.getSelectionByCode("GC21");

        Assertions.assertNotNull(selectionList);
        Assertions.assertEquals(selectionList.size(),1);
        Assertions.assertEquals(selectionList.get(0).getName(), "Select 2");

    }
}