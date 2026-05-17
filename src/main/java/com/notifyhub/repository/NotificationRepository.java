package com.notifyhub.repository;

import com.notifyhub.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {

    // Find by status
    List<NotificationLog> findByStatus(String status);

    // Find by channel
    List<NotificationLog> findByChannel(String channel);

    // Stats query - count by status
    @Query("SELECT n.status, COUNT(n) FROM NotificationLog n GROUP BY n.status")
    List<Object[]> countByStatus();

    // Stats query - count by channel
    @Query("SELECT n.channel, COUNT(n) FROM NotificationLog n GROUP BY n.channel")
    List<Object[]> countByChannel();
}
