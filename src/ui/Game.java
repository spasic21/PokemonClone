package ui;

import battle.BattleManager;
import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;
import framework.pokemon.PokemonDatabase;
import framework.pokemon.PokemonGenerator;
import framework.spawn.SpawnManager;
import keyInput.GameKeyInput;
import objects.pokemon.Pokemon;
import screen.*;

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

    private PokemonDatabase pokemonDatabase;

    private GameKeyInput gameKeyInput;

    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private BattleScreen battleScreen;
    private TransitionScreen transitionScreen;
    private PokemonMenuScreen pokemonMenuScreen;
    private PokemonSummaryScreen pokemonSummaryScreen;
    private BattleManager battleManager;
    private boolean running = false;
    private Thread thread;

    private boolean battleStarted = false;

    public static GameState gameState = GameState.Loading;

    public Game(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    private void init() {
        window = new Window(title, width, height);
        this.handler = new Handler(this);
        this.loadingScreen = new LoadingScreen(this.handler);

        new Thread(() -> {
            this.pokemonDatabase = new PokemonDatabase();

            long start = System.currentTimeMillis();
            this.pokemonDatabase.initDatabase();
            long end = System.currentTimeMillis();

            System.out.println("Pokemon Database Initialization took " + (end - start) / 1000 + "s");

            onDatabaseLoaded();
        }).start();
    }

    private void onDatabaseLoaded() {
        this.handler.setPokemonParty(getPlayerParty());

        SpawnManager spawnManager = SpawnManager.getInstance();

        spawnManager.init();

        this.handler.setSpawnManager(spawnManager);

        this.battleManager = BattleManager.getInstance();
        this.gameKeyInput = new GameKeyInput(this.handler, this.battleManager);


        this.gameScreen = new GameScreen(handler);
        this.battleScreen = new BattleScreen(this.handler, this.battleManager);
        this.transitionScreen = new TransitionScreen(this.handler);
        this.pokemonMenuScreen = new PokemonMenuScreen(handler);
        this.pokemonSummaryScreen = new PokemonSummaryScreen(handler);

        loadSounds();


        window.getCanvas().addKeyListener(gameKeyInput);
        handler.setNextTransition(1, GameState.Game);
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
        if (!running) {
            return;
        }

        running = false;

        try {
            thread.join();
        } catch (InterruptedException e) {
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
            case Loading -> loadingScreen.update();
            case Game, Menu, Dialogue -> {
//                playMusicIfNeeded("/sounds/azalea_city.wav");
                gameScreen.update();
            }

            case Transition -> {
//                if(handler.getNextGameState() == GameState.Battle) playMusicIfNeeded("/sounds/johto_wild_pokemon_battle.wav");

                transitionScreen.update(handler.getTransitionType());
            }

            case Battle -> {
                if (!battleStarted) {
                    gameKeyInput.resetKeys();
                    this.battleManager.init(pokemonDatabase, handler.getPokemonParty());
                    battleStarted = true;
                }

                battleScreen.update();
            }

            case PokemonMenu -> pokemonMenuScreen.update();
            case PokemonSummary -> pokemonSummaryScreen.update();

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
            case Loading -> loadingScreen.render(g);
            case Game, Menu, Dialogue -> gameScreen.render(g);

            case Transition -> {
                transitionScreen.render(g, handler.getTransitionType());

                if (transitionScreen.isFinished(handler.getTransitionType())) {
                    if (handler.getNextGameState() == GameState.Battle) {
                        gameState = GameState.Battle;
                        battleStarted = false;
                    } else {
                        gameState = handler.getNextGameState();
                    }
                }
            }

            case Battle -> {
                if (battleStarted) {
                    battleScreen.render(g);
                }
            }

            case PokemonMenu -> pokemonMenuScreen.render(g);
            case PokemonSummary -> pokemonSummaryScreen.render(g);
        }

        bs.show();
        g.dispose();
    }

    private void loadSounds() {
        SoundManager.loadSound("MenuSound", "/sounds/menu_sound.wav");
        SoundManager.loadSound("ButtonSound", "/sounds/button_sound.wav");
        SoundManager.loadSound("RunningAwaySound", "/sounds/running_away_sound.wav");
        SoundManager.loadSound("FaintedSound", "/sounds/fainted_sound.wav");
        SoundManager.loadSound("LowHealthSound", "/sounds/low_health_sound.wav");
    }

    public void playMusicIfNeeded(String path) {
        if (!SoundManager.isPlaying(path)) {
            SoundManager.playMusic(path);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        Game.gameState = gameState;
    }

    public List<Pokemon> getPlayerParty() {
        List<Pokemon> playerParty = new ArrayList<>();
        PokemonGenerator pokemonGenerator = new PokemonGenerator(pokemonDatabase);
        Pokemon pokemon1 = pokemonGenerator.createMyPokemon("Charmander");
        Pokemon pokemon2 = pokemonGenerator.createMyPokemon("Totodile");

        playerParty.add(pokemon1);
        playerParty.add(pokemon2);

        return playerParty;
    }

    public void setBattleStarted(boolean battleStarted) {
        this.battleStarted = battleStarted;
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
