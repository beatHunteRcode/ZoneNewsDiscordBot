import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    private char prefix = '-';

    public static void main(String[] args) throws LoginException {
//        String botToken = "NjM2OTg4NzAyMjI0MDIzNTg0.XbHn9Q.WA6RI3unJIJaqQb7cJlm7EQrAO0";
//        JDABuilder builder = new JDABuilder(AccountType.BOT);
//        builder.setToken(botToken);
//        builder.addEventListeners(new Main());
//        builder.build();


        NewsGenerator newsGenerator = new NewsGenerator();
        newsGenerator.generateNewsJSON();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println( "Message from " + event.getAuthor().getName() + ": " +
                            event.getMessage().getContentDisplay());
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "новость")) { //новость
            int rndNumb = getRandomIntegerBetweenRange(1, 5);
            NewsGenerator newsGenerator = new NewsGenerator(rndNumb);
            String news = newsGenerator.generateNewsJSON();
            event.getChannel().sendMessage(news).queue();
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "помощь")) { //новость
            showCommandList(event);
        }

    }

    public void showCommandList(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                        "Список команд: \n" +
                        prefix + "новость : выводит новость\n" +
                        prefix + "недавнее : вывод список последних 5 новостей\n"
        ).queue();
    }

    private static int getRandomIntegerBetweenRange(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
    }

}
