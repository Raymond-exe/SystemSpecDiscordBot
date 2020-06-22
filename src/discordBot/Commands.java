package discordBot;


import net.dv8tion.jda.api.Permission;
import pcParts.*;
import webAccess.*;
import records.*;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class Commands extends ListenerAdapter {

    private static String betaServers = "709528247230267473, 511968553021472781";
    private static String errorLogChannelId = "639894236183003157";
    private static String feedbackChannelId = "638183306642456577";
    private static String consoleChannelId = "711280957142990958";

    private static final int CPU_INDEX = 0;
    private static final int GPU_INDEX = 1;
    private static final int RAM_INDEX = 2;


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        try {

            String message = event.getMessage().getContentRaw().trim();
            String prefix = Recordkeeper.getGuildPrefix(event.getGuild().getId());
            int endOfFirstArg = (message.contains(" ") ? message.indexOf(" ") : message.length());


            if(message.length() < prefix.length() + 2)
                return;

            //Checks if message starts with a mention to the bot
            if(message.startsWith("<@!" + DiscordBot.getJda().getSelfUser().getId() + ">")) {
                message = message.substring(message.indexOf(">") + 1).trim();


                switch (message) {
                    case "":
                        event.getChannel().sendMessage("Make sure to let me know what game you want to play by using __\"" + DiscordBot.getJda().getSelfUser().getAsMention() + " `[YOUR GAME HERE]`\"__").queue();
                        break;
                    case "help":
                        help(event);
                        break;
                    default:
                        canUserPlay(event, message);
                        break;
                }
            }

            //Checks if message starts with prefix
            if (message.startsWith(prefix)) {
                message = message.substring(prefix.length(), endOfFirstArg);

                if (!betaServers.contains(event.getGuild().getId())) {
                    event.getChannel().sendMessage("Sorry! " + DiscordBot.getJda().getSelfUser().getAsMention() + " is currently in beta and *not available in public servers*. Please message **@Ramen.exe#8147** to add your server to the beta testing list, thank you!").queue();
                    return;
                }
            }
            else return;

            switch (message) {
                case "ping":
                case "pong":
                    ping(event);
                    break;
                case "rules":
                    rules(event);
                    break;
                case "search":
                    search(event);
                    break;
                case "gamespecs":
                    gamespecs(event);
                    break;
                case "gameinfo":
                    gameinfo(event);
                    break;
                case "info":
                    info(event);
                    break;
                case "help":
                    help(event);
                    break;
                case "resetspecs":
                case "resetinfo":
                    resetspecs(event);
                    break;
                case "myspecs":
                case "my":
                case "specs":
                case "myinfo":
                    myspecs(event);
                    break;
                case "setspecs":
                case "set":
                    setspecs(event);
                    break;
                case "getspecs":
                    getspecs(event);
                    break;
                case "setprivacy":
                    setprivacy(event);
                    break;
                case "setprefix":
                    setprefix(event);
                    break;
                case "feedback":
                    feedback(event);
                    break;
                case "compare":
                    compare(event);
                    break;
                default:
                    event.getChannel().sendMessage(
                            "**\""
                                    + message
                                    + "\"** is an unrecognized command, try **\""
                                    + prefix +
                                    "help\"** for the list of commands.").queue();
            }
        } catch (Exception e) {


            //Logging error in console
            sendToConsole("Error occured after a user ran the command `" + event.getMessage().getContentRaw() + "`, check #caniplay-error-log for more info.");

            /*/Logging error in error log channel
            DiscordBot.getJda().getTextChannelById(errorLogChannelId)
                    .sendMessage("**__"+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) +" PST__**\n**Message at fault:** `" + event.getMessage().getContentRaw() + "`\n**StackTrace:**\n```\n" + StringTools.getErrorAsStackTrace(e) + "```")
                    .queue(); //*/

            //Send message to user indicating error
            event.getChannel().sendMessage("Uh-oh, I've run into an error! If you know what happened, use `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "feedback` to let us know what happened. Thanks!");
            throw e;
        }
    }



    /***** COMMAND METHODS *****/

    private void ping(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(":ping_pong: **" + (event.getMessage().getContentRaw().contains("pong") ? "Ping" : "Pong") + "!**").queue();
    }

    private void rules(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("How am I supposed to know?").queue();
        event.getChannel().sendMessage("Ask an admin").queue();
    }

    private void cpuSearch(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String query = message.substring(message.toLowerCase().indexOf("cpu") + 3).trim();

        ArrayList<Cpu> results = Searcher.searchCpu(query, 10);

        String link = "https://benchmarks.ul.com/compare/best-cpus?search=" + StringTools.cleanString(query.trim()).toLowerCase();
        for (int i = 0; i < link.length(); i++) {
            if (link.charAt(i) == ' ') {
                link = link.substring(0, i) + "%20" + link.substring(i+1);
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("CPU Search Results for " + query, link)
                .setThumbnail(DiscordBot.getJda().getSelfUser().getAvatarUrl())
                .setDescription("To set your cpu, type `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "setspecs cpu [YOUR CPU]`")
                .setColor(Color.WHITE);

        if (results.isEmpty()) {
            embed.setTitle(":warning: No CPUs found for `" + query + "`.");
            embed.setDescription("Maybe try another search term?");
            embed.setColor(Color.ORANGE);
        } else {
            for (int i = 0; i < results.size(); i++) {
                embed.addField(results.get(i).getName(), "Ranking: " + results.get(i).getRank(), false);
            }
        }

        event.getChannel().sendMessage(embed.build()).queue();
    }

    private void gpuSearch(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String query = message.substring(message.toLowerCase().indexOf("gpu") + 3).trim();

        ArrayList<Gpu> results = Searcher.searchGpu(query, 10);
        //.println(results);

        String link = "https://benchmarks.ul.com/compare/best-gpus?search=" + StringTools.cleanString(query.trim()).toLowerCase();
        for (int i = 0; i < link.length(); i++) {
            if (link.charAt(i) == ' ') {
                link = link.substring(0, i) + "%20" + link.substring(i+1);
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("GPU Search Results for " + query, link)
                .setThumbnail(DiscordBot.getJda().getSelfUser().getAvatarUrl())
                .setDescription("To set your gpu, type `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "setspecs gpu [YOUR GPU]`")
                .setColor(Color.WHITE);

        if (results.isEmpty()) {
            embed.setTitle(":warning: No GPUs found for `" + query + "`.");
            embed.setDescription("Maybe try another search term?");
            embed.setColor(Color.ORANGE);
        } else {
            for (Gpu entry : results) {
                embed.addField(entry.getName(), "Ranking: " + entry.getRank(), false);
            }
        }

        event.getChannel().sendMessage(embed.build()).queue();
        //event.getChannel().sendMessage(results.toString()).queue();

    }

    private void search(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().split(" ");

        if (messageArgs[1].equalsIgnoreCase("cpu")) {
            cpuSearch(event);
            return;
        } else if (messageArgs[1].equalsIgnoreCase("gpu")) {
            gpuSearch(event);
            return;
        } else if (messageArgs[1].equalsIgnoreCase("ram")) {
            event.getChannel().sendMessage("You don't need to search for RAM, silly!").queue();
            event.getChannel().sendMessage("Set your amount of RAM by typing `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "setspecs RAM [YOUR RAM IN GB]`").queue();
            return;
        } else if (messageArgs[1].equalsIgnoreCase("os")) {
            event.getChannel().sendMessage("You don't need to search for your operating system, silly!").queue();
            event.getChannel().sendMessage("Currently, " + DiscordBot.getJda().getSelfUser().getAsMention() + " only works with Windows specs and games. If you use an apple product and would like support to be added in the future, send us feedback by typing \"" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "feedback\" to let us know!").queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder().setThumbnail(DiscordBot.getJda().getSelfUser().getAvatarUrl());

        String query = getArgsAfter(0, messageArgs, false);
        //GameInfo tempGame = new GameInfo(Searcher.getSearchResult(query));
        try {
            long deltaTime = System.currentTimeMillis();
            int searchResultLimit = 10;
            ArrayList<String> tempArray = new ArrayList<>(Arrays.asList(StringTools.toStringArray(Searcher.searchFor(query).toArray())));

            //*
            for (int i = 0; i < tempArray.size(); i++) {
                if (tempArray.get(i).toLowerCase().contains("forgotten password")
                        || tempArray.get(i).toLowerCase().contains("lostpassword")
                        || tempArray.get(i).contains("img src")) {
                    tempArray.remove(i--); //remove index i, AND THEN decrease i
                }
            } //*/

            embed.setTitle("System requirement search results for " + query, Searcher.getGameSiteLink(query));
            embed.setDescription(":stopwatch: **" + (tempArray.size() == 25 ? "25+" : tempArray.size()) + " search result" + (tempArray.size() == 1 ? "" : "s") + "** in " + (float)(System.currentTimeMillis() - deltaTime)/1000 + " seconds." + (tempArray.size() > searchResultLimit ? "\nHere are the top " + searchResultLimit + " results:": ""));
            embed.setFooter("Type `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "gamespecs [GAME]` to see system requirements for the given game.");

            for (int i = 0; i < tempArray.size() && i < searchResultLimit; i++) {
                embed.addField((tempArray.get(i).substring(0, tempArray.get(i).lastIndexOf("("))), tempArray.get(i).substring(tempArray.get(i).lastIndexOf("(") + 1, tempArray.get(i).lastIndexOf(")")), false);
            }
        } catch (Exception e) {
            embed.setTitle(":warning: No games titled `" + query.trim() + "` were found.");
            embed.setDescription("Maybe try searching for another title?");
            embed.setColor(Color.ORANGE);
        }
        event.getChannel().sendMessage(embed.build()).queue();
    }

    private void gamespecs(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().split(" ");
        GameInfo gameInfo = new GameInfo(Searcher.getSearchResult(getArgsAfter(0, messageArgs, false)));
        ArrayList<String> minSpecs = gameInfo.getSpecs(0);
        ArrayList<String> recSpecs = gameInfo.getSpecs(1);

        try {
            //String[] result = StringTools.toStringArray(minSpecs.toArray());
            EmbedBuilder embed = new EmbedBuilder()
                    .setImage(gameInfo.getImageUrl())
                    .setThumbnail(DiscordBot.getJda().getSelfUser().getAvatarUrl())
                    .setTitle("System Requirements for " + gameInfo.getTitle(), gameInfo.getWebsite())
                    .setFooter("Type \"" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "gameinfo\" to see information on this title.")
                    .setColor(Color.WHITE);

            String temp;
            String[] titles = new String[] {"CPU - Central Processing Unit", "RAM - Random Access Memory", "GPU - Graphics Processing Unit", "OS - Operating System", "Storage space needed"};
            for (int i = 0; i < minSpecs.size(); i++) {
                temp = StringTools.removeHtmlTags(minSpecs.get(i));

                temp = StringTools.fixString(temp);

                embed.addField(titles[i], temp, false);
            }
            event.getChannel().sendMessage(embed.build()).queue();

            //System.out.print("GPU: ");
            //System.out.print(gameInfo.getGpu());
        } catch (Exception e) {
            event.getChannel().sendMessage("No search results for " + getArgsAfter(0, messageArgs, false).trim() + ", maybe try searching for another title?");
        }


        /*
        for(int i = 0; i < minSpecs.size(); i++) {
            if (minSpecs.get(i).contains("display:table-cell")) {
                String replacement = minSpecs.get(i).substring(minSpecs.lastIndexOf("display:table-cell") + 45);
                minSpecs.set(i, replacement);
            }
        } //*/


        //System.out.println(minSpecs.toString());
        //event.getChannel().sendMessage(minSpecs.toString()).queue();

        //TEST PURPOSES ONLY
        /*
        for (int i = 0; i < minSpecs.size(); i++)
            event.getChannel().sendMessage(minSpecs.get(i)).queue();
        //*/
    }

    private void gameinfo(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().split(" ");
        GameInfo gameInfo = new GameInfo(Searcher.getSearchResult(getArgsAfter(0, messageArgs, false)));

        try {
            String[] result = StringTools.toStringArray(gameInfo.getInfo().toArray());
            EmbedBuilder embed = new EmbedBuilder()
                    .setImage(gameInfo.getImageUrl())
                    .setThumbnail(DiscordBot.getJda().getSelfUser().getAvatarUrl())
                    .setTitle(gameInfo.getTitle(), gameInfo.getWebsite())
                    .setFooter("Type \"" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "gamespecs\" to see system requirements for this game.")
                    .setColor(Color.WHITE);

            String temp;
            for (int i = 1; i < result.length; i++) {
                temp = result[i];
                embed.addField(temp.substring(0, temp.indexOf(":")), StringTools.removeHtmlTags(temp.substring(temp.indexOf(":") + 1)), false);
            }
            event.getChannel().sendMessage(embed.build()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("No search results for " + getArgsAfter(0, messageArgs, false) + ", maybe try searching for another title?");
        }
    }

    private void info(GuildMessageReceivedEvent event) {
    }

    private void help(GuildMessageReceivedEvent event) {
        String prefix = Recordkeeper.getGuildPrefix(event.getGuild().getId());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(DiscordBot.getJda().getSelfUser().getName() + " commands")
                .setThumbnail(DiscordBot.getJda().getSelfUser().getAvatarUrl())
                .addField(DiscordBot.getJda().getSelfUser().getAsMention() + " [game]", "Ask me if you can play a game!", false)
                .addField(prefix + "help", "Displays this dialog box.", false)
                .addField(prefix + "search [query]", "Runs a search for any PC games.", false)
                .addField(prefix + "search [CPU/GPU] [query]", "Runs a search for any specified hardware.", false)
                .addField(prefix + "setspecs [GPU/CPU/RAM] [value]", "Allows users to enter their system specifications.", false)
                .addField(prefix + "myspecs", "Displays *your* system specifications.", false)
                .addField(prefix + "getspecs [@user]", "Displays *another user's* system specifications (only if they disable user privacy).", false)
                .addField(prefix + "setprivacy [ON/OFF/TRUE/FALSE]", "Determines whether or not other users can view your system specifications. (On/True) will leave your hardware private.", false)
                .addField(prefix + "compare [@user]", "Compares your PC specs against another user's PC.", false)
                .addField(prefix + "gameinfo [game]", "Displays details on a given title.", false)
                .addField(prefix + "gamespecs [game]", "Displays system requirements for a given title and compares it to the user's PC specs.", false)
                .addField(prefix + "feedback [text]", "Allows users to write feedback on this bot to an external text channel. Note: Your username and message will be recorded!", false)
                .addField(prefix + "ping", "Want to play a round of ping-pong?", false);

        event.getChannel().sendMessage(embed.build()).queue();
    }

    private void resetspecs(GuildMessageReceivedEvent event) {
        UserSpecs user = new UserSpecs(event.getAuthor().getId(), new Cpu("No Cpu", 0), new Gpu("No Gpu", 0), 0);
        String message;

        if (Recordkeeper.addUserSpecs(user)) {
            message = "Successfully reset your System specs.";
        } else
            message = "An error occurred. If you know what happened, please use " + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "feedback` to let us know what happened.";

        event.getChannel().sendMessage(message).queue();
    }

    private void myspecs(GuildMessageReceivedEvent event) {
        myspecs(event, event.getAuthor());
    }

    private void myspecs(GuildMessageReceivedEvent event, User user) {
        UserSpecs userSpecs = Recordkeeper.getSpecsByUserId(user.getId());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle((user.getId().equals("168376512272269313") ? "Kabrir" : user.getName()) + "'s PC specs")
                .setDescription("use `" + Recordkeeper.getGuildPrefix(user.getId()) + "setspecs` to modify your PC specs.")
                .setThumbnail(user.getAvatarUrl())
                .addField("CPU - Central Processing Unit", "**" + userSpecs.getUserCpu().getName() + "** Rank: " + userSpecs.getUserCpu().getRank(), false)
                .addField("GPU - Graphics Processing Unit", "**" + userSpecs.getUserGpu().getName() + "** Rank: " + userSpecs.getUserGpu().getRank(), false)
                .addField("RAM - Random Access Memory", "**" + userSpecs.getUserRam() + "** GB", false)
                .addField("Overall PC Score", "**" + userSpecs.getPcScore() + "** (" + getPcRank(userSpecs.getPcScore()) + ")", false)
                .setFooter("Privacy setting: " + (userSpecs.getPrivacy() ? "Private" : "Public"));

        if (!userSpecs.getPcDescription().equals("null")) {
            embed.setDescription(userSpecs.getPcDescription());
        }

        event.getChannel().sendMessage(embed.build()).queue();
    }

    private void compare(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().trim().split(" ");
        User targetUser;

        if (messageArgs.length < 2) {
            event.getChannel().sendMessage("Please name a user to compare your PC specs against!").queue();
            event.getChannel().sendMessage("Usage: `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "compare [user]`").queue();
            return;
        }


        //declare the targetUser
        if(messageArgs[1].startsWith("<@!") && messageArgs[1].endsWith(">")) {
            targetUser = DiscordBot.getJda().getUserById(messageArgs[1].substring(3, messageArgs[1].indexOf(">")));
        } else {
            ArrayList<User> userList = new ArrayList<>(DiscordBot.getJda().getUsersByName(messageArgs[1], true));
            if (userList.isEmpty()) {
                event.getChannel().sendMessage("No users named `" + messageArgs[1] + "` were found on this server, try @mentioning them.").queue();
                return;
            } else {
                targetUser = userList.get(0);
            }
        }

        UserSpecs targetSpecs = Recordkeeper.getSpecsByUserId(targetUser.getId());
    }

    private void setspecs(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().trim().split(" ");
        String message;
        UserSpecs user = Recordkeeper.getSpecsByUserId(event.getAuthor().getId());

        if(messageArgs.length < 3) {
            event.getChannel().sendMessage("Usage: `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "setspecs [CPU/GPU/RAM] [VALUE]`").queue();
            return;
        }

        switch (messageArgs[1].toLowerCase()) {
            case "cpu":
                ArrayList<Cpu> cpuResults = Searcher.searchCpu(getArgsAfter(1, messageArgs, false), 25);

                if(cpuResults.isEmpty()) {
                    message = "Sorry, there were no CPU search results for " + getArgsAfter(1, messageArgs, false);
                } else {
                    user.setUserCpu(cpuResults.get(0));
                    Recordkeeper.addUserSpecs(user);
                    message = "Successfully set your CPU to **" + user.getUserCpu().getName() + "**";
                }

                break;
            case "gpu":
                ArrayList<Gpu> gpuResults = Searcher.searchGpu(getArgsAfter(1, messageArgs, false), 25);

                if(gpuResults.isEmpty()) {
                    message = "Sorry, there were no GPU search results for " + getArgsAfter(1, messageArgs, false);
                } else {
                    user.setUserGpu(gpuResults.get(0));
                    Recordkeeper.addUserSpecs(user);
                    message = "Successfully set your GPU to **" + user.getUserGpu().getName() + "**";
                }

                break;
            case "ram":
                int ram;

                try {
                    ram = Integer.parseInt(messageArgs[2].trim());
                    if (ram < 2) { ram = 2; }

                    user.setUserRam(ram);
                    Recordkeeper.addUserSpecs(user);
                    message = "Successfully set your RAM to **" + user.getUserRam() + "** GB";
                } catch (Exception e) {
                    message = "Usage: `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "setspecs RAM [INTEGER]`";
                }
                break;
            case "description":
                String description = getArgsAfter(1, messageArgs, false);

                if (description.contains("@")) {
                    message = "Sorry, your PC description cannot contain any mentions!";
                    break;
                }

                user.setPcDescription(getArgsAfter(1, messageArgs, false));
                Recordkeeper.addUserSpecs(user);

                message = "Your PC's description has been updated to \"" + user.getPcDescription() + "\"";
                break;
            case "privacy":
                setprivacy(event);
                return;
            default:
                message = "Usage: `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "setspecs [CPU/GPU/RAM] [VALUE]`";
                break;
        }

        event.getChannel().sendMessage(message).queue();

    }

    private void getspecs(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().trim().split(" ");
        User targetUser;

        if (messageArgs.length < 2) {
            myspecs(event);
            return;
        }


        //declare the targetUser
        if(messageArgs[1].startsWith("<@!") && messageArgs[1].endsWith(">")) {
            targetUser = DiscordBot.getJda().getUserById(messageArgs[1].substring(3, messageArgs[1].indexOf(">")));
        } else {
            ArrayList<User> userList = new ArrayList<>(DiscordBot.getJda().getUsersByName(messageArgs[1], true));
            if (userList.isEmpty()) {
                event.getChannel().sendMessage("No users named `" + messageArgs[1] + "` were found on this server, try @mentioning them.").queue();
                return;
            } else {
                targetUser = userList.get(0);
            }
        }

        UserSpecs targetSpecs = Recordkeeper.getSpecsByUserId(targetUser.getId());
        if(targetSpecs.getPrivacy()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle((targetUser.getId().equals("168376512272269313") ? "Kabrir" : targetUser.getName()) + "'s PC specs")
                    .setDescription((targetUser.getId().equals("168376512272269313") ? "Kabrir" : targetUser.getName()) + " has set their privacy settings to private.\nYou can only view their PC score.")
                    .setThumbnail(targetUser.getAvatarUrl())
                    .addField("Overall PC Score", "**" + targetSpecs.getPcScore() + "** (" + getPcRank(targetSpecs.getPcScore()) + ")", false)
                    .setFooter("Privacy setting: " + (targetSpecs.getPrivacy() ? "Private" : "Public"));

            if (!targetSpecs.getPcDescription().equals("null")) {
                embed.setDescription(targetSpecs.getPcDescription());
            }

            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        myspecs(event, targetUser);
    }

    private void setprivacy(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().trim().split(" ");
        UserSpecs user = Recordkeeper.getSpecsByUserId(event.getAuthor().getId());
        String prefix = Recordkeeper.getGuildPrefix(event.getGuild().getId());
        String affirm = "on|true|private", deny = "off|false|public";

        if(affirm.contains(messageArgs[1].toLowerCase().trim())) {
            //if the argument is found in the string "affirm", the argument is affirmative
            user.setPrivacy(true);
            System.out.println("true!");
        } else if (deny.contains(messageArgs[1].toLowerCase().trim())) {
            //if the argument is found in the string "deny", the argument is denial
            user.setPrivacy(false);
            System.out.println("false!");
        } else {
            //if the argument is not found in either, it is unrecognized
            event.getChannel().sendMessage("Unrecognized argument. Please use `" + prefix + "setprivacy public` or `" + prefix + "setprivacy private`.").queue();
            return;
        }

        event.getChannel().sendMessage(event.getAuthor().getAsMention() + "'s privacy setting was updated to `" + (Recordkeeper.getSpecsByUserId(event.getAuthor().getId()).getPrivacy() ? "private" : "public") + "`.").queue();
        Recordkeeper.addUserSpecs(user);
    }

    private void setprefix(GuildMessageReceivedEvent event) {
        //event.getChannel().sendMessage("`~setprefix` is not supported while the bot is in beta. Thank you!").queue();

        //TODO find a way to specify ADMINS ONLY
        if(!authorHasAdminPrivileges(event)) {
            event.getChannel().sendMessage("Sorry, you must have server management permissions to change the prefix!").queue();
            return;
        }
        //*
        String prefix = event.getMessage().getContentRaw();
        prefix = prefix.substring(prefix.indexOf("setprefix") + 9).trim();

        if(prefix.contains("@")) {
            event.getChannel().sendMessage("Sorry, prefixes can't include mentions!").queue();
            return;
        }

        Recordkeeper.setPrefix(event.getGuild().getId(), prefix);

        boolean success = Recordkeeper.getGuildPrefix(event.getGuild().getId()).equals(prefix);

        event.getChannel().sendMessage(success ? "Successfully set " + event.getGuild().getName() + "'s prefix to " + prefix: "An error occured, use `" + Recordkeeper.getGuildPrefix(event.getGuild().getId()) + "feedback` to tell us what happened.").queue(); //*/
    }

    private void feedback(GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().split(" ");

        sendToConsole("New feedback, check the #caniplay-feedback channel.");
        DiscordBot.getJda().getTextChannelById(feedbackChannelId).sendMessage(event.getAuthor().getAsTag() + "'s feedback: ```" + getArgsAfter(0, messageArgs, false) + "```").queue();

        event.getChannel().sendMessage("Thank you! Your feedback has been recorded.");
        event.getChannel().sendMessage("If you're reporting a bug, make sure you mention the word \"bug\" in your response, so it can be filed separately.");
    }



    /*****OTHER METHODS *****/

    private String getArgsAfter(int n, String[] array, boolean commas) {
        String output = "";

        for (int i = n + 1; i < array.length; i++) {
            output += array[i] + (commas? ", " : " ");
        }

        return output;
    }

    public void sendToConsole(String message) {
        if (message.contains("<@!") && message.contains(">")) {
            String userId;
            while (message.contains("<@!") && message.contains(">")) {
                userId = message.substring(message.indexOf("<@!") + 3, message.indexOf(">", message.indexOf("<@!")));
                message = message.substring(0, message.indexOf("<@!")) + "@" + DiscordBot.getJda().getUserById(userId).getAsTag() + message.substring(message.indexOf(">", message.indexOf("<@!")) + 1);
            }
        }

        DiscordBot.getJda().getTextChannelById(consoleChannelId).sendMessage("**__" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "__:** " + message)
                .queue();
    }

    private String getPcRank(int num) {
        String pcRank;

        if (num < 2500) {
            pcRank = "POOR";
        } else if (num >= 2500 && num < 5000) {
            pcRank = "OKAY";
        } else if (num >= 5000 && num < 10000) {
            pcRank = "GOOD";
        } else if (num >= 10000 && num < 15000) {
            pcRank = "SOLID";
        } else if (num > 15000) {
            pcRank = "*OVERKILL*";
        } else
            pcRank = "UNKNOWN";

        return pcRank;
    }

    private void canUserPlay(GuildMessageReceivedEvent event, String message) {
        boolean debugPrintouts = true;
        long deltaTime = System.currentTimeMillis();
        event.getChannel().sendMessage("`THIS FEATURE IS STILL IN BETA. RESPONSE MAY BE DELAYED AND SOME REQUIRED GAME SPECS PRESENTED MAYBE INCORRECT.`").queue();

        GameInfo game = new GameInfo(Searcher.getSearchResult(message));
        if (debugPrintouts) {
            System.out.println("GameInfo: " + game + " " + (System.currentTimeMillis() - deltaTime));
            deltaTime = System.currentTimeMillis();
        }
        UserSpecs user = Recordkeeper.getSpecsByUserId(event.getAuthor().getId());
        if (debugPrintouts) {
            System.out.println("UserSpecs: " + user + " " + (System.currentTimeMillis() - deltaTime));
            deltaTime = System.currentTimeMillis();
        }
        boolean[] specsMeetReqs = compareSpecs(game, user); //returns which of the users specs meet the requirements to play the game

        if (debugPrintouts) {
            System.out.println("SpecsMeetReqs: " + specsMeetReqs + " " + (System.currentTimeMillis() - deltaTime));
            deltaTime = System.currentTimeMillis();
        }
        boolean temp = true;
        for (boolean bool : specsMeetReqs) { if (!bool) { temp = false; } } //if all of specsMeetReqs = true, temp = true
        if (debugPrintouts) {
            System.out.println("All specs true: " + temp + " " + (System.currentTimeMillis() - deltaTime));
            deltaTime = System.currentTimeMillis();
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setImage(game.getImageUrl())
                .setColor((temp ? Color.GREEN : Color.RED))
                .setTitle((temp ? "Yes, you *can* play " + game.getTitle().trim() + "!" : "No, you *can't* play " + game.getTitle()), game.getWebsite())
                .setDescription("because...")
                .addField("CPU - Central Processing Unit", "Your CPU " + (specsMeetReqs[CPU_INDEX] ? "meets" : "**does not** meet") + " the minimum requirement (" + user.getUserCpu() + " vs. " + game.getCpu() + ")", false)
                .addField("GPU - Graphics Processing Unit", "Your GPU " + (specsMeetReqs[GPU_INDEX] ? "meets" : "**does not** meet") + " the minimum requirement (" + user.getUserGpu() + " vs. " + game.getGpu() + ")", false)
                .addField("RAM - Random Access Memory", "Your RAM " + (specsMeetReqs[RAM_INDEX] ? "meets" : "**does not** meet") + " the minimum requirement (" + user.getUserRam() + " GB vs. " + (game.getRamInGb() == -1 ? "<1" : game.getRamInGb()) + " GB)", false);

        if (debugPrintouts) {
            System.out.println("Message sent: " + (System.currentTimeMillis() - deltaTime));
        }
        event.getChannel().sendMessage(embed.build()).queue();
    }

    private boolean authorHasAdminPrivileges(GuildMessageReceivedEvent event) {
        //if the author is the guild owner, then he obviously has admin privilages
        if(event.getGuild().getOwnerId().equals(event.getAuthor().getId()))
            return true;

        return event.getGuild().getMember(event.getAuthor()).hasPermission(Permission.MANAGE_SERVER);

        /*
        ArrayList<Role> guildRoles = new ArrayList<Role>(); //an arrayList containing all roles in the guild
        guildRoles.addAll(event.getGuild().getRoles());

        //a loop to cycle through all the roles in guildRoles
        for (Role role : guildRoles) {
            if (!role.getPermissions().contains(Permission.MANAGE_SERVER)) //if a role doesn't have permissions to manage the server, skip it
                break;

            //get a list of all members who have that role on the server
            ArrayList<Member> memberList = new ArrayList<>();
            memberList.addAll(event.getGuild().getMembersWithRoles(role));

            //cycle through memberList to see if it contains the author of the event
            for(Member member : memberList) {
                if (member.getUser().getId().equals(event.getAuthor().getId())) { return true; }
            }

        }

        //if the code reaches this point, the author doesn't have admin privilages
        return false; //*/
    }

    private boolean[] compareSpecs(GameInfo gameInfo, UserSpecs user) {
        boolean[] output = new boolean[3];

        output[CPU_INDEX] = user.getUserCpu().isBetterThan(gameInfo.getCpu());
        output[GPU_INDEX] = user.getUserGpu().isBetterThan(gameInfo.getGpu());
        output[RAM_INDEX] = user.getUserRam() >= gameInfo.getRamInGb();

        //System.out.println(output[0] + ", " + output[1] + ", " + output[2]);

        return output;
    }

    private ArrayList<String> splitMessage(String inputStr) {
        ArrayList<String> output = new ArrayList<>();

        do { //so long as "inputStr" is too long to send
            output.add(inputStr.substring(0, inputStr.lastIndexOf("\n", 1999)));
            inputStr = inputStr.substring(inputStr.lastIndexOf("\n", 1999) + 1);
        } while (inputStr.contains("\n"));

        return output;
    }
}
