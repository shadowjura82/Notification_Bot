package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.models.NotificationEntity;
import pro.sky.telegrambot.repositories.NotificationRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private Matcher matcher;

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationRepository notificationRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            messagesAnalyzer(update.message().chat().id(), update.message().text());
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    private void messagesAnalyzer(Long id, String message) {
        switch (message) {

            case "/start": {
                start(id);
                break;
            }

            case "/help": {
                help(id);
                break;
            }

            case "/review": {
                review(id);
                break;
            }

            case "/clear": {
                clear(id);
                break;
            }

            default: {
                addNewRecord(id, message);
                break;
            }
        }
    }

    private void start(Long id) {
        String welcome = "Добро пожаловать в тестовую версию бота \"Напоминалка\"!\n" +
                "Бот предназначен для напоминания о предстоящих событиях в заданное время\n" +
                "Введите предстоящие событие в таком формате:\n" +
                "dd.MM.yyyy hh:mm <Text message>\nНапример:\n" +
                "01.01.2022 20:00 Сделать домашнюю работу";
        SendResponse result = telegramBot.execute(new SendMessage(id, welcome));
        loggingMessage(result.isOk(), result.errorCode());
    }

    private void help(Long id) {
        String welcome = "Формат сообщения для нового напоминания:" +
                "dd.MM.yyyy hh:mm <Text message>\nНапример:\n" +
                "01.01.2022 20:00 Сделать домашнюю работу\n" +
                "/start - показать приветственное сообщение\n" +
                "/review - показать все запланированные события\n" +
                "/clear - удалить все напоминания";
        SendResponse result = telegramBot.execute(new SendMessage(id, welcome));
        loggingMessage(result.isOk(), result.errorCode());
    }

    private void review(Long id) {
        List<NotificationEntity> allNotifications = notificationRepository.findAll();
        allNotifications.forEach(e -> {
            String text = e.getTime() + " " + e.getMessage() + "\n";
            SendResponse result = telegramBot.execute(new SendMessage(id, text));
            loggingMessage(result.isOk(), result.errorCode());
        });
    }

    private void clear(Long id) {
        notificationRepository.deleteAll();
        SendResponse result = telegramBot.execute(new SendMessage(id, "Все напоминания успешно удалены"));
        loggingMessage(result.isOk(), result.errorCode());
    }

    private void addNewRecord(Long id, String message) {
        matcher = pattern.matcher(message);
        if (matcher.matches()) {
            String notificationText = matcher.group(3);
            LocalDateTime dateTime = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    .truncatedTo(ChronoUnit.MINUTES);
            notificationRepository.save(new NotificationEntity(id, notificationText, dateTime));
            logger.info("New record has been created in database:" +
                    " \nID: " + id + "\nDate and Time: " + dateTime + "\nMessage: " + notificationText + "\n");
            SendResponse result = telegramBot.execute(
                    new SendMessage(id, "Новая запись добавлена в базу"));
            loggingMessage(result.isOk(), result.errorCode());
        } else {
            SendResponse result = telegramBot.execute(
                    new SendMessage(id, "Неверный формат сообщения. Введите /help для помощи"));
            loggingMessage(result.isOk(), result.errorCode());
        }
    }

    private void loggingMessage(boolean result, int errorCode) {
        if (result) {
            logger.info("Message has been sent successfully");
        } else {
            logger.error("Message was not sent. Error code: " + errorCode);
        }
    }
}