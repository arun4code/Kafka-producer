package com.java.batch.dto;

import lombok.Data;

@Data
public class PeopleDTO {

	private String id;
    private Long peopleId;
    private String firstName;
    private String lastName;
    private String email;
    private int age;

}
