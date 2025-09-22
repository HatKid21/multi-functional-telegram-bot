package com.github.hatkid.sql;

import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(DriverManager.class.getName());

    private static final String DB_URL = "jdbc:sqlite:reminders/reminders.db";

    public static Connection connect() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public static long insertReminder(Reminder reminder){
        long chatId = reminder.getChatId();
        String message = reminder.getMessageText();
        long scheduledTime = reminder.getScheduledTime();
        return insertReminder(chatId,message,scheduledTime);
    }

    public static synchronized long insertReminder(long chatId, String messageText, long scheduledTime) {
        String sql = "INSERT INTO reminders(chat_id,message_text,scheduled_time) VALUES(?,?,?)";

        long generatedId = -1;

        try (Connection connection = connect(); PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, chatId);
            preparedStatement.setString(2, messageText);
            preparedStatement.setLong(3, scheduledTime);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0){
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                    if (generatedKeys.next()){
                        generatedId = generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting reminder : " + e.getMessage());
        }
        return generatedId;
    }

//    public static List<Reminder> getRemindersByChatId(TelegramClient telegramClient, long chatId){
//        String sql = "SELECT id, chat_id, message_text, scheduled_time" +
//                " FROM reminders" +
//                " WHERE chat_id = ?" +
//                " ORDER BY scheduled_time DESC";
//        List<Reminder> reminderList = new ArrayList<>();
//        try (Connection connection = connect();
//        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
//            preparedStatement.setLong(1,chatId);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while(resultSet.next()){
//                int id = resultSet.getInt("id");
//                String message = resultSet.getString("message_text");
//                long scheduledTime = resultSet.getLong("scheduled_time");
//                Reminder reminder = new Reminder(telegramClient,chatId,message,scheduledTime);
//                reminder.setDatabaseId(id);
//                reminderList.add(reminder);
//            }
//
//        } catch (SQLException e){
//            LOGGER.log(Level.SEVERE, "SQL exception",e);
//        }
//        return reminderList;
//    }

    public static void activateReminders(TelegramClient telegramClient){
        String sql = "SELECT id, chat_id, message_text, scheduled_time FROM reminders";
        int iterations = 0;
        try(Connection connection = connect()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                long chatId = resultSet.getInt("chat_id");
                String messageText = resultSet.getString("message_text");
                long scheduledTime = resultSet.getLong("scheduled_time");
                Reminder reminder = new Reminder(telegramClient,chatId,messageText,scheduledTime);
                reminder.setDatabaseId(id);
                reminder.activate();
                iterations++;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        if (iterations > 0){
            LOGGER.log(Level.INFO,"Saved reminders data has successfully activated (" + iterations + ")");
        } else{
            LOGGER.log(Level.INFO, "Database is empty");
        }

    }



    public static synchronized void deleteReminder(long id){
        String sql = "DELETE FROM reminders WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();


            LOGGER.log(Level.INFO, "Reminder with id = " + id + " has deleted");
        } catch (SQLException e){
            System.err.println("SQL Error deleting reminder with ID " + id + ": " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Connection Error during deletion of reminder with ID " + id + ": " + e.getMessage());
        }
    }



    public static synchronized void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS reminders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "chat_id INTEGER NOT NULL," +
                "message_text TEXT NOT NULL," +
                "scheduled_time INTEGER NOT NULL);";

        File directory = new File("reminders");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (Connection connection = connect()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error with connection : " + e.getMessage());
        }
    }

}
