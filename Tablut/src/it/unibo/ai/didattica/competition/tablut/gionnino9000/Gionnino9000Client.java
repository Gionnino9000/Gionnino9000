package it.unibo.ai.didattica.competition.tablut.gionnino9000;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

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
        GOGionninoDoTheMagicAndShine();

        // send marvelous name to the server
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state = new StateTablut();
        state.setTurn(State.Turn.WHITE);
        GameAshtonTablut tablut = new GameAshtonTablut(0, -1, "logs", "white_ai", "black_ai");

        while(true) {
            // update the current state from the server
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("[GIONNINO9000] | Current state: ");
            state = this.getCurrentState();
            System.out.println(state.toString());

            // if WHITE turn
            if(this.getPlayer().equals(State.Turn.WHITE)) {

                if(state.getTurn().equals(State.Turn.WHITE)) {

                    System.out.println("[GIONNINO9000] | Searching a suitable move...");

                    Action a = findBestMove(tablut, state);

                    System.out.println("[GIONNINO9000] | Action selected: " + a.toString());

                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }

                } else if(state.getTurn().equals(State.Turn.BLACK)) {
                    // Opponent TURN
                    System.out.println("[GIONNINO9000] | Waiting for the opponent turn...");
                } else if(state.getTurn().equals(State.Turn.WHITEWIN)) {
                    // if WHITE win
                    System.out.println("[GIONNINO9000] | YOU WIN");
                    System.exit(0);
                } else if(state.getTurn().equals(State.Turn.BLACKWIN)) {
                    // if BLACK win
                    System.out.println("[GIONNINO9000] | YOU LOSE");
                    System.exit(0);
                } else if(state.getTurn().equals(State.Turn.DRAW)) {
                    System.out.println("[GIONNINO9000] | DRAW");
                    System.exit(0);
                }
            }
            // if BLACK turn
            else {

                if(state.getTurn().equals(State.Turn.BLACK)) {

                    System.out.println("[GIONNINO9000] | Searching a suitable move...");

                    Action a = findBestMove(tablut, state);

                    System.out.println("[GIONNINO9000] | Action selected: " + a.toString());

                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }

                } else if(state.getTurn().equals(State.Turn.WHITE)) {
                    // Opponent TURN
                    System.out.println("[GIONNINO9000] | Waiting for the opponent turn...");
                } else if(state.getTurn().equals(State.Turn.BLACKWIN)) {
                    // if WHITE win
                    System.out.println("[GIONNINO9000] | YOU WIN");
                    System.exit(0);
                } else if(state.getTurn().equals(State.Turn.WHITEWIN)) {
                    // if BLACK win
                    System.out.println("[GIONNINO9000] | YOU LOSE");
                    System.exit(0);
                } else if(state.getTurn().equals(State.Turn.DRAW)) {
                    System.out.println("[GIONNINO9000] | DRAW");
                    System.exit(0);
                }

            }
        }
    }

    private Action findBestMove(GameAshtonTablut tablutGame, State state) {
        // le cose belle
        return null;
    }

    private void GOGionninoDoTheMagicAndShine() {
        System.out.println(
                        " ░██████╗░██╗░█████╗░███╗░░██╗███╗░░██╗██╗███╗░░██╗░█████╗░░█████╗░░█████╗░░█████╗░░█████╗░ " +
                        " ██╔════╝░██║██╔══██╗████╗░██║████╗░██║██║████╗░██║██╔══██╗██╔══██╗██╔══██╗██╔══██╗██╔══██╗ " +
                        " ██║░░██╗░██║██║░░██║██╔██╗██║██╔██╗██║██║██╔██╗██║██║░░██║╚██████║██║░░██║██║░░██║██║░░██║ " +
                        " ██║░░╚██╗██║██║░░██║██║╚████║██║╚████║██║██║╚████║██║░░██║░╚═══██║██║░░██║██║░░██║██║░░██║ " +
                        " ╚██████╔╝██║╚█████╔╝██║░╚███║██║░╚███║██║██║░╚███║╚█████╔╝░█████╔╝╚█████╔╝╚█████╔╝╚█████╔╝ " +
                        " ░╚═════╝░╚═╝░╚════╝░╚═╝░░╚══╝╚═╝░░╚══╝╚═╝╚═╝░░╚══╝░╚════╝░░╚════╝░░╚════╝░░╚════╝░░╚════╝░ "
        );
    }
}
