package com.java.batch.listner;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

import com.java.batch.dto.PeopleDTO;

public class WriteStepListner implements ItemWriteListener<PeopleDTO>{

	@Override
	public void beforeWrite(List<? extends PeopleDTO> items) {
		System.out.println("count---before write------ " + items.size());
	}

	@Override
	public void afterWrite(List<? extends PeopleDTO> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWriteError(Exception exception, List<? extends PeopleDTO> items) {
		// TODO Auto-generated method stub
		System.out.println("ërror while writing--- " + items.size());
		
	}

}
