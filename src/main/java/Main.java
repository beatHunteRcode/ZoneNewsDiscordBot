import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.Timer;

public class Main extends ListenerAdapter {
    private char prefix = '-';
    private boolean generatorOn = false;
    private NewsGenerator newsGenerator = new NewsGenerator();

    public static void main(String[] args) throws LoginException {
        String botToken = "NjM2OTg4NzAyM1337youcantgetmydiscorbotoken1337SJJckVIqk";
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
    public void onReady(@Nonnull ReadyEvent event) {

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println( "Message from " + event.getAuthor().getName() + ": " +
                            event.getMessage().getContentDisplay());
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "новость")) { //новость
            String news = genNews(newsGenerator);
            event.getChannel().sendMessage(news).queue();
            if (news.contains("Зомбированные")) {
                String response = genResponse(newsGenerator, 1);
                event.getChannel().sendMessage(response).queue();
            }
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "помощь")) { //новость
            showCommandList(event);
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "genOn")) {
            generatorOn = true;

            Timer timer = new Timer();
            MyTimerTask timerTask = new MyTimerTask(newsGenerator);
            timer.schedule(timerTask, 2 * 1000);
            timerTask.run();
            event.getChannel().sendMessage(timerTask.news).queue();
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "genOff")) { generatorOn = false; }
    }

    public void showCommandList(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                        "Список команд: \n" +
                                prefix + "новость : выводит новость\n" +
                                prefix + "недавнее : выводит список последних 5 новостей\n" +
                                prefix + "genOn : включить появление новостей в случайный момент времени\n" +
                                prefix + "genOff : выключить появление новостей в случайный момент времени\n"
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

}
