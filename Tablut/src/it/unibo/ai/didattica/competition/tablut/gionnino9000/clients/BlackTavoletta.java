package it.unibo.ai.didattica.competition.tablut.gionnino9000.clients;

import java.io.IOException;
import java.net.UnknownHostException;

public class BlackTavoletta {

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
        String[] arguments = new String[]{"BLACK", "10", "localhost", "debug"};

        if(args.length > 0) {
            arguments = new String[]{"BLACK", args[0]};
        }

        Tavoletta.main(arguments);
    }
}
