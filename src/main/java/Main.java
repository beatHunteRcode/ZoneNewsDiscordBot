import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.*;

public class Main extends ListenerAdapter {

    private final int MIN_TIME_DELAY = 1000 * 60 * 10;
    private final int MAX_TIME_DELAY = 1000 * 60 * 15;

    private char prefix = '-';
    private NewsGenerator newsGenerator = new NewsGenerator();
    private ArrayList<String> lastNewsList = new ArrayList<>();
    public static Map<String, Boolean> mapOfGenerators = new HashMap<>();
    public  static Map<String, Thread> mapOfThreads = new HashMap<>();


    public static void main(String[] args) throws LoginException {
        String botToken = "NjM2OTg4N1337youdontgetmydiscordbottoken1337OMSJJckVIqk";
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(botToken);
        builder.addEventListeners(new Main());
        builder.build();

//        NewsGenerator newsGenerator = new NewsGenerator();
//        for (int i = 0; i < 50; i++) {
//            System.out.println(newsGenerator.generateNews());
//            System.out.println();
//        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Thread thread = new Thread();

        System.out.println( "Message from " + event.getAuthor().getName() + " (" + new Date().toString() + ")" + ":\n"
                            + "[--- " + event.getMessage().getContentDisplay() + " ---]");
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "новость")) { //новость
            String news = genNews(newsGenerator);
            event.getChannel().sendMessage(news).queue();
            addNewsToList(news);

            if (news.contains("Зомбированные")) {
                String response = genResponse(newsGenerator, 1, event);
                event.getChannel().sendMessage(response).queue();
                addNewsToList(news);
            }
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "помощь")) { //новость
            showCommandList(event);
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "startNews")) {
            if (!mapOfGenerators.containsKey(event.getChannel().getName())) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            String news = genNews(newsGenerator);
                            event.getChannel().sendMessage(news).queue();
                            addNewsToList(news);
                            if (news.contains("Зомбированные")) {
                                String response = genResponse(newsGenerator, 1, event);
                                event.getChannel().sendMessage(response).queue();
                                addNewsToList(response);
                            }
                            try {
                                Thread.sleep(getRandomIntegerBetweenRange(MIN_TIME_DELAY, MAX_TIME_DELAY));
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
    }



    public void showCommandList(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                        "Список команд: \n" +
                                prefix + "новость : выводит новость\n" +
                                prefix + "недавнее : выводит список последних 5 новостей\n" +
                                prefix + "startNews : включить появление новостей в случайный момент времени\n" +
                                prefix + "stopNews : выключить появление новостей в случайный момент времени\n" +
                                prefix + "stopAll : выключить появление новостей в случайный момент времени во всех каналах на сервере\n" +
                                prefix + "chList : вывести список всех каналов на сервере, на которых запущено появление новостей в случайный момент времени\n"
        ).queue();
    }

    public static String genNews(NewsGenerator newsGenerator) {
        return newsGenerator.generateNews();
    }

    public static String genResponse(NewsGenerator newsGenerator, int responseType, MessageReceivedEvent event) {
        return newsGenerator.generateResponse(responseType, event);
    }
    private static int getRandomIntegerBetweenRange(int min, int max){
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
