import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends ListenerAdapter {

    private final int MIN_TIME_DELAY = 1000 * 60 * 10;
    private final int MAX_TIME_DELAY = 1000 * 60 * 15;

    private char prefix = '-';

    private ArrayList<String> lastNewsList = new ArrayList<>();
    public static Map<String, Boolean> mapOfGenerators = new HashMap<>();
    public  static Map<String, Thread> mapOfThreads = new HashMap<>();


    public static void main(String[] args) throws LoginException {
        String botToken = "NjM2OTg4NzAyM1337youdontgetmydiscordbottoken133730oo792d0";
        JDABuilder builder = JDABuilder.createDefault(botToken);
        builder.addEventListeners(new Main());
        builder.build();

//        NewsGenerator newsGenerator = new NewsGenerator();
//        for (int i = 0; i < 30; i++) {
//            System.out.println(newsGenerator.generateNews());
//            System.out.println();
//        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        NewsGenerator newsGenerator = new NewsGenerator();
        Thread thread = new Thread();

        System.out.println( "Message from " + event.getAuthor().getName() + " (" + new Date().toString() + ")" + ":\n"
                            + "[--- " + event.getMessage().getContentDisplay() + " ---]");
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "новость")) { //новость
            String news = newsGenerator.generateNews();
            event.getChannel().sendMessage(news).queue();
            addNewsToList(news);
            genResponse(news, newsGenerator, event);
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "анекдот")) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readValue(new File("./input/dynamic_news.json"), JsonNode.class);

                StringBuilder newsBuilder = new StringBuilder();
                String name = newsGenerator.genName().toString();
                if (newsGenerator.getFaction().equals("Зомбированные")) {
                    while (newsGenerator.getFaction().equals("Зомбированные")) {
                        name = newsGenerator.genName().toString();
                    }
                }
                newsBuilder.append(name).append(":\n");

                newsBuilder.append(newsGenerator.genJokeNews(node));
                event.getChannel().sendMessage(newsBuilder.toString()).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String response = null;
            switch (getRndIntInRange(1, 3)) {
                case 1:
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    break;
                case 2:
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    break;
                case 3:
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    break;
            }
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "помощь")) {
            showCommandList(event);
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "startNews")) {
            if (!mapOfGenerators.containsKey(event.getChannel().getName())) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            String news = newsGenerator.generateNews();
                            event.getChannel().sendMessage(news).queue();
                            addNewsToList(news);
                            genResponse(news, newsGenerator, event);
                            try {
                                Thread.sleep(getRndIntInRange(MIN_TIME_DELAY, MAX_TIME_DELAY));
                            } catch (InterruptedException e) {
                                /**
                                 * здесь вылетает NullPointerException на вызове .interrupt(), но при этом поток завершается (?),
                                 * новости перестают поступать и всё продолжает нормально работать.
                                 *
                                 * нужно попытаться исправить...
                                 * */
                                mapOfThreads.get(event.getChannel().getName()).interrupt();
                            }
                        }
                    }
                });
                thread.start();
                mapOfGenerators.put(event.getChannel().getName(), true);
                mapOfThreads.put(event.getChannel().getName(), thread);
            }
            else {
                if (mapOfGenerators.get(event.getChannel().getName())) {
                    event.getChannel().sendMessage("Error: Генератор новостей уже запущен!").queue();
                }
            }
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "stopNews")) {
            mapOfThreads.get(event.getChannel().getName()).interrupt();
            mapOfThreads.remove(event.getChannel().getName());
            mapOfGenerators.remove(event.getChannel().getName());
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "недавнее")) {
            for (String news : lastNewsList) {
                event.getChannel().sendMessage(news).queue();
            }
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "chList")) {
            getChannelsWithGensList(event);
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "stopAll")) {
            deleteGenInAllServerChannels(event);
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "botInfo")) {
            event.getChannel().sendMessage(Resources.getBotInfo()).queue();
        }
    }



    public void showCommandList(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                        "Список команд: \n" +
                                prefix + "новость : выводит новость\n" +
                                prefix + "анекдот : выводит анекдот\n" +
                                prefix + "недавнее : выводит список последних 5 новостей\n" +
                                prefix + "startNews : включить генератор новостей в данном канале\n" +
                                prefix + "stopNews : выключить генератор новостей в данном канале\n" +
                                prefix + "stopAll : выключить генератор новостей во всех каналах на сервере\n" +
                                prefix + "chList : вывести список всех каналов на сервере, на которых запущен генератор новостей\n" +
                                prefix + "botInfo : выводит информацию о боте"
        ).queue();
    }

    public static void genResponse(String news, NewsGenerator newsGenerator, MessageReceivedEvent event) {
        if (news.contains("Зомбированные")) {
            String response = newsGenerator.generateResponse(1, event);
            event.getChannel().sendMessage(response).queue();
        }
        if (newsGenerator.getNewsType() == 8) {//если тип новости - анекдот - генерируем реакцию на анекдот
            String response = null;
            switch (getRndIntInRange(1, 3)) {
                case 1:
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    break;
                case 2:
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    break;
                case 3:
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    response = newsGenerator.generateResponse(2, event);
                    event.getChannel().sendMessage(response).queue();
                    break;
            }
        }
        if (newsGenerator.getNewsType() == 7) {
            String response = newsGenerator.generateResponse(4, event);
            event.getChannel().sendMessage(response).queue();
        }
        if (newsGenerator.getNewsType() == 10) { //если тип новости - новость о врагах рядом - генерируем реакцию
            String response = newsGenerator.generateResponse(3, event);
            event.getChannel().sendMessage(response).queue();
        }
    }

    private static int getRndIntInRange(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
    }

    private void deleteGenInAllServerChannels(MessageReceivedEvent event) {
        List<String> channelsNamesList = new LinkedList<>();
        for (TextChannel ch : event.getGuild().getTextChannels()) {
            channelsNamesList.add(ch.getName());
        }
        for (String name : channelsNamesList) {
            if (mapOfThreads.containsKey(name)) {
                mapOfThreads.get(name).interrupt();
                mapOfThreads.remove(name);
            }
            mapOfGenerators.remove(name);
        }
    }

    private void addNewsToList(String news) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getCurrentTime()).append(": ").append(news);
        if (lastNewsList.size() == 5) lastNewsList.remove(0);
        lastNewsList.add(stringBuilder.toString());
    }

    private String getCurrentTime(){
        Date date = new Date();
        return date.toString();
    }

    private void getChannelsWithGensList(MessageReceivedEvent event) {
        List<String> channelsNamesList = new LinkedList<>();
        for (TextChannel ch : event.getGuild().getTextChannels()) {
            channelsNamesList.add(ch.getName());
        }

        if (mapOfGenerators.isEmpty()) {
            event.getChannel().sendMessage("Нигде ещё не запущен генератор новостей.").queue();
        }
        for (String key : mapOfGenerators.keySet()) {
            if (channelsNamesList.contains(key)) event.getChannel().sendMessage(key).queue();
        }
    }

    private void getInfoAboutChannel(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getChannel().getName()).queue();
    }


}
