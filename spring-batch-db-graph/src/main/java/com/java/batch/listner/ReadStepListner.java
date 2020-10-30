package com.java.batch.listner;

import org.springframework.batch.core.ItemReadListener;

import com.java.batch.model.People;

public class ReadStepListner implements ItemReadListener<People>{

	@Override
	public void beforeRead() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterRead(People item) {

		//System.out.println("---after read---");
	}

	@Override
	public void onReadError(Exception ex) {
		// TODO Auto-generated method stub
		
	}

}
