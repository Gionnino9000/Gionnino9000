package it.unibo.ai.didattica.competition.tablut.gionnino9000.clients;

import java.io.IOException;
import java.net.UnknownHostException;

public class WhiteTavoletta {

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
        String[] arguments = new String[]{"WHITE", "60", "localhost", "debug"};

        if(args.length > 0) {
            arguments = new String[]{"WHITE", args[0]};
        }

        Tavoletta.main(arguments);
    }
}
