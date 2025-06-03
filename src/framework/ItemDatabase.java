package framework;

import framework.enums.ItemCategory;
import objects.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemDatabase {

    private final List<Item> itemDatabase = new ArrayList<>();

    private final SpriteSheet spriteSheet;

    public ItemDatabase() {
        spriteSheet = new SpriteSheet("/sprites/item_sprite_sheet.png");
    }

    public void initDatabase() {
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("item_database.json")) {
            if(inputStream == null) {
                throw new FileNotFoundException("Resource not found: item_database.json");
            }

            BufferedReader itemReader = new BufferedReader(new InputStreamReader(inputStream));

            JSONParser parser = new JSONParser();
            JSONObject itemObject = (JSONObject) parser.parse(itemReader);
            JSONArray itemArray = (JSONArray) itemObject.get("items");
            JSONArray pokeballArray = (JSONArray) itemObject.get("pokeballs");

            sortItems(itemArray, "items");
            sortItems(pokeballArray, "pokeballs");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void sortItems(JSONArray array, String category) {
        for (Object o : array) {
            JSONObject item = (JSONObject) o;
            String name = String.valueOf(item.get("name"));
            String description = String.valueOf(item.get("description"));
            int count = new Random().nextInt(10) + 1;
            int col = Integer.parseInt(String.valueOf(item.get("col")));
            int row = Integer.parseInt(String.valueOf(item.get("row")));
            BufferedImage image = spriteSheet.grabImage(col, row, 24, 24);

            switch (category) {
                case "items" -> itemDatabase.add(new Item(name, description, count, image, ItemCategory.Item));
                case "pokeballs" -> itemDatabase.add(new Item(name, description, count, image, ItemCategory.Pokeball));
            }
        }
    }

    public List<Item> getItemDatabase() {
        return itemDatabase;
    }
}
