package ui;

import battle.BattleManager;
import framework.EventFlagManager;
import framework.Handler;
import framework.ItemDatabase;
import framework.MusicManager;
import framework.SoundManager;
import framework.enums.Location;
import framework.npc.NpcDatabase;
import framework.enums.GameState;
import framework.pokemon.PokemonDatabase;
import framework.pokemon.PokemonGenerator;
import keyInput.GameKeyInput;
import objects.Bag;
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

    private ItemDatabase itemDatabase;

    private GameKeyInput gameKeyInput;

    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private BattleScreen battleScreen;
    private TransitionScreen transitionScreen;
    private PokemonMenuScreen pokemonMenuScreen;
    private PokemonSummaryScreen pokemonSummaryScreen;
    private BagScreen bagScreen;
    private BattleManager battleManager;
    private MusicManager musicManager;
    private NpcDatabase npcDatabase;
    private Bag bag;
    private boolean running = false;
    private Thread thread;

    private boolean battleStarted = false;

    private volatile boolean databaseLoaded = false;

    private GameState previousState = GameState.Loading;

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
            this.itemDatabase = new ItemDatabase();

            long start = System.currentTimeMillis();
            this.pokemonDatabase.initDatabase();
            this.itemDatabase.initDatabase();
            long end = System.currentTimeMillis();

            System.out.println("Database Initialization took " + (end - start) / 1000 + "s");

            onDatabaseLoaded();
        }).start();
    }

    private void onDatabaseLoaded() {
        this.handler.setEventFlagManager(new EventFlagManager());

        this.npcDatabase = new NpcDatabase();
        this.npcDatabase.initDatabase();
        this.handler.setNpcDatabase(this.npcDatabase);

        this.handler.setPokemonParty(getPlayerParty());

        this.handler.setBag(new Bag(itemDatabase));
        this.handler.setDialogueScreen(new framework.DialogueScreen(handler));

        this.battleManager = BattleManager.getInstance();
        this.gameKeyInput = new GameKeyInput(this.handler, this.battleManager);


        this.gameScreen = new GameScreen(handler);
        this.battleScreen = new BattleScreen(this.handler, this.battleManager);
        this.transitionScreen = new TransitionScreen(this.handler);
        this.pokemonMenuScreen = new PokemonMenuScreen(handler);
        this.pokemonSummaryScreen = new PokemonSummaryScreen(handler);
        this.bagScreen = new BagScreen(handler);

        loadSounds();

        this.musicManager = new MusicManager();
        preloadMusic();

        window.getCanvas().addKeyListener(gameKeyInput);
        databaseLoaded = true;
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
        if (!databaseLoaded && gameState != GameState.Loading) return;

        if (musicManager != null) {
            musicManager.update();
        }

        if (previousState == GameState.Battle && gameState != GameState.Battle) {
            battleStarted = false;
        }
        previousState = gameState;

        switch (gameState) {
            case Loading -> loadingScreen.update();
            case Game -> {
                if (musicManager != null
                        && musicManager.getState() == MusicManager.MusicState.IDLE
                        && handler.getWorld() != null) {
                    musicManager.playLocationMusic(handler.getWorld().getLocation());
                }
                gameKeyInput.tickCooldown();
                gameScreen.update();
            }
            case Menu, Dialogue -> {
                // World is paused — only key input handlers update state for these screens
            }

            case Transition -> {
                if (musicManager != null) {
                    if (handler.getNextGameState() == GameState.Battle
                            && musicManager.getState() != MusicManager.MusicState.PLAYING_BATTLE) {
                        musicManager.playBattleMusic(true);
                    } else if (handler.getNextGameState() == GameState.Game
                            && handler.getPendingWorld() != null
                            && musicManager.getState() != MusicManager.MusicState.CROSSFADING) {
                        musicManager.playLocationMusic(handler.getPendingWorld().getLocation());
                    }
                }

                transitionScreen.update(handler.getTransitionType());

                if (transitionScreen.isAtMidpoint(handler.getTransitionType()) && handler.hasPendingWorld()) {
                    handler.applyPendingWorld();
                }
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
            case Bag -> bagScreen.update();
        }
    }

    private void renderForState(Graphics g, GameState state) {
        switch (state) {
            case Loading               -> loadingScreen.render(g);
            case Game, Menu, Dialogue  -> gameScreen.render(g);
            case PokemonMenu           -> pokemonMenuScreen.render(g);
            case PokemonSummary        -> pokemonSummaryScreen.render(g);
            case Bag                   -> bagScreen.render(g);
            case Battle                -> { if (battleStarted) battleScreen.render(g); }
            default                    -> {}
        }
    }

    private void render() {
        if (!databaseLoaded && gameState != GameState.Loading) return;

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
                GameState renderState = transitionScreen.isFadingOut()
                        ? handler.getTransitionSourceState()
                        : handler.getNextGameState();
                renderForState(g, renderState);

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
            case Bag -> bagScreen.render(g);
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

    private void preloadMusic() {
        for (Location location : Location.values()) {
            if (location.getMusicPath() != null) {
                SoundManager.getMusicClip(location.getMusicPath());
            }
        }
        SoundManager.getMusicClip("/sounds/johto_wild_pokemon_battle.wav");
        SoundManager.getMusicClip("/sounds/rival_battle.wav");
        SoundManager.getMusicClip("/sounds/victory_wild_pokemon.wav");
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

    public MusicManager getMusicManager() {
        return musicManager;
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
