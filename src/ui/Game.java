package ui;

import battle.BattleManager;
import framework.Handler;
import framework.PokemonGenerator;
import framework.SoundManager;
import framework.enums.GameState;
import keyInput.GameKeyInput;
import objects.Pokemon;
import screen.BattleScreen;
import screen.GameScreen;
import screen.PokemonMenuScreen;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable {

    private Window window;

    private final String title;
    private final int width;
    private final int height;

    private Handler handler;

    private GameKeyInput gameKeyInput;

    private GameScreen gameScreen;
    private BattleScreen battleScreen;
    private PokemonMenuScreen pokemonMenuScreen;
    private BattleManager battleManager;
    private boolean running = false;
    private Thread thread;

    private static boolean battleStarted = false;

    public static GameState gameState = GameState.Game;

    public Game(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    private void init() {
        this.handler = new Handler(this);

        this.battleManager = BattleManager.getInstance();
        this.gameKeyInput = new GameKeyInput(this.handler, this.battleManager);

        this.gameScreen = new GameScreen(handler);
        this.battleScreen = new BattleScreen(this.handler, this.battleManager);
        this.pokemonMenuScreen = new PokemonMenuScreen(handler);

        loadSounds();

        window = new Window(title, width, height);
        window.getCanvas().addKeyListener(gameKeyInput);
    }

    public synchronized void start() {
        if (running) {
            return;
        }

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        if(!running) {
            return;
        }

        running = false;

        try {
            thread.join();
        } catch (InterruptedException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        init();

        long lastTime = System.nanoTime();
        double fps = 60.0;
        double drawInterval = 1000000000 / fps;
        double delta = 0;
        int updates = 0;
        long timer = 0;
        int frames = 0;

        while (running) {
            long currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            while (delta >= 1) {
                update();
                render();
                updates++;
                delta--;
            }

            if (timer >= 1000000000) {
                timer = 0;
                updates = 0;
            }
        }
    }

    private void update() {
        switch (gameState) {
            case Game, Menu, Dialogue -> {
                playMusicIfNeeded("/sounds/azalea_city.wav");
                gameScreen.update();
            }

            case Battle -> {
                if (!battleStarted) {
                    gameKeyInput.resetKeys();
                    this.battleManager.init(getPlayerParty());
                    battleStarted = true;

                    playMusicIfNeeded("/sounds/rival_battle.wav");
                }

                battleScreen.update();
            }

            case PokemonMenu -> pokemonMenuScreen.update();

        }
    }

    private void render() {
        BufferStrategy bs = window.getCanvas().getBufferStrategy();

        if (bs == null) {
            window.getCanvas().createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        switch (gameState) {
            case Game, Menu, Dialogue -> gameScreen.render(g);

            case Battle -> {
                if (battleStarted) {
                    battleScreen.render(g);
                }
            }

            case PokemonMenu -> pokemonMenuScreen.render(g);
        }

        bs.show();
        g.dispose();
    }

    private void loadSounds() {
        SoundManager.loadSound("MenuSound", "/sounds/menu_sound.wav");
        SoundManager.loadSound("ButtonSound", "/sounds/button_sound.wav");
        SoundManager.loadSound("RunningAwaySound", "/sounds/running_away_sound.wav");
        SoundManager.loadSound("FaintedSound", "/sounds/fainted_sound.wav");
    }

    public void playMusicIfNeeded(String path) {
        if(!SoundManager.isPlaying(path)) {
            SoundManager.playMusic(path);
        }
    }

    public GameState getGameState() {
        return  gameState;
    }

    public void setGameState(GameState gameState) {
        Game.gameState = gameState;
    }

    public List<Pokemon> getPlayerParty() {
        List<Pokemon> playerParty = new ArrayList<>();
        PokemonGenerator pokemonGenerator = new PokemonGenerator();
        Pokemon pokemon1 = pokemonGenerator.createMyPokemon("Charmander");
//        Pokemon pokemon2 = pokemonGenerator.createMyPokemon("Squirtle");
//        Pokemon pokemon3 = pokemonGenerator.createMyPokemon("Bulbasaur");
////        Pokemon pokemon4 = pokemonGenerator.createMyPokemon("Charizard");
//        Pokemon pokemon5 = pokemonGenerator.createMyPokemon("Blastoise");
//        Pokemon pokemon6 = pokemonGenerator.createMyPokemon("Venusaur");

        playerParty.add(pokemon1);
//        playerParty.add(pokemon2);
//        playerParty.add(pokemon3);
//        playerParty.add(pokemon4);
//        playerParty.add(pokemon5);
//        playerParty.add(pokemon6);

        return playerParty;
    }

    public void setBattleStarted(boolean battleStarted) {
        Game.battleStarted = battleStarted;
    }

    public GameKeyInput getGameKeyInput() {
        return gameKeyInput;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
