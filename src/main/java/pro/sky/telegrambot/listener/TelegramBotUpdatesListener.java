package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository repository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }



    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String message = update.message().text();
            Long chatId = update.message().chat().id();
            if (message.equals("/start")) {
                sendMessage(chatId, message);
            } else {
                Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                Matcher matcher = pattern.matcher(message);
                if (matcher.matches()) {
                    String date = matcher.group(1);
                    message = message.replace(date, "");
                    message = message.trim();
                    LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    createNotificationTask(chatId, message, localDateTime);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void createNotificationTask(Long chat_id, String message, LocalDateTime dateTime) {
        repository.save(new NotificationTask(chat_id, message, dateTime));
    }

    @Scheduled(cron = "0 0/1 * * * *")
    private void checkMessage() {
        List<NotificationTask> messageToSend = repository.findNotificationTaskByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        if (!messageToSend.isEmpty()) {
            for (NotificationTask task : messageToSend) {
                sendMessage(task.getChat_id(), task.getMessage());
            }
        }
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage greetingsMessage = new SendMessage(chatId, message);
        SendResponse response = telegramBot.execute(greetingsMessage);
        response.isOk();
    }
}
