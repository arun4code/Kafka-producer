package com.java.batch.kafka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.java.batch.dto.PeopleDTO;

@Service
public class Sender {

    @Autowired
    private KafkaTemplate<String, List<? extends PeopleDTO>> kafkaTemplate;

    public void send(List<? extends PeopleDTO> peoples) {
        kafkaTemplate.send("pdata", peoples);
    }
}