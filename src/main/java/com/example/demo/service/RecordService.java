package com.example.demo.service;

import com.example.demo.domain.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecordService {
    Record saveRecord(Record record);

    Page<Record> getPagedRecords(Pageable pageable);
}
