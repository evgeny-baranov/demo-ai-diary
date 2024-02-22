package com.example.demo;

public interface RecordService {
    public void saveRecord(Record record);

    public Iterable<Record> getRecords();

}
