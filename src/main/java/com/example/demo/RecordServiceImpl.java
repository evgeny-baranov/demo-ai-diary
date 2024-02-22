package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl implements RecordService{

    @Autowired
    private RecordRepository  recordRepository;

    public void saveRecord(Record record) {
        recordRepository.save(record);
    }

    public Iterable<Record> getRecords() {
        return recordRepository.findAll();
    }
}




















































