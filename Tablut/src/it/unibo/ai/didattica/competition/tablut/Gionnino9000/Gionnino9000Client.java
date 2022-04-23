package it.unibo.ai.didattica.competition.tablut.Gionnino9000;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;

import java.io.IOException;
import java.net.UnknownHostException;

public class Gionnino9000Client extends TablutClient {


    public Gionnino9000Client(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
    }

    public Gionnino9000Client(String player, String name, int timeout) throws UnknownHostException, IOException {
        super(player, name, timeout);
    }

    public Gionnino9000Client(String player, String name) throws UnknownHostException, IOException {
        super(player, name);
    }

    public Gionnino9000Client(String player, String name, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, ipAddress);
    }

    @Override
    public void run() {

    }
}
