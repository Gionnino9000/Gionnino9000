package it.unibo.ai.didattica.competition.tablut.gionnino9000;

import java.io.IOException;
import java.net.UnknownHostException;

public class Gionnino9000BlackClient {

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
        String[] arguments = new String[]{"BLACK", "60", "localhost", "debug"};

        if(args.length > 0) {
            arguments = new String[]{"BLACK", args[0]};
        }

        Gionnino9000Client.main(arguments);
    }
}
