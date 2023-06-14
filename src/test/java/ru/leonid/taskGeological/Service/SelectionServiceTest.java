package ru.leonid.taskGeological.Service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.leonid.taskGeological.Model.Selection;
import ru.leonid.taskGeological.Repository.SelectionRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SelectionServiceTest {

    @Mock
    SelectionRepository selectionRepository;
    @InjectMocks
    SelectionService selectionService;

    @Test
    void getSelectionByCode_testCustomRequestFromDB(){
        Selection s = new Selection();
        s.setName("Selection 2");
        Mockito.doReturn(List.of(s)).when(selectionRepository).getSelectionByCodeOfGeologicClass("GC23");

        List<Selection> result = selectionService.getSelectionByCode("GC23");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getName(), "Selection 2");
    }
    @Test
    void getStatusOfTask_notExistTask() {
        String result = selectionService.getStatusOfTask(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Task id 1 not exist", result);
    }
}