package com.java.batch.reader;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.java.batch.model.People;
import com.java.batch.repo.PeopleRepo;

@Component
public class PeopleItemStreamReader {

	@Autowired
	private PeopleRepo peopleRepo;

	public ItemReader<People> employeeDBReaderJPA() {
		RepositoryItemReader<People> reader = new RepositoryItemReader<>();
		reader.setRepository(this.peopleRepo);
		reader.setMethodName("findAll");
		reader.setPageSize(10000);
		Map<String, Sort.Direction> sorts = new HashMap<String, Sort.Direction>();
		sorts.put("peopleId", Direction.ASC);
		reader.setSort(sorts);
		return reader;
	}

}
