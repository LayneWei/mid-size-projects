package byow.Core;

/**
 * State of the game:
 * MAIN_MENU: state in which the main menu is displayed.
 * SEED_ENTERING: state in which the user is prompted to enter the
 *                seed used to generate the game world.
 * GAME: state in which the user plays the game.
 * GAME_COLON: the game enters this state only when the user presses
 *             the ':' key to enter subsequent commands such as 'Q'
 *             to quit the game.
 * REPLAY_MODE: replaying last auto save.
 * TERMINATED: game has been terminated by the user.
 *
 * @author Robert Shi, Layne Wei
 * @e-mail robertyishi@berkeley.edu, lengning_wei@berkeley.edu
 */
public enum GameState {
    MAIN_MENU, SEED_ENTERING, NAME_ENTERING, GAME, GAME_COLON, REPLAY_MODE, TERMINATED
}
