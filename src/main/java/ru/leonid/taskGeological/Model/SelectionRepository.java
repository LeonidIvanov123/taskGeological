package ru.leonid.taskGeological.Model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectionRepository extends CrudRepository<Selection, Long> {

    @Query(nativeQuery = true,
            value = """
                    SELECT s.id,s.name FROM selection s 
                    LEFT JOIN geologic_class g ON g.selection_id=s.id
                    WHERE g.code = ?1
                    """)
    List<Selection> getSelectionByCodeOfGeologicClass(@Param("code") String code);

 //   @Query(value = "SELECT s from Selection s")
  //  List<Selection> getTestQuery();
}
