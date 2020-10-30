package com.java.batch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.java.batch.model.People;


@Component
public class PeopleDBRowMapper implements RowMapper<People> {

    @Override
    public People mapRow(ResultSet resultSet, int i) throws SQLException {
        People employee = new People();
        employee.setPeopleId(resultSet.getLong("people_id"));
        employee.setFirstName(resultSet.getString("first_name"));
        employee.setLastName(resultSet.getString("last_name"));
        employee.setEmail(resultSet.getString("email"));
        employee.setAge(resultSet.getInt("age"));
        return employee;
    }
}