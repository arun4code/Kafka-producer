package com.java.batch.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.java.batch.model.People;

@Repository
public interface PeopleRepo extends PagingAndSortingRepository<People, Long> {

}
