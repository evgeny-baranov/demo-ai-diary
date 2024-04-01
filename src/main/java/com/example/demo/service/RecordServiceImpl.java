package com.example.demo.service;

import com.example.demo.RecordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordRepository recordRepository;

    public Record saveRecord(Record record) {
        return recordRepository.save(record);
    }

    @Override
    public Page<Record> getPagedRecords(Pageable pageable) {
        return recordRepository.findAll(pageable);
    }

//    @PostConstruct
//    public void init() {
//        for (int i = 0; i < 10; i++) {
//            Record r = new Record();
//            r.setText("test text here " + i);
//            saveRecord(r);
//        }
//    }
}




















































