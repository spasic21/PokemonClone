package screen;

import framework.Handler;
import objects.Pokemon;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PokemonMenuScreen extends Screen {

    List<PokemonMenuTile> pokemonMenuTiles = new ArrayList<>();
    List<Point> emptyTiles;

    public PokemonMenuScreen(Handler handler) {
        super(handler);

        List<Pokemon> pokemonParty = handler.getPokemonParty();

        if (pokemonParty.size() < 6) {
            emptyTiles = new ArrayList<>();
        }

        int index = 0;

        int[] xPositions = {150, 650};
        int[] yPositions = {75, 300, 525};

        for (int y : yPositions) {
            for (int x : xPositions) {
                if(index < pokemonParty.size()) {
                    pokemonMenuTiles.add(new PokemonMenuTile(x, y, 1, pokemonParty.get(index).getDexNumber(), pokemonParty.get(index)));
                    index++;
                } else {
                    emptyTiles.add(new Point(x, y));
                }
            }
        }
    }

    @Override
    public void update() {
        int optionId = handler.getGameKeyInput().getPokemonMenuOptionId();

        if(optionId >= pokemonMenuTiles.size()) {
            if(pokemonMenuTiles.size() == 1) {
                optionId = 0;
            }
        }

        for(int i = 0; i < pokemonMenuTiles.size(); i++) {
            pokemonMenuTiles.get(i).update(optionId == i);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(254, 255, 221));
        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        for(PokemonMenuTile pokemonMenuTile : pokemonMenuTiles) {
            pokemonMenuTile.render(g);
        }

        if(emptyTiles != null && !emptyTiles.isEmpty()) {
            g.setColor(Color.GRAY);

            for(Point emptyTile : emptyTiles) {
                g.fillRoundRect((int)emptyTile.getX(), (int)emptyTile.getY(), 400, 175, 20, 20);
            }
        }
    }
}
