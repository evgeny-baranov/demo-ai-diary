package com.example.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecordService {
    public Record saveRecord(Record prompt);

    public Page<Record> getPagedRecords(Pageable pageable);
}
