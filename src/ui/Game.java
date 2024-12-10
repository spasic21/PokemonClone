package ui;

import battle.BattleManager;
import framework.ObjectId;
import keyInput.GameKeyInput;
import keyInput.KeyInput;
import objects.*;
import screen.BattleScreen;
import framework.GameState;
import framework.PokemonGenerator;
import framework.PokemonTeamBuilder;
import keyInput.BattleKeyInput;
import screen.GameScreen;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    private GameScreen gameScreen;
    private BattleScreen battleScreen;
    private BattleManager battleManager;
    private boolean running = false;
    private Thread thread;

    private static boolean battleStarted = false;

    public static GameObject player = new Player(500, 400, 72, 72, ObjectId.Player);

    public static int WIDTH, HEIGHT;

    public static GameState gameState = GameState.Game;

    private PokemonTeamBuilder builder = new PokemonTeamBuilder();

    private void init(){
        WIDTH = this.getWidth();
        HEIGHT = this.getHeight();
        this.gameScreen = new GameScreen(WIDTH, HEIGHT, (Player) player);
        this.battleManager = BattleManager.getInstance();
        this.battleScreen = new BattleScreen(WIDTH, HEIGHT, this.battleManager);
        this.addKeyListener(new GameKeyInput((Player) player, this.battleManager));
    }

    public synchronized void start(){
        if(running){
            return;
        }

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop(){}

    @Override
    public void run() {
        init();
        this.requestFocus();

        long lastTime = System.nanoTime();
        double fps = 60.0;
        double drawInterval = 1000000000 / fps;
        double delta = 0;
		int updates = 0;
		long timer = 0;
		int frames = 0;

        while(running){
            long currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            while(delta >= 1){
                update();
                render();
//				updates++;
                delta--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
                updates = 0;
            }

//			if(timer >= 1000000000){
//				timer = 0;
//				updates = 0;
//			}
        }
    }

    private void update(){
        if(gameState == GameState.Battle){
            if(!battleStarted) {
//                this.battleManager = BattleManager.getInstance();
                this.battleManager.init(getPlayerParty(), builder.createPokemonTeam());
//                this.battleScreen = new BattleScreen(WIDTH, HEIGHT, this.battleManager);
                battleStarted = true;
            }

            battleScreen.update();
        } else {
            gameScreen.update();
        }
    }

    private void render(){
        BufferStrategy bs = this.getBufferStrategy();

        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        if(gameState == GameState.Battle) {
            if(battleStarted) {
                battleScreen.render(g);
            }

        }else {
            gameScreen.render(g);
        }

        g.dispose();
        bs.show();
    }

    public GameState getGameState() {
        return gameState;
    }

    public static void setGameState(GameState gameState) {
        Game.gameState = gameState;
    }

    private List<Pokemon> getPlayerParty() {
        List<Pokemon> playerParty = new ArrayList<>();
        List<PokemonMove> pokemonMoves = new ArrayList<>();
        Pokemon pokemon = new PokemonGenerator().createMyPokemon("Mewtwo");

        pokemonMoves.add(new PokemonMove("Flamethrower", Type.Fire, 90, 100, 15, 15, PokemonMove.MoveCategory.Special));
//        pokemonMoves.add(new PokemonMove("Fly", Type.Flying, 80, 95, 10, 10, PokemonMove.MoveCategory.Physical));
        pokemonMoves.add(new PokemonMove("Dragon Claw", Type.Dragon, 80, 100, 9, 15, PokemonMove.MoveCategory.Physical));
        pokemonMoves.add(new PokemonMove("Dragon Dance", Type.Dragon, 0, 100, 10, 10, PokemonMove.MoveCategory.Status));

        pokemon.setPokemonMovesList(pokemonMoves);

        playerParty.add(pokemon);

        return playerParty;
    }

    public static boolean isBattleStarted() {
        return battleStarted;
    }

    public static void setBattleStarted(boolean battleStarted) {
        Game.battleStarted = battleStarted;
    }
}
