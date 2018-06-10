package tech.skycraft.indianajaune;

import discord4j.core.ClientBuilder;
import discord4j.core.DiscordClient;


public class MainRunner {

    public static void main(String[] args){

        if(args.length != 1){
            System.out.println("Please enter the bots token as the first argument e.g java -jar thisjar.jar tokenhere");
            return;
        }

        new  ClientBuilder(args[0]);



        // Register a listener via the EventSubscriber annotation which allows for organisation and delegation of events
        cli.getDispatcher().on(new CommandHandler());

        // Only login after all events are registered otherwise some may be missed.
        cli.login();
        cli.online("/help");

    }

}
