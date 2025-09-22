package com.github.hatkid.state;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.UserManager;
import com.github.hatkid.sql.DatabaseManager;
import com.github.hatkid.sql.Reminder;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

public class RemoveReminderHandler extends BaseStateHandler implements StateHandler{


    public RemoveReminderHandler(UserManager userManager) {
        super(userManager);
    }

    @Override
    public void handle(Message message, long chatId, TelegramBot bot) {
        List<Reminder> reminderList = Reminder.getReminders(chatId);
        if (!isNumeric(message.getText())){
            bot.sendMessage(chatId,"Некорректный id");
            return;
        }

        long id = Long.parseLong(message.getText());

        for (Reminder reminder : reminderList){
            long remId = reminder.getDatabaseId();
            if (remId == id){
                DatabaseManager.deleteReminder(id);
                reminder.deleteScheduler();
                bot.sendMessage(chatId,"Напоминание с id " + id + " было успешно удалено!");
                userManager.getUser(chatId).setState(UserState.IDLE);
                return;
            }
        }

    }

    private boolean isNumeric(String str){
        if (str == null){
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

}
