package com.example.demo;

import com.example.demo.service.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID> {
}
