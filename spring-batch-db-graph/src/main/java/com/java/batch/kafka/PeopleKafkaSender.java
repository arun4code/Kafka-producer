package com.java.batch.kafka;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.java.batch.dto.PeopleDTO;

@Component
public class PeopleKafkaSender implements ItemWriter<PeopleDTO> {

    @Autowired
    private Sender sender;

    @Override
    public void write(List<? extends PeopleDTO> peoples) throws Exception {
    	sender.send(peoples);
        System.out.println("Message sent to kafka");

    }
}
