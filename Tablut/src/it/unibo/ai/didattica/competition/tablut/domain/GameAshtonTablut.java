package it.unibo.ai.didattica.competition.tablut.domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import it.unibo.ai.didattica.competition.tablut.exceptions.*;
import it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics.BlackHeuristics;
import it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics.Heuristics;
import it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics.WhiteHeuristics;

/**
 * 
 * Game engine inspired by the Ashton Rules of Tablut
 *
 * @author A. Piretti, Andrea Galassi (extended by Gionnino9000)
 */
public class GameAshtonTablut implements Game, Cloneable, aima.core.search.adversarial.Game<State, Action, State.Turn> {
	public static final int NUM_BLACK = 16;
	public static final int NUM_WHITE = 8;
	/**
	 * Number of repeated states that can occur before a draw
	 */
	private int repeated_moves_allowed;

	/**
	 * Number of states kept in memory. negative value means infinite.
	 */
	private int cache_size;
	/**
	 * Counter for the moves without capturing that have occurred
	 */

	private int movesWithutCapturing;
	private String gameLogName;
	private File gameLog;
	private FileHandler fh;
	private Logger loggGame;
	private List<String> citadels;
	// private List<String> strangeCitadels;
	private List<State> drawConditions;

	public GameAshtonTablut(int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName,
			String blackName) {
		this(new StateTablut(), repeated_moves_allowed, cache_size, logs_folder, whiteName, blackName);
	}

