package com.java.batch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.batch.model.People;
import com.java.batch.repo.PeopleRepo;

@Service
public class PeopleServiceImpl implements PeopleService {
	@Autowired
	PeopleRepo peopleRepo;
	
	@Override
	public void saveTestData() {
		int count = (int) peopleRepo.count();
		List<People> appList = new ArrayList<>();
		int total = count + 1000;
		for (int i = count + 1; i < total; i++) {
			appList.add(People.of("firstname" + i, "lastname" + i, i, "email@" + i));
		}
		peopleRepo.saveAll(appList);
	}
	
}
