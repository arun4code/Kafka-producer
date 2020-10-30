package com.java.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.java.batch.dto.PeopleDTO;
import com.java.batch.model.People;

@Component
public class PeopleProcessor implements ItemProcessor<People, PeopleDTO> {

    @Override
    public PeopleDTO process(People employee) throws Exception {
        PeopleDTO employeeDTO = new PeopleDTO();
        employeeDTO.setPeopleId(employee.getPeopleId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setAge(employee.getAge());
        employeeDTO.setId(String.valueOf(employee.getPeopleId()));
        return employeeDTO;
    }
}