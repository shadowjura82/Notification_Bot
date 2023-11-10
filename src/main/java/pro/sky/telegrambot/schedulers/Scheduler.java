package pro.sky.telegrambot.schedulers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.models.NotificationEntity;
import pro.sky.telegrambot.repositories.NotificationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Scheduler {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private TelegramBot telegramBot;
    private Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Scheduled(cron = "0 0/1 * * * *")
    public void timeMonitor() {
        notificationRepository.findByTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(e -> {
                    SendResponse result = telegramBot.execute(new SendMessage(e.getChatId(), e.getMessage()));
                    loggingMessage(result.isOk(), result.errorCode());
                });
    }

    private void loggingMessage(boolean result, int errorCode) {
        if (result) {
            logger.info("Message has been sent successfully");
        } else {
            logger.error("Message was not sent. Error code: " + errorCode);
        }
    }
}
