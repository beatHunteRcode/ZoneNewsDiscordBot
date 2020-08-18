import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.*;

public class Main extends ListenerAdapter {
    private char prefix = '-';
    private boolean generatorOn = false;
    private NewsGenerator newsGenerator = new NewsGenerator();
    private ArrayList<String> lastNewsList = new ArrayList<>();
    Date date = new Date();

    public static void main(String[] args) throws LoginException {
        String botToken = "NjM2OTg41337youdontgetmydiscordbottoken1337SJJckVIqk";
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(botToken);
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
        Thread thread = new Thread();

        System.out.println( "Message from " + event.getAuthor().getName() + ": " +
                            event.getMessage().getContentDisplay());
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "старт")) { //новость
            showCommandList(event);
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "новость")) { //новость
            String news = genNews(newsGenerator);
            event.getChannel().sendMessage(news).queue();
            addNewsToList(news);

            if (news.contains("Зомбированные")) {
                String response = genResponse(newsGenerator, 1);
                event.getChannel().sendMessage(response).queue();
                addNewsToList(news);

            }
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "помощь")) { //новость
            showCommandList(event);

        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "startNews")) {
            generatorOn = true;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String news = genNews(newsGenerator);
                        event.getChannel().sendMessage(news).queue();
                        addNewsToList(news);
                        if (news.contains("Зомбированные")) {
                            String response = genResponse(newsGenerator, 1);
                            event.getChannel().sendMessage(response).queue();
                            addNewsToList(response);

                        }
                        try {
                            Thread.sleep(getRandomIntegerBetweenRange(1000 * 60 * 20, 1000 * 60 * 30));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();

        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "stopNews")) {
            generatorOn = false;
            thread.interrupt();

        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "недавнее")) {
            for (String news : lastNewsList) {
                event.getChannel().sendMessage(news).queue();
            }

        }
    }

    public void showCommandList(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                        "Список команд: \n" +
                                prefix + "новость : выводит новость\n" +
                                prefix + "недавнее : выводит список последних 5 новостей\n" +
                                prefix + "startNews : включить появление новостей в случайный момент времени\n" +
                                prefix + "stopNews : выключить появление новостей в случайный момент времени\n"
        ).queue();
    }

    public static String genNews(NewsGenerator newsGenerator) {
        return newsGenerator.generateNews();
    }

    public static String genResponse(NewsGenerator newsGenerator, int responseType) {
        return newsGenerator.generateResponse(responseType);
    }
    private static int getRandomIntegerBetweenRange(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
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

}
