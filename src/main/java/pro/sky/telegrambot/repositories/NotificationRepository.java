package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.models.NotificationEntity;

import java.util.ArrayList;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {
}
