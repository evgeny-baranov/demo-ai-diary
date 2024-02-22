package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;
import java.util.Scanner;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    @Autowired
    private RecordService recordService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {

        for (int i = 0; i < 10; i++) {
            Record r = new Record();
            r.setText("test text here " + i);
            recordService.saveRecord(r);
        }

        recordService.getRecords().forEach(System.out::println);

    }
}