	public GameAshtonTablut(State state, int repeated_moves_allowed, int cache_size, String logs_folder,
			String whiteName, String blackName) {
		super();
		this.repeated_moves_allowed = repeated_moves_allowed;
		this.cache_size = cache_size;
		this.movesWithutCapturing = 0;

		Path p = Paths.get(logs_folder + File.separator + "_" + whiteName + "_vs_" + blackName + "_"
				+ new Date().getTime() + "_gameLog.txt");
		p = p.toAbsolutePath();
		this.gameLogName = p.toString();
		File gamefile = new File(this.gameLogName);
		try {
			File f = new File(logs_folder);
			f.mkdirs();
			if (!gamefile.exists()) {
				gamefile.createNewFile();
			}
			this.gameLog = gamefile;
			fh = null;
			fh = new FileHandler(gameLogName, true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.loggGame = Logger.getLogger("GameLog");
		loggGame.addHandler(this.fh);
		this.fh.setFormatter(new SimpleFormatter());
		loggGame.setLevel(Level.FINE);
		loggGame.fine("Players:\t" + whiteName + "\tvs\t" + blackName);
		loggGame.fine("Repeated moves allowed:\t" + repeated_moves_allowed + "\tCache:\t" + cache_size);
		loggGame.fine("Inizio partita");
		loggGame.fine("Stato:\n" + state.toString());
		drawConditions = new ArrayList<State>();
		this.citadels = new ArrayList<String>();
		// this.strangeCitadels = new ArrayList<String>();
		this.citadels.add("a4");
		this.citadels.add("a5");
		this.citadels.add("a6");
		this.citadels.add("b5");
		this.citadels.add("d1");
		this.citadels.add("e1");
		this.citadels.add("f1");
		this.citadels.add("e2");
		this.citadels.add("i4");
		this.citadels.add("i5");
		this.citadels.add("i6");
		this.citadels.add("h5");
		this.citadels.add("d9");
		this.citadels.add("e9");
		this.citadels.add("f9");
		this.citadels.add("e8");
		// this.strangeCitadels.add("e1");
		// this.strangeCitadels.add("a5");
		// this.strangeCitadels.add("i5");
		// this.strangeCitadels.add("e9");
	}

	/**
	 * Method to check wheter an action is allowed or not for a given state. If it is not, throws and Exception.
	 *
	 * @param state		Current state of the game
	 * @param action	The action to be checked
	 *
	 * @return			the resulting State obtained by performing the given
	 *
	 * @throws BoardException if the action causes a pawn to leave the board
	 * @throws ActionException if the action format is wrong
	 * @throws StopException if no action is made
	 * @throws PawnException if a player is trying to move an opponent's pawn
	 * @throws DiagonalException if a player is trying to move a pawn diagonally
	 * @throws ClimbingException if a player is trying to make an action that would climb another pawn or special box illegally
	 * @throws ThroneException if a player is trying to move a pawn to the throne
	 * @throws OccupiedException if a player is trying to move a pawn onto a box that is already occupied
	 * @throws ClimbingCitadelException if a player is trying to make an action that would climb the citadel
	 * @throws CitadelException if a player is trying to move a pawn onto the citadel
	 */
	@Override
	public State checkMove(State state, Action action)
			throws BoardException, ActionException, StopException, PawnException, DiagonalException, ClimbingException,
			ThroneException, OccupiedException, ClimbingCitadelException, CitadelException {
		// check isPossibleMove
		this.loggGame.fine(action.toString());
		// controllo la mossa
		if (action.getTo().length() != 2 || action.getFrom().length() != 2) {
			this.loggGame.warning("Formato mossa errato");
			throw new ActionException(action);
		}
		int columnFrom = action.getColumnFrom();
		int columnTo = action.getColumnTo();
		int rowFrom = action.getRowFrom();
		int rowTo = action.getRowTo();

		// controllo se sono fuori dal tabellone
		if (columnFrom > state.getBoard().length - 1 || rowFrom > state.getBoard().length - 1
				|| rowTo > state.getBoard().length - 1 || columnTo > state.getBoard().length - 1 || columnFrom < 0
				|| rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			this.loggGame.warning("Mossa fuori tabellone");
			throw new BoardException(action);
		}

		// controllo che non vada sul trono
		if (state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString())) {
			this.loggGame.warning("Mossa sul trono");
			throw new ThroneException(action);
		}

		// controllo la casella di arrivo
		if (!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString())) {
			this.loggGame.warning("Mossa sopra una casella occupata");
			throw new OccupiedException(action);
		}
		if (this.citadels.contains(state.getBox(rowTo, columnTo))
				&& !this.citadels.contains(state.getBox(rowFrom, columnFrom))) {
			this.loggGame.warning("Mossa che arriva sopra una citadel");
			throw new CitadelException(action);
		}
		if (this.citadels.contains(state.getBox(rowTo, columnTo))
				&& this.citadels.contains(state.getBox(rowFrom, columnFrom))) {
			if (rowFrom == rowTo) {
				if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(action);
				}
			} else {
				if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(action);
				}
			}

		}

		// controllo se cerco di stare fermo
		if (rowFrom == rowTo && columnFrom == columnTo) {
			this.loggGame.warning("Nessuna mossa");
			throw new StopException(action);
		}

		// controllo se sto muovendo una pedina giusta
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("W")
					&& !state.getPawn(rowFrom, columnFrom).equalsPawn("K")) {
				this.loggGame.warning("Giocatore " + action.getTurn() + " cerca di muovere una pedina avversaria");
				throw new PawnException(action);
			}
		}
		if (state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("B")) {
				this.loggGame.warning("Giocatore " + action.getTurn() + " cerca di muovere una pedina avversaria");
				throw new PawnException(action);
			}
		}

		// controllo di non muovere in diagonale
		if (rowFrom != rowTo && columnFrom != columnTo) {
			this.loggGame.warning("Mossa in diagonale");
			throw new DiagonalException(action);
		}

		// controllo di non scavalcare pedine
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(action);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(action);
						}
					}
					if (this.citadels.contains(state.getBox(rowFrom, i))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(action);
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(action);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(action);
						}
					}
					if (this.citadels.contains(state.getBox(rowFrom, i))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(action);
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(action);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(action);
						}
					}
					if (this.citadels.contains(state.getBox(i, columnFrom))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(action);
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(action);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(action);
						}
					}
					if (this.citadels.contains(state.getBox(i, columnFrom))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(action);
					}
				}
			}
		}

		// se sono arrivato qui, muovo la pedina
		state = this.movePawn(state, action);

		// a questo punto controllo lo stato per eventuali catture
		if (state.getTurn().equalsTurn("W")) {
			state = this.checkCaptureBlack(state, action);
		} else if (state.getTurn().equalsTurn("B")) {
			state = this.checkCaptureWhite(state, action);
		}

		// if something has been captured, clear cache for draws
		if (this.movesWithutCapturing == 0) {
			this.drawConditions.clear();
			this.loggGame.fine("Capture! Draw cache cleared!");
		}

		// controllo pareggio
		int trovati = 0;
		for (State s : drawConditions) {

			System.out.println(s.toString());

			if (s.equals(state)) {
				// DEBUG: //
				// System.out.println("UGUALI:");
				// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
				// System.out.println("STATO NUOVO:\t" +
				// state.toLinearString());

				trovati++;
				if (trovati > repeated_moves_allowed) {
					state.setTurn(State.Turn.DRAW);
					this.loggGame.fine("Partita terminata in pareggio per numero di stati ripetuti");
					break;
				}
			} else {
				// DEBUG: //
				// System.out.println("DIVERSI:");
				// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
				// System.out.println("STATO NUOVO:\t" +
				// state.toLinearString());
			}
		}
		if (trovati > 0) {
			this.loggGame.fine("Equal states found: " + trovati);
		}
		if (cache_size >= 0 && this.drawConditions.size() > cache_size) {
			this.drawConditions.remove(0);
		}
		this.drawConditions.add(state.clone());

		this.loggGame.fine("Current draw cache size: " + this.drawConditions.size());

		this.loggGame.fine("Stato:\n" + state.toString());
		System.out.println("Stato:\n" + state.toString());

		return state;
	}

	private State checkCaptureWhite(State state, Action a) {
		// controllo se mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K")
						|| (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))
								&& !(a.getColumnTo() + 2 == 8 && a.getRowTo() == 4)
								&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 0)
								&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 8)
								&& !(a.getColumnTo() + 2 == 0 && a.getRowTo() == 4)))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			this.movesWithutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
		}
		// controllo se mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K")
						|| (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
								&& !(a.getColumnTo() - 2 == 8 && a.getRowTo() == 4)
								&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 0)
								&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 8)
								&& !(a.getColumnTo() - 2 == 0 && a.getRowTo() == 4)))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			this.movesWithutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
		}
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K")
						|| (this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
								&& !(a.getColumnTo() == 8 && a.getRowTo() - 2 == 4)
								&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 0)
								&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 8)
								&& !(a.getColumnTo() == 0 && a.getRowTo() - 2 == 4)))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
			this.movesWithutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
		}
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K")
						|| (this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
								&& !(a.getColumnTo() == 8 && a.getRowTo() + 2 == 4)
								&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 0)
								&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 8)
								&& !(a.getColumnTo() == 0 && a.getRowTo() + 2 == 4)))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
			this.movesWithutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
		}
		// controllo se ho vinto
		if (a.getRowTo() == 0 || a.getRowTo() == state.getBoard().length - 1 || a.getColumnTo() == 0
				|| a.getColumnTo() == state.getBoard().length - 1) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
				state.setTurn(State.Turn.WHITEWIN);
				this.loggGame.fine("Bianco vince con re in " + a.getTo());
			}
		}
		// TODO: implement the winning condition of the capture of the last
		// black checker

		this.movesWithutCapturing++;
		return state;
	}

	private State checkCaptureBlackKingLeft(State state, Action a) {
		// ho il re sulla sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K")) {
			//System.out.println("Ho il re sulla sinistra");
			// re sul trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")) {
				if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
				if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")) {
				if (state.getPawn(6, 4).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
				if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
		}
		return state;
	}

	private State checkCaptureBlackKingRight(State state, Action a) {
		// ho il re sulla destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K"))) {
			//System.out.println("Ho il re sulla destra");
			// re sul trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")) {
				if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")) {
				if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(6, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")) {
				if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
				if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
						|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
		}
		return state;
	}

	private State checkCaptureBlackKingDown(State state, Action a) {
		// ho il re sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K")) {
			//System.out.println("Ho il re sotto");
			// re sul trono
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")) {
				if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
				}
			}
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")) {
				if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
				}
			}
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")) {
				if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
				}
			}
		}
		return state;
	}

	private State checkCaptureBlackKingUp(State state, Action a) {
		// ho il re sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K")) {
			//System.out.println("Ho il re sopra");
			// re sul trono
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e6")) {
				if (state.getPawn(5, 3).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
				}
			}
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")) {
				if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
				}
			}
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")) {
				if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e6")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))) {
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame
							.fine("Nero vince con re catturato in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
				}
			}
		}
		return state;
	}

	private State checkCaptureBlackPawnRight(State state, Action a) {
		// mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}
			if (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}

		}

		return state;
	}

	private State checkCaptureBlackPawnLeft(State state, Action a) {
		// mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
						|| (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5")))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			this.movesWithutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
		}
		return state;
	}

	private State checkCaptureBlackPawnUp(State state, Action a) {
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
			this.movesWithutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
		}
		return state;
	}

	private State checkCaptureBlackPawnDown(State state, Action a) {
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
			this.movesWithutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
		}
		return state;
	}

	private State checkCaptureBlack(State state, Action a) {

		this.checkCaptureBlackPawnRight(state, a);
		this.checkCaptureBlackPawnLeft(state, a);
		this.checkCaptureBlackPawnUp(state, a);
		this.checkCaptureBlackPawnDown(state, a);
		this.checkCaptureBlackKingRight(state, a);
		this.checkCaptureBlackKingLeft(state, a);
		this.checkCaptureBlackKingDown(state, a);
		this.checkCaptureBlackKingUp(state, a);

		this.movesWithutCapturing++;
		return state;
	}

	private State movePawn(State state, Action a) {
		State.Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
		State.Pawn[][] newBoard = state.getBoard();
		// State newState = new State();
		this.loggGame.fine("Movimento pedina");
		// libero il trono o una casella qualunque
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.THRONE;
		} else {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.EMPTY;
		}

		// metto nel nuovo tabellone la pedina mossa
		newBoard[a.getRowTo()][a.getColumnTo()] = pawn;
		// aggiorno il tabellone
		state.setBoard(newBoard);
		// cambio il turno
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			state.setTurn(State.Turn.BLACK);
		} else {
			state.setTurn(State.Turn.WHITE);
		}

		return state;
	}

	public File getGameLog() {
		return gameLog;
	}

	public int getMovesWithutCapturing() {
		return movesWithutCapturing;
	}

	@SuppressWarnings("unused")
	private void setMovesWithutCapturing(int movesWithutCapturing) {
		this.movesWithutCapturing = movesWithutCapturing;
	}

	public int getRepeated_moves_allowed() {
		return repeated_moves_allowed;
	}

	public int getCache_size() {
		return cache_size;
	}

	public List<State> getDrawConditions() {
		return drawConditions;
	}

	public void clearDrawConditions() {
		drawConditions.clear();
	}
	

	@Override
	public void endGame(State state) {
		this.loggGame.fine("Stato:\n"+state.toString());
	}

	/**
	 * Auxiliary method used to check wheter an action is allowed or not for a given state.
	 *
	 * @param state		Current state of the game
	 * @param action	The action to be checked
	 *
	 * @return			true if the action is allowed, false otherwise.
	 */
	public boolean isPossibleMove(State state, Action action)
	{
		this.loggGame.fine(action.toString());
		// controllo la mossa
		if (action.getTo().length() != 2 || action.getFrom().length() != 2) {
			return false;
		}
		int columnFrom = action.getColumnFrom();
		int columnTo = action.getColumnTo();
		int rowFrom = action.getRowFrom();
		int rowTo = action.getRowTo();

		// controllo se sono fuori dal tabellone
		if (columnFrom > state.getBoard().length - 1 || rowFrom > state.getBoard().length - 1
				|| rowTo > state.getBoard().length - 1 || columnTo > state.getBoard().length - 1 || columnFrom < 0
				|| rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			return false;
		}

		// controllo che non vada sul trono
		if (state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString())) {
			return false;
		}

		// controllo la casella di arrivo
		if (!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString())) {
			return false;
		}
		if (this.citadels.contains(state.getBox(rowTo, columnTo))
				&& !this.citadels.contains(state.getBox(rowFrom, columnFrom))) {
			return false;
		}
		if (this.citadels.contains(state.getBox(rowTo, columnTo))
				&& this.citadels.contains(state.getBox(rowFrom, columnFrom))) {
			if (rowFrom == rowTo) {
				if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
					return false;
				}
			} else {
				if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
					return false;
				}
			}
		}

		// controllo se cerco di stare fermo
		if (rowFrom == rowTo && columnFrom == columnTo) {
			return false;
		}

		// controllo se sto muovendo una pedina giusta
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("W")
					&& !state.getPawn(rowFrom, columnFrom).equalsPawn("K")) {
				return false;
			}
		}
		if (state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("B")) {
				return false;
			}
		}

		// controllo di non muovere in diagonale
		if (rowFrom != rowTo && columnFrom != columnTo) {
			return false;
		}

		// controllo di non scavalcare pedine
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (this.citadels.contains(state.getBox(rowFrom, i))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						return false;
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (this.citadels.contains(state.getBox(rowFrom, i))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						return false;
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (this.citadels.contains(state.getBox(i, columnFrom))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						return false;
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (this.citadels.contains(state.getBox(i, columnFrom))
							&& !this.citadels.contains(state.getBox(action.getRowFrom(), action.getColumnFrom()))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Cloneable Interface methods
	@Override
	public GameAshtonTablut clone()
	{
		try
		{
			GameAshtonTablut clone = (GameAshtonTablut) super.clone();
			clone.repeated_moves_allowed = this.repeated_moves_allowed;
			clone.cache_size = this.cache_size;
			clone.movesWithutCapturing = this.movesWithutCapturing;
			clone.gameLogName = this.gameLogName;
			clone.gameLog = this.gameLog;
			clone.fh = this.fh;
			clone.loggGame = this.loggGame;
			clone.citadels = this.citadels;
			clone.drawConditions = this.drawConditions;

			return clone;
		} catch (CloneNotSupportedException e)
		{
			throw new AssertionError();
		}
	}

	// aima.core.search.adversarial.Game Interface methods
	/**
	 * Get the player who must make the next move
	 *
	 * @param state Current state of the game
	 *
	 * @return The turn of the game (W = WHITE, B = BLACK)
	 */
	@Override
	public State.Turn getPlayer(State state)
	{
		return state.getTurn();
	}

	/**
	 * Method that compute a list of all possible actions for the current player according to the rules of the game
	 *
	 * @param state Current state of the game
	 *
	 * @return List of all the Action allowed from current state for each pawn of the player
	 */
	@Override
	public List<Action> getActions(State state)
	{
		State.Turn turn = state.getTurn();
		List<Action> possibleActions = new ArrayList<Action>();

		// Loop through rows
		for(int i = 0; i < state.getBoard().length; i++)
		{
			// Loop through columns
			for(int j = 0; j < state.getBoard().length; j++)
			{
				State.Pawn p = state.getPawn(i, j);

				// The pawn in this box belongs to the player that must make the next move
				if(p.toString().equals(turn.toString()) ||
						(p.equals(State.Pawn.KING) && turn.toString().equals(State.Turn.WHITE)))
				{
					// Search on top of pawn (#row - 1)
					for(int k = i - 1; k >= 0; k--)
					{
						// Break if pawn is out of citadels, and it's moving on a citadel
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j)))
						{
							break;
						}
						// Check if we are moving on an empty cell
						else if (state.getPawn(k, j).equalsPawn(State.Pawn.EMPTY.toString()))
						{
							String from = state.getBox(i, j);
							String to = state.getBox(k, j);

							Action action = null;

							try {
								action = new Action(from, to, turn);
								// If the action is allowed, add it to the list
								if (isPossibleMove(state.clone(), action))
									possibleActions.add(action);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						// There is a pawn in the same column, and it cannot be crossed
						else
						{
							break;
						}
					}

					// Search on bottom of pawn (#row + 1)
					for (int k = i + 1; k < state.getBoard().length; k++)
					{
						// Break if pawn is out of citadels, and it is moving on a citadel
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j)))
						{
							break;
						}
						// Check if we are moving on an empty cell
						else if (state.getPawn(k, j).equalsPawn(State.Pawn.EMPTY.toString()))
						{
							String from = state.getBox(i, j);
							String to = state.getBox(k, j);

							Action action = null;
							try {
								action = new Action(from, to, turn);
								// If the action is allowed, add it to the list
								if (isPossibleMove(state.clone(), action))
									possibleActions.add(action);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						// There is a pawn in the same column, and it cannot be crossed
						else
						{
							break;
						}
					}

					// Search on left of pawn (#column - 1)
					for (int k = j - 1; k >= 0; k--)
					{
						// Break if pawn is out of citadels, and it is moving on a citadel
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k)))
						{
							break;
						}

						// Check if we are moving on an empty cell
						else if (state.getPawn(i, k).equalsPawn(State.Pawn.EMPTY.toString().toString()))
						{
							String from = state.getBox(i, j);
							String to = state.getBox(i, k);

							Action action = null;
							try {
								action = new Action(from, to, turn);
								// If the action is allowed, add it to the list
								if (isPossibleMove(state.clone(), action))
									possibleActions.add(action);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						// There is a pawn in the same column, and it cannot be crossed
						else
						{
							break;
						}
					}

					// Search on right of pawn (#column + 1)
					for (int k = j + 1; k < state.getBoard().length; k++)
					{
						// Break if pawn is out of citadels, and it is moving on a citadel
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k)))
						{
							break;
						}
						// Check if we are moving on an empty cell
						else if (state.getPawn(i, k).equalsPawn(State.Pawn.EMPTY.toString()))
						{
							String from = state.getBox(i, j);
							String to = state.getBox(i, k);

							Action action = null;
							try {
								action = new Action(from, to, turn);
								// If the action is allowed, add it to the list
								if (isPossibleMove(state.clone(), action))
									possibleActions.add(action);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						// There is a pawn in the same column, and it cannot be crossed
						else
						{
							break;
						}
					}
				}
			}
		}
		return possibleActions;
	}

	/**
	 * Method that performs an action in a given state and returns the resulting state
	 *
	 * @param state Current state
	 * @param action Action admissible on the given state
	 *
	 * @return State obtained after performing the action
	 */
	@Override
	public State getResult(State state, Action action)
	{
		// Move pawn
		state = this.movePawn(state.clone(), action);

		// Check the state for any capture
		if (state.getTurn().equalsTurn("W"))
		{
			state = this.checkCaptureBlack(state, action);
		}
		else if (state.getTurn().equalsTurn("B"))
		{
			state = this.checkCaptureWhite(state, action);
		}


		/*
		// If something has been captured, clear cache for draws
		if (this.movesWithutCapturing == 0)
		{
			this.drawConditions.clear();
			this.loggGame.fine("Capture! Draw cache cleared!");
		}

		// Check draw
		int found = 0;
		for (State s : drawConditions)
		{
			if (s.equals(state))
			{

				found++;
				if (found > repeated_moves_allowed)
				{
					state.setTurn(State.Turn.DRAW);
					this.loggGame.fine("Partita terminata in pareggio per numero di stati ripetuti");
					break;
				}
			}
			else
			{
				// DEBUG: //
				// System.out.println("DIVERSI:");
				// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
				// System.out.println("STATO NUOVO:\t" +
				// state.toLinearString());
			}
		}
		if (found > 0)
		{
			this.loggGame.fine("Equal states found: " + found);
		}
		if (cache_size >= 0 && this.drawConditions.size() > cache_size)
		{
			this.drawConditions.remove(0);
		}
		this.drawConditions.add(state.clone());

		this.loggGame.fine("Current draw cache size: " + this.drawConditions.size());

		this.loggGame.fine("Stato:\n" + state.toString());
		//System.out.println("Stato:\n" + state.toString());
		*/

		return state;
	}

	/**
	 * Check if a state is terminal, since a player has either won or drawn (i.e. the game ends)
	 *
	 * @param state Current state of the game
	 *
	 * @return true if the current state is terminal, otherwise false
	 */
	@Override
	public boolean isTerminal(State state)
	{
		return state.getTurn().equals(State.Turn.WHITEWIN) ||
				state.getTurn().equals(State.Turn.BLACKWIN) ||
				state.getTurn().equals(State.Turn.DRAW);
	}

	/**
	 * Method to evaluate a state using heuristics
	 *
	 * @param state	Current state
	 * @param turn	Player that want to find the best moves in the search space
	 *
	 * @return Evaluation of the state
	 */
	@Override
	public double getUtility(State state, State.Turn turn)
	{
		// Terminal state
		if ((turn.equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.BLACKWIN))
				|| (turn.equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.WHITEWIN)))
			return Double.POSITIVE_INFINITY; // Win
		else if ((turn.equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.WHITEWIN))
				|| (turn.equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.BLACKWIN)))
			return Double.NEGATIVE_INFINITY; // Lose

		// Non-terminal state => get Heuristics for the current state
		Heuristics heuristics = turn.equals(State.Turn.WHITE) ? new WhiteHeuristics(state) : new BlackHeuristics(state);
		return heuristics.evaluateState();
	}

	/* Not used in AlphaBetaSearch */
	@Override
	public State getInitialState()
	{
		return null;
	}

	/* Not used in AlphaBetaSearch*/
	@Override
	public State.Turn[] getPlayers()
	{
		return State.Turn.values();
	}
}
