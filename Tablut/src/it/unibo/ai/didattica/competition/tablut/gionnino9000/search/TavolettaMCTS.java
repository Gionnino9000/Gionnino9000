package it.unibo.ai.didattica.competition.tablut.gionnino9000.search;

import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.MonteCarloTreeSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class TavolettaMCTS extends MonteCarloTreeSearch<State, Action, State.Turn> {

    public TavolettaMCTS(Game<State, Action, State.Turn> game, int iterations) {
        super(game, iterations);
    }
}
