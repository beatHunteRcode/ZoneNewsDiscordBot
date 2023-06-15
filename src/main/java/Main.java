import music.GuildMusicManager;
import music.MusicPlayerManager;
import music.MusicQueriesHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main extends ListenerAdapter {

    private static final int MIN_TIME_DELAY = 1000 * 60 * 10;
    private static final int MAX_TIME_DELAY = 1000 * 60 * 15;

    private char prefix = '-';

    private ArrayList<String> lastNewsList = new ArrayList<>();
    public static Map<String, Boolean> mapOfGenerators = new HashMap<>();
    public  static Map<String, Thread> mapOfThreads = new HashMap<>();
    public static List<String> urlsList = new ArrayList<>();

    private boolean enableMemes = false;

    NewsGenerator newsGenerator = new NewsGenerator();

    private static Thread downloadMemesThread = new Thread();

    public static void main(String[] args) throws LoginException {
        String botToken = readTokenFile(Resources.BOT_TOKEN_FILE_PATH);
        JDABuilder builder = JDABuilder.createDefault(botToken);
        builder.addEventListeners(new Main());
        builder.build();

        instLogin();

        downloadMemesThread = new Thread(() -> {
           while (urlsList.size() == 0) {
               Main.downloadMemes(Resources.getCurrentIGClient());
               try {
                   Thread.sleep(getRndIntInRange(MIN_TIME_DELAY, MAX_TIME_DELAY));
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });
        downloadMemesThread.start();

        Calendar calendar = Calendar.getInstance();
        Resources.setCurrentDayNumber(calendar.get(Calendar.DAY_OF_MONTH));
        Resources.setCurrentMonthNumber(calendar.get(Calendar.MONTH));
        Resources.setCurrentYearNumber(calendar.get(Calendar.YEAR));

//        NewsGenerator newsGenerator = new NewsGenerator();
//        for (int i = 0; i < 1; i++) {
//            System.out.println(newsGenerator.generateNews());
//            System.out.println();
//        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("новость")) {
            String news = newsGenerator.generateNews();
            event.reply(news).queue();
            addNewsToList(news);
            genResponse(news, newsGenerator, event);
        } else if (command.equals("анекдот")) {
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
                event.reply(newsBuilder.toString()).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
            genResponseToJoke(newsGenerator, event);
        } else if (command.equals("start-news")) {
            if (!mapOfGenerators.containsKey(event.getChannel().getName())) {
                Thread thread = new Thread(() -> {
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
                });
                thread.start();
                mapOfGenerators.put(event.getChannel().getName(), true);
                mapOfThreads.put(event.getChannel().getName(), thread);
                event.reply("Лента новостей запущена").queue();
            } else {
                if (mapOfGenerators.get(event.getChannel().getName())) {
                    event.reply("Error: Лента новостей уже запущена!").queue();
                }
            }
        } else if (command.equals("stop-news")) {
            mapOfThreads.get(event.getChannel().getName()).interrupt();
            mapOfThreads.remove(event.getChannel().getName());
            mapOfGenerators.remove(event.getChannel().getName());
            event.reply("Лента новостей остановлена").queue();
        } else if (command.equals("недавнее")) {
            event.reply("Последние 5 новостей:").queue();
            for (String news : lastNewsList) {
                event.getChannel().sendMessage(news).queue();
            }
        } else if (command.equals("ch-list")) {
            getChannelsWithGensList(event);
        } else if (command.equals("stop-all")) {
            deleteGenInAllServerChannels(event);
        } else if (command.equals("bot-info")) {
            event.reply(Resources.getBotInfo()).queue();
        } else if (command.equals("мем")) {
            StringBuilder builder = new StringBuilder();
            builder.append(newsGenerator.genName());
            builder.append(":\n");
            builder.append(newsGenerator.genMemeNews());
            event.reply(builder.toString()).queue();
            if (!builder.toString().contains(Resources.getNoMemePhrase())) genResponseToJoke(newsGenerator, event);
        } else if (command.equals("enable-memes")) {
            if (!enableMemes) {
                enableMemes = true;
                event.reply("Мемы **включены**").queue();
            }
            else {
                enableMemes = false;
                event.reply("Мемы **выключены**").queue();
            }
        } else if (command.equals("play")) {
            OptionMapping messageOption = event.getOption("youtube-url");
            if (messageOption != null) {
                String message = messageOption.getAsString();
                if (!event.getMember().getVoiceState().inAudioChannel()) {
                    event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
                }
                else {
                    //bot joining in channel process
                    if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                        final AudioManager audioManager = event.getGuild().getAudioManager();
                        final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                        audioManager.openAudioConnection(memberChannel);
                    }
                    String URL = message;
                    if (!MusicQueriesHandler.isURL(URL)) {
                        URL = "ytsearch:" + URL + " audio";
                    }
                    MusicPlayerManager.getINSTANCE().loadAndPlay(event.getGuildChannel().asTextChannel(), URL);
                    event.reply(URL).queue();
                }
            }
        } else if (command.equals("pause")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                //bot leaving current channel process
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    if (!musicManager.audioPlayer.isPaused()) {
                        musicManager.trackScheduler.onPlayerPause(musicManager.audioPlayer);
                        event.reply("Музыка на паузе").queue();
                    }
                    else {
                        event.reply("Музыка уже на паузе!").queue();
                    }
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        } else if (command.equals("resume")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                //bot leaving current channel process
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    if (musicManager.audioPlayer.isPaused()) {
                        musicManager.trackScheduler.onPlayerResume(musicManager.audioPlayer);
                        event.reply("Музыка возобновлена").queue();
                    }
                    else {
                        event.reply("Музыка уже запущена!").queue();
                    }
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        } else if (command.equals("leave")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                //bot leaving current channel process
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    final AudioManager audioManager = event.getGuild().getAudioManager();
                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    musicManager.trackScheduler.tracksQueue.clear();
                    audioManager.closeAudioConnection();
                    musicManager.audioPlayer.destroy();
                    event.reply("Покинул `" + audioManager.getConnectedChannel().getName() + "`").queue();
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        } else if (command.equals("loop")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    if (!musicManager.trackScheduler.isLoop()) {
                        musicManager.trackScheduler.setLoop(true);
                        event.reply("Повтор очереди **включен**").queue();
                    }
                    else {
                        musicManager.trackScheduler.setLoop(false);
                        event.reply("Повтор очереди **выключен**").queue();
                    }
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        } else if (command.equals("queue")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    final AudioManager audioManager = event.getGuild().getAudioManager();
                    StringBuilder sb = new StringBuilder();
                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    Object[] trackQueue = musicManager.trackScheduler.tracksQueue.toArray();
                    sb.append("В очереди **").append(musicManager.trackScheduler.tracksQueue.size()).append(" треков**\n\n");
                    for (int i = 0; i < (
                            (musicManager.trackScheduler.tracksQueue.size() < musicManager.trackScheduler.trackPageSize)
                                    ? musicManager.trackScheduler.tracksQueue.size() : musicManager.trackScheduler.trackPageSize);
                         i++) {
                        if (trackQueue[i] != null) {
                            sb.append("**").append(i + 1).append(".** ").
                                    append(((AudioTrack)trackQueue[i]).getInfo().title).
                                    append(" - ").
                                    append(((AudioTrack)trackQueue[i]).getInfo().author).append("\n");
                        }
                    }
                    if (musicManager.trackScheduler.tracksQueue.size() > musicManager.trackScheduler.trackPageSize) {
                        sb.append("\n").
                                append("... и еще **").
                                append(musicManager.trackScheduler.tracksQueue.size() - musicManager.trackScheduler.trackPageSize).
                                append("** треков");
                    }
                    if (!sb.toString().isEmpty()) event.reply(sb.toString()).queue();
                    else event.reply("Нет треков в очереди").queue();
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        } else if (command.equals("np")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Сейчас играет: `");

                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    sb.append(musicManager.trackScheduler.audioPlayer.getPlayingTrack().getInfo().title).
                            append(" - ").
                            append(musicManager.trackScheduler.audioPlayer.getPlayingTrack().getInfo().author).
                            append("`");
                    event.reply(sb.toString()).queue();
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        } else if (command.equals("skip")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    if (musicManager.audioPlayer.getPlayingTrack() != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Пропущено `");
                        sb.append(musicManager.trackScheduler.audioPlayer.getPlayingTrack().getInfo().title).
                                append(" - ").
                                append(musicManager.trackScheduler.audioPlayer.getPlayingTrack().getInfo().author).
                                append("`");
                        event.reply(sb.toString()).queue();

                        musicManager.trackScheduler.onTrackEnd(musicManager.audioPlayer, musicManager.audioPlayer.getPlayingTrack(), AudioTrackEndReason.FINISHED);
                    }
                    else event.reply("В данный момент музыка не играет").queue();
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        } else if (command.equals("shuffle")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply(MusicQueriesHandler.YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE).queue();
            }
            else {
                if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                    GuildMusicManager musicManager = MusicPlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                    if (!musicManager.trackScheduler.tracksQueue.isEmpty()) {
                        List<AudioTrack> tracksList = new LinkedList<>(musicManager.trackScheduler.tracksQueue);
                        Collections.shuffle(tracksList);
                        musicManager.trackScheduler.tracksQueue.clear();
                        musicManager.trackScheduler.tracksQueue.addAll(tracksList);
                        event.reply("Очередь перемешана").queue();
                    }
                    else event.reply("В данный момент очередь пуста").queue();
                }
                else {
                    event.reply("Бот не находится в голосовом канале!").queue();
                }
            }
        }

        if (!event.getUser().getName().equalsIgnoreCase("новости зоны")) {
            if (   (command.toLowerCase().contains("анек") ||
                    command.toLowerCase().contains("шут") ||
                    command.toLowerCase().contains("рофл") ||
                    command.toLowerCase().contains("юмор")) &&
                    !command.toLowerCase().contains("-")
            ) {
                genResponseToJoke(newsGenerator, event);
            }
        }

        if (enableMemes) dateCheck();
    }

    @Override
    public void onReady(ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("новость", "Вывести новость с просторов Зоны"));
        commandData.add(Commands.slash("анекдот", "Вывести анекдот от сталкера"));
        commandData.add(Commands.slash("мем", "Показать мем от сталкера"));
        commandData.add(Commands.slash("start-news", "Включить ленту новостей в текущем канале"));
        commandData.add(Commands.slash("stop-news", "Выключить ленту новостей в текущем канале"));
        commandData.add(Commands.slash("недавнее", "Вывести 5 последних новостей"));
        commandData.add(Commands.slash("ch-list", "Вывести список всех каналов на сервере, на которых запущена лента новостей"));
        commandData.add(Commands.slash("bot-info", "Вывести информацию о боте"));

        //---------------MUSIC SECTION---------------
        OptionData option1 = new OptionData(OptionType.STRING, "youtube-url", "Ссылка на видео на YouTube", true);
        commandData.add(Commands.slash("play", "Проигрывает видео с YouTube").addOptions(option1));

        commandData.add(Commands.slash("pause", "Приостановить проигрывание музыки"));
        commandData.add(Commands.slash("resume", "Возобновить проигрывание музыки"));
        commandData.add(Commands.slash("leave", "Покинуть текущий голосовой канал"));
        commandData.add(Commands.slash("loop", "Включить/Выключить повтор очереди"));
        commandData.add(Commands.slash("queue", "Вывести текщую очередь"));
        commandData.add(Commands.slash("np", "Вывести текущий проигрываемый трек"));
        commandData.add(Commands.slash("skip", "Пропустить текущий проигрываемый трек"));
        commandData.add(Commands.slash("shuffle", "Перемешать очередь"));
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println( "Message from " + event.getAuthor().getName() + " in " + event.getChannel().getName() +
                            " from " + event.getGuild().getName() +
                            " (" + new Date().toString() + ")" + ":\n"
                            + "[--- " + event.getMessage().getContentDisplay() + " ---]");
    }

    public static void genResponse(String news, NewsGenerator newsGenerator, SlashCommandInteractionEvent event) {
        if (news.contains("(Зомбированные):")) {
            String response = newsGenerator.generateResponse(1, event);
            event.getChannel().sendMessage(response).queue();
        }
        if (newsGenerator.getNewsType() == 8 || newsGenerator.getNewsType() == 13) {//если тип новости - анекдот/мем - генерируем реакцию на анекдот/мем
            genResponseToJoke(newsGenerator, event);
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

    private static void genResponseToJoke(NewsGenerator newsGenerator, SlashCommandInteractionEvent event) {
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
    private static int getRndIntInRange(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
    }

    private void deleteGenInAllServerChannels(SlashCommandInteractionEvent event) {
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

    private void getChannelsWithGensList(SlashCommandInteractionEvent event) {
        List<String> channelsNamesList = new LinkedList<>();
        for (TextChannel ch : event.getGuild().getTextChannels()) {
            channelsNamesList.add(ch.getName());
        }

        if (mapOfGenerators.isEmpty()) {
            event.reply("Нигде ещё не запущена лента новостей.").queue();
        }
        for (String key : mapOfGenerators.keySet()) {
            if (channelsNamesList.contains(key)) event.reply(key).queue();
        }
    }

    private void getInfoAboutChannel(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getChannel().getName()).queue();
    }

    private static void instLogin() {
        try {
            IGClient client = IGClient.builder()
                    .username("***")
                    .password("***")
                    .login();

            Resources.setCurrentIGClient(client);
        } catch (IGLoginException e) {
            e.printStackTrace();
        }
    }

    private static void downloadMemes(IGClient client) {
            client.actions().search().searchUser("stalker.mem").thenAccept(usersSearchResponse -> {
                downloadTask(usersSearchResponse, client);
            }).join();


            client.actions().search().searchUser("stalker.mem4").thenAccept(usersSearchResponse -> {
                downloadTask(usersSearchResponse, client);
            }).join();
    }

    private static void downloadTask(UsersSearchResponse usersSearchResponse, IGClient client) {
        Long pk = usersSearchResponse.getUsers().get(0).getPk();
        FeedUserRequest req = new FeedUserRequest(pk);
        FeedUserResponse response = client.sendRequest(req).join();
        while (response.isMore_available()) {
            response = client.sendRequest(new FeedUserRequest(pk, response.getNext_max_id())).join();
            for (TimelineMedia item : response.getItems()) {
                if (item.getMedia_type().equals("1")) {
                    if (((TimelineImageMedia) item).getImage_versions2() != null) {
                        urlsList.add(((TimelineImageMedia) item).getImage_versions2().getCandidates().get(0).getUrl());
                    }
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Collections.shuffle(urlsList);
        System.out.println( "[----- " + new Date().toString() + ": SUCCESSFULLY DOWNLOADED ALL " + urlsList.size() +
                            " MEMES from " + usersSearchResponse.getUsers().get(0).getFull_name() + " -----]");
    }

    private static String readTokenFile(String fileName) {
        String token = "";
        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(fileName));
            token = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    private void dateCheck() {
        Calendar calendar = Calendar.getInstance();
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        int monthNow = calendar.get(Calendar.MONTH);
        int yearNow = calendar.get(Calendar.YEAR);
        if (dayNow != Resources.getCurrentDayNumber() ||
            monthNow != Resources.getCurrentMonthNumber() ||
            yearNow != Resources.getCurrentYearNumber()) {
            urlsList.clear();
            downloadMemes(Resources.getCurrentIGClient());
            Resources.setCurrentDayNumber(dayNow);
            Resources.setCurrentMonthNumber(monthNow);
            Resources.setCurrentYearNumber(yearNow);
        }
    }

}
