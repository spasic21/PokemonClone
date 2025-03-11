package objects;

import framework.ItemDatabase;

import java.util.ArrayList;
import java.util.List;

public class Bag {

    private final List<Item> itemPocket = new ArrayList<>();

    private final List<Item> pokeballPocket = new ArrayList<>();

    public Bag(ItemDatabase itemDatabase) {

        itemDatabase.getItemDatabase().forEach(item -> {
            switch (item.getCategory()) {
                case Item -> itemPocket.add(item);
                case Pokeball -> pokeballPocket.add(item);
            }
        });
    }

    public List<Item> getItemPocket() {
        return itemPocket;
    }

    public List<Item> getPokeballPocket() {
        return pokeballPocket;
    }

    public void addItem(Item item) {
        itemPocket.add(item);
    }

    public void addPokeball(Item item) {
        pokeballPocket.add(item);
    }
}
