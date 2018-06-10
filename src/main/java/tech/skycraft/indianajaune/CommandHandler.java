package tech.skycraft.indianajaune;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import tech.skycraft.indianajaune.lavaplayer.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;

import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class CommandHandler {

    // A static map of commands mapping from command string to the functional impl
    private static Map<String, Command> commandMap = new HashMap<>();

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();;;
    private static final Map<Long, GuildMusicManager> musicManagers  = new HashMap<>();;

    // Statically populate the commandMap with the intended functionality
    // Might be better practise to do this from an instantiated objects constructor
    static {

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        // If the IUser that called this is in a voice channel, join them
        commandMap.put("join", (event, args) -> {

            IVoiceChannel userVoiceChannel = event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel();

            if(userVoiceChannel == null)
                return;

            userVoiceChannel.join();

        });

        commandMap.put("leave", (event, args) -> {

            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();

            if(botVoiceChannel == null)
                return;

            AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(event.getGuild());

            audioP.clear();

            botVoiceChannel.leave();

        });

        // Plays the first song found containing the first arg
        commandMap.put("music", (event, args) -> {

            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();

            if(botVoiceChannel == null) {
                BotUtils.sendMesasge(event.getChannel(), "Not in a voice channel, join one and then use joinvoice");
                return;
            }

            // Turn the args back into a string separated by space
            String searchStr = String.join(" ", args);

            loadAndPlay(event.getChannel(), searchStr);


        });

        // Skips the current song
        commandMap.put("next", (event, args) -> {

            skipTrack(event.getChannel());

        });

        commandMap.put("player", (event, args) -> {

            EmbedBuilder profile = new EmbedBuilder();
            String username = String.join(" ", args);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                URL uuidurl = new URL("https://api.mojang.com/users/profiles/minecraft/"  + URLEncoder.encode(username, "UTF-8"));
                JsonNode uuid = objectMapper.readValue(uuidurl, JsonNode.class);

                JsonNode brandNode = uuid.get("id");
                String id = brandNode.asText();
                System.out.println("id = " + id);
                profile.withDesc("UUID : ");
                profile.appendDesc(id);
                profile.withDescription("UUID : ");
                profile.appendDescription(id);

                JsonNode doorsNode = uuid.get("name");
                String name = doorsNode.asText();
                System.out.println("name = " + name);
            } catch (Exception e) {

                System.out.println("Error: " + e);
                e.printStackTrace();
            }


            try {
                URL uuidurl = new URL("https://api.mojang.com/user/profiles/"  + URLEncoder.encode(username, "UTF-8") + "/names");
                JsonNode history = objectMapper.readValue(uuidurl, JsonNode.class);



                JsonNode nameNode = history.get("name");
                String name = nameNode.asText();
                System.out.println("name = " + name);
                profile.appendField("Original name", name, true);
            } catch (Exception e) {

                System.out.println("Error: " + e);
                e.printStackTrace();
            }

            LocalDateTime now = LocalDateTime.now();

            LocalDate localDate = now.toLocalDate();

            long millis = System.currentTimeMillis();

            System.out.println("skin request of" + username + "at " + now);





            /*
            profile.appendField("fieldTitleInline", "fieldContentInline", true);
            profile.appendField("fieldTitleInline2", "fieldContentInline2", true);
            profile.appendField("fieldTitleNotInline", "fieldContentNotInline", false);
            profile.appendField(":tada: fieldWithCoolThings :tada:", "[hiddenLink](http://i.imgur.com/Y9utuDe.png)", false);
            */
            profile.withAuthorName(username);
            profile.withAuthorIcon("https://mcapi.ca/avatar/" + username);
            profile.withAuthorUrl("https://minecraft.net");

            profile.withColor(0, 170, 0);

            profile.withTitle(username);
            profile.withTimestamp(millis);
            profile.withUrl("https://minecraft.net");
            profile.withImage("https://mcapi.ca/skin/" + username);

            profile.withFooterIcon("https://minecraft.net/favicon-32x32.png");
            profile.withFooterText("Minecraft profile");
            profile.withFooterIcon("https://minecraft.net/favicon-32x32.png");
            profile.withThumbnail("http://blogosquare.com/wp-content/uploads/2013/04/minecraft_banner.png");





            RequestBuffer.request(() -> event.getChannel().sendMessage(profile.build()));

        });


        commandMap.put("wiki", (event, args) -> {
            String search = String.join(" ", args);
            BotUtils.sendMesasge(event.getChannel(), "http://minecraft.gamepedia.com/index.php?search=" + search);


        });


        commandMap.put("help", (event, args) -> {
            BotUtils.sendDM(event.getAuthor(), "```Here is what you can request to me : \n" +
                    "/player <playername> : request some informations about a minecraft player \n" +
                    "/query <address> : request some informations about a minecraft server \n" +
                    "/wiki <search> : request to the minecraft wiki your search \n" +
                    "/join : join the vocal channel you are currently on \n" +
                    "/leave : leave the voice channel i am currently on  \n" +
                    "/music <song> : search <song> in the internet and automatically plays the first found \n" +
                    "/next = skip the song currently playing  ```");
            BotUtils.sendMesasge(event.getChannel(), "You received a Private message");

        });

        commandMap.put("blob", (event, args) -> {



        });



    }

    private static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

    private static void loadAndPlay(final IChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                BotUtils.sendMesasge(channel, "Adding to queue " + track.getInfo().title);

                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                BotUtils.sendMesasge(channel, "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")");

                play(musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                BotUtils.sendMesasge(channel, "Nothing found by " + trackUrl);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                BotUtils.sendMesasge(channel, "Could not play: " + exception.getMessage());
            }
        });
    }

    private static void play(GuildMusicManager musicManager, AudioTrack track) {

        musicManager.scheduler.queue(track);
    }

    private static void skipTrack(IChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        BotUtils.sendMesasge(channel, "Skipped to next track.");
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {

        // Note for error handling, you'll probably want to log failed commands with a logger or sout
        // In most cases it's not advised to annoy the user with a reply incase they didn't intend to trigger a
        // command anyway, such as a user typing ?notacommand, the bot should not say "notacommand" doesn't exist in
        // most situations. It's partially good practise and partially developer preference

        // Given a message "/test arg1 arg2", argArray will contain ["/test", "arg1", "arg"]
        String[] argArray = event.getMessage().getContent().split(" ");

        // First ensure at least the command and prefix is present, the arg length can be handled by your command func
        if(argArray.length == 0)
            return;

        // Check if the first arg (the command) starts with the prefix defined in the utils class
        if(!argArray[0].startsWith(BotUtils.BOT_PREFIX))
            return;

        // Extract the "command" part of the first arg out by just ditching the first character
        String commandStr = argArray[0].substring(1);

        // Load the rest of the args in the array into a List for safer access
        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0); // Remove the command

        // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists

        if(commandMap.containsKey(commandStr))
            commandMap.get(commandStr).runCommand(event, argsList);

    }

}
