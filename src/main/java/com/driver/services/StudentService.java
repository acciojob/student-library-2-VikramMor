package com.driver.services;

import com.driver.repositories.StudentRepository;
import com.driver.models.Card;
import com.driver.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    CardService cardService;

    @Autowired
    StudentRepository studentRepository;

    public Student getDetailsByEmail(String email){
        return studentRepository.findByEmailId(email);
    }

    public Student getDetailsById(int id){
        return studentRepository.findById(id).get();
    }

    public void createStudent(Student student){
        Card Card = cardService.createAndReturn(student);
    }

    public void updateStudent(Student student){
        studentRepository.updateStudentDetails(student);
    }

    public void deleteStudent(int id){
        cardService.deactivateCard(id);
        studentRepository.deleteCustom(id);
    }
}