package it.unibo.ai.didattica.competition.tablut.gionnino9000;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;

import java.io.IOException;
import java.net.UnknownHostException;

public class Gionnino9000Client extends TablutClient {

    private boolean debug;
    private int game;

    public Gionnino9000Client(String player, String name, int timeout, String ipAddress, int game, boolean debug) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.debug = debug;
        this.game = game;
    }

    public static void main(String[] args) throws IOException {
        int gameType = 4;
        String role = "";
        String name = "Gionnino9000";
        String ip = "localhost";
        int timeout = 60;
        boolean deb = false;

        if(args.length < 1) {
            System.out.println("[Gionnino9000] | You must specify which player you are [WHITE | BLACK]");
            System.out.println("[Gionnino9000] | USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
            System.exit(-1);
        } else {
            role = (args[0]);
        }

        if(args.length == 2) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("[Gionnino9000] | Timeout must be an integer representing seconds");
                System.out.println("[Gionnino9000] | USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }
        }

        if(args.length == 3) {
            try {
                timeout = Integer.parseInt(args[1]);
                ip = args[2];
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("[Gionnino9000] | Timeout must be an integer representing seconds");
                System.out.println("[Gionnino9000] | USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }
        }

        if(args.length == 4) {
            try {
                timeout = Integer.parseInt(args[1]);
                ip = args[2];
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("[Gionnino9000] | Timeout must be an integer representing seconds");
                System.out.println("[Gionnino9000] | USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }

            if(args[3].equals("debug")) {
                deb = true;
            } else {
                System.out.println("[Gionnino9000] | The last argument can be only 'debug' and it allow to print logs during search");
                System.out.println("[Gionnino9000] | USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }
        }

        Gionnino9000Client client = new Gionnino9000Client(role, name, timeout, ip, gameType, deb);
        // GO GIONNINO9000, DO THE MAGIC AND SHINE
        client.run();
    }

    @Override
    public void run() {
        System.out.println(
                " ░██████╗░██╗░█████╗░███╗░░██╗███╗░░██╗██╗███╗░░██╗░█████╗░░█████╗░░█████╗░░█████╗░░█████╗░ " +
                " ██╔════╝░██║██╔══██╗████╗░██║████╗░██║██║████╗░██║██╔══██╗██╔══██╗██╔══██╗██╔══██╗██╔══██╗ " +
                " ██║░░██╗░██║██║░░██║██╔██╗██║██╔██╗██║██║██╔██╗██║██║░░██║╚██████║██║░░██║██║░░██║██║░░██║ " +
                " ██║░░╚██╗██║██║░░██║██║╚████║██║╚████║██║██║╚████║██║░░██║░╚═══██║██║░░██║██║░░██║██║░░██║ " +
                " ╚██████╔╝██║╚█████╔╝██║░╚███║██║░╚███║██║██║░╚███║╚█████╔╝░█████╔╝╚█████╔╝╚█████╔╝╚█████╔╝ " +
                " ░╚═════╝░╚═╝░╚════╝░╚═╝░░╚══╝╚═╝░░╚══╝╚═╝╚═╝░░╚══╝░╚════╝░░╚════╝░░╚════╝░░╚════╝░░╚════╝░ " );
    }
}
