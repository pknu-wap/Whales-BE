package com.whales.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReceiver_IdOrderByCreatedAtDesc(UUID receiverId);
    long countByReceiver_IdAndReadFalse(UUID receiverId);
    List<Notification> findByReceiver_IdAndReadFalseOrderByCreatedAtDesc(UUID receiverId);
    @Modifying
    @Query("""
    UPDATE Notification n
    SET n.read = true
    WHERE n.receiver.id = :userId AND n.read = false
""")
    int markUnreadAsRead(UUID userId);
}
