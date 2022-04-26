package it.unibo.ai.didattica.competition.tablut.gionnino9000.minmax;

import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class OurIterativeDeepeningSearch extends IterativeDeepeningAlphaBetaSearch<State, Action, State.Turn> {

    public OurIterativeDeepeningSearch(Game<State, Action, State.Turn> game, double utilMin, double utilMax, int time) {
        super(game, utilMin, utilMax, time);
    }

    @Override
    protected double eval(State state, State.Turn player) {

    }
}
