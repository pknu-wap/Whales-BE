package com.whales.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "chat_rooms", uniqueConstraints = {@UniqueConstraint(columnNames = {"tag_name"})})
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "tag_name", nullable = false, length = 100)
    private String tagName;

    public ChatRoom(String tagName) {
        this.tagName = tagName;
    }
}
