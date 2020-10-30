package com.java.batch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "PEOPLE")
@AllArgsConstructor
@NoArgsConstructor
public class People {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "people_id")
    private Long peopleId;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "age")
    private int age;
    
    @Column(name = "email")
    private String email;

	public static People of(String firstName, String lastName, int age, String email) {
		People p = new People();
		p.setFirstName(firstName);
		p.setLastName(lastName);
		p.setAge(age);
		p.setEmail(email);
		return p;
	}

}
