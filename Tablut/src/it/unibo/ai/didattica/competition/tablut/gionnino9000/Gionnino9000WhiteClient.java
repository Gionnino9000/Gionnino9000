package it.unibo.ai.didattica.competition.tablut.gionnino9000;

import java.io.IOException;
import java.net.UnknownHostException;

public class Gionnino9000WhiteClient {

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
        String[] arguments = new String[]{"WHITE", "60", "localhost", "debug"};

        if(args.length > 0) {
            arguments = new String[]{"WHITE", args[0]};
        }

        Gionnino9000Client.main(arguments);
    }
}
