package ru.leonid.taskGeological.Controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import ru.leonid.taskGeological.Model.Selection;
import ru.leonid.taskGeological.Service.SelectionService;

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SelectionControllerTest {
    @Mock
    SelectionService selectionService;
    @InjectMocks
    SelectionController selectionController;

    @Test
    void getSelectionsByCodeTest_returnSelectionFromDB(){
        Selection selection = new Selection();
        selection.setName("Selection 123");
        Mockito.doReturn(List.of(selection)).when(selectionService).getSelectionByCode("1");

        ResponseEntity<List<Selection>> responseEntity = this.selectionController.getSelectionsByCode("1");

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(List.of(selection), responseEntity.getBody());
    }

    @Test
    void importFileTest_submittedFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.xls",
                "text/plain", "Spring Framework".getBytes());

        ResponseEntity responseEntity = this.selectionController.importFile(multipartFile);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(1, responseEntity.getBody());
    }

    @Test
    void statusImportTest_returnStatus() {
        Mockito.doReturn("IN PROGRESS").when(selectionService).getStatusOfTask(1);

        ResponseEntity<String> responseEntity = this.selectionController.statusImport(1);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals("IN PROGRESS", responseEntity.getBody());
    }

    @Test
    void statusOfExport_returnStatusOfExport() {
        Mockito.doReturn("DONE").when(selectionService).getStatusOfTask(1);

        ResponseEntity<String> responseEntity = this.selectionController.statusImport(1);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals("DONE", responseEntity.getBody());
    }


    @Test
    void exportFromDB_returnIdOfTask() {
        ResponseEntity responseEntity = this.selectionController.exportFromDB();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(1, responseEntity.getBody());
    }
}