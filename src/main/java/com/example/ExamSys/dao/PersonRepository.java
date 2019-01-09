package com.example.ExamSys.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ExamSys.domain.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
