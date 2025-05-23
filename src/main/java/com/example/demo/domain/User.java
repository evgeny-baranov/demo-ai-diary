package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`user`")
public class User {
    @GeneratedValue
    @Id
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    private TelegramUser telegramUser;
}
