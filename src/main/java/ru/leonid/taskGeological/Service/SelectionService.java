package ru.leonid.taskGeological.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.leonid.taskGeological.Model.GeologicClass;
import ru.leonid.taskGeological.Model.Selection;
import ru.leonid.taskGeological.Model.SelectionRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class SelectionService {
    @Autowired
    SelectionRepository selectionRepository;
    private static final Logger log = LoggerFactory.getLogger(SelectionService.class);

    //Храним информацию о статусах CompletableFeature-задач(экспорт/импорт)
    private Map<Integer, TaskStatus> numOfTask = new ConcurrentHashMap<>();

    private Map<Integer,XSSFWorkbook> workbookToImport = new ConcurrentHashMap<>();
    public List<Selection> getSelectionByCode(String code){
        log.info("getByCode with code = " + code);
        List<Selection> result1 = selectionRepository.getSelectionByCodeOfGeologicClass(code);
        if (result1.isEmpty())
            return Collections.emptyList();
        return result1;
    }

    //Выполнить импорт XlS в БД
    @Async
    public CompletableFuture<String> importToDB(InputStream inputStream, int num){
        log.info("Выполняем в потоке: " + Thread.currentThread().getName() + " num = " + num);
        numOfTask.put(num, TaskStatus.INPROGRESS);
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        }catch (IOException exception){
            numOfTask.put(num, TaskStatus.ERROR);
            log.error(exception.getMessage());
            return CompletableFuture.failedFuture(exception);
        }
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
        iterator.next();

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            Selection selection = new Selection();
            List<GeologicClass> geologicClassList = new ArrayList<>();
            Cell currentCell = cellIterator.next();
            selection.setName(currentCell.getStringCellValue());//первый столбец - selection name
            while (cellIterator.hasNext()) {
                currentCell = cellIterator.next();
                GeologicClass geologicClass = new GeologicClass();
                geologicClass.setSelection(selection);
                geologicClass.setName(currentCell.getStringCellValue());
                currentCell = cellIterator.next();
                geologicClass.setCode(currentCell.getStringCellValue());
                geologicClassList.add(geologicClass);
            }
            selection.setGeological(geologicClassList);
            selectionRepository.save(selection);
        }
        numOfTask.put(num, TaskStatus.DONE);
        log.info(Thread.currentThread().getName() + ": Данные из файла сохранены в БД");
        return CompletableFuture.completedFuture("Complete "+ Thread.currentThread().getName());
    }

    //Проверить статус выполнения задачи(экспорт/импорт)
    public String getStatusOfTask(Integer id){
        if(numOfTask.containsKey(id))
            return numOfTask.get(id).getTitle();
        else
            return "Task id " + id + " not exist";
    }
    //формирование xlsx для выгрузки
    @Async
    @Transactional //из за Lazy initialization коллекции в сущности Selection
    public CompletableFuture<Integer> exportFromDB(int num){
        numOfTask.put(num, TaskStatus.INPROGRESS);
        log.info("Начинаем экспорт из БД в xls task= "+ num);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("dataFromBD");

        XSSFRow headersRow = sheet.createRow(0);
        XSSFCell headCell = headersRow.createCell(0);
        headCell.setCellValue("Selection name");
        headCell = headersRow.createCell(1);
        headCell.setCellValue("Class " + 1 + " name");
        //Заполнить файл данными из БД
        int indrow = 1;
        int indcell = 0;
        XSSFRow currentRow;
        XSSFCell currentCell;
        Iterable<Selection> selections = selectionRepository.findAll();
        for(Selection selection: selections){
            currentRow = sheet.createRow(indrow);
            currentCell = currentRow.createCell(indcell);
            currentCell.setCellValue(selection.getName());
            int indexGeoClass = 1;
            for(GeologicClass geologicClass : selection.getGeological()){
                headCell=headersRow.createCell(indcell+1);
                headCell.setCellValue("Class " + indexGeoClass + " name");
                headCell=headersRow.createCell(indcell+2);
                headCell.setCellValue("Class " + indexGeoClass + " code");
                currentCell = currentRow.createCell(++indcell);
                currentCell.setCellValue(geologicClass.getName());
                currentCell = currentRow.createCell(++indcell);
                currentCell.setCellValue(geologicClass.getCode());
                indexGeoClass++;
            }
            indcell = 0;
            indrow++;
        }
        log.info("Данные из БД обработаны. Создаем новый файл на экспорт / num = "+ num);
        try {
            FileOutputStream outFile = new FileOutputStream("/tmp/export" + num + ".xls");
            workbook.write(outFile);
        }catch (IOException exception){
            numOfTask.put(num, TaskStatus.ERROR);
            log.error("Не удалось создать файл / num = "+ num);
        }
        numOfTask.put(num, TaskStatus.DONE);
        workbookToImport.put(num, workbook);
        log.info("Экспорт из БД в xls выполнен task= "+ num);

        return CompletableFuture.completedFuture(1);
    }

}
