package com.example.ExamSys.dao;

import java.util.List;
import com.example.ExamSys.domain.QuestionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionListRepository extends JpaRepository<QuestionList, Long> {
    @Query("select q from QuestionList q where q.name = ?1")
    List<QuestionList> findByName(String name);

    @Query("select q from QuestionList q where q.name = ?1 and q.number = ?2")
    QuestionList findByNameandNumber(String name, int number);

    @Modifying
    @Query("delete from QuestionList q where q.id=?1")
    void deleteById(Long id);
}
