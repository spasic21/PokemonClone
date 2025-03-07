package framework.pokemon;

import framework.enums.ExpType;
import framework.enums.MoveCategory;
import framework.enums.Type;
import objects.pokemon.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonDatabase {

    private final Map<String, Pokemon> pokemonDatabase = new HashMap<>();

    public PokemonDatabase() {
    }

    public void initDatabase() {
        try (FileReader baseStatReader = new FileReader("resources/pokemon_base_stats.json")) {

            JSONParser parser = new JSONParser();
            JSONObject pokemonStatObject = (JSONObject) parser.parse(baseStatReader);
            JSONArray statArray = (JSONArray) pokemonStatObject.get("pokemon");

            for (int i = 0; i < statArray.size(); i++) {
                Pokemon pokemon = new Pokemon();
                JSONObject pokemonObject = (JSONObject) statArray.get(i);

                if (pokemonObject.get("form") != null) continue;
                if (Integer.parseInt(String.valueOf(pokemonObject.get("dexNumber"))) == 151) continue;

                pokemon.setName(String.valueOf(pokemonObject.get("name")));
                pokemon.setDexNumber(Integer.parseInt(String.valueOf(pokemonObject.get("dexNumber"))));
                pokemon.setHeight(Integer.parseInt(String.valueOf(pokemonObject.get("height"))));
                pokemon.setWeight(Integer.parseInt(String.valueOf(pokemonObject.get("weight"))));
                pokemon.setExpYield(Integer.parseInt(String.valueOf(pokemonObject.get("baseExperience"))));

                int col = Integer.parseInt(pokemonObject.get("col").toString());
                int row = Integer.parseInt(pokemonObject.get("row").toString());

                pokemon.setFrontSprite(new PokemonFrontSprite(pokemon.getDexNumber(), col, row, 58, 58));
                pokemon.setBackSprite(new PokemonBackSprite(pokemon.getDexNumber(), col, row, 58, 58));

                JSONArray typeList = (JSONArray) pokemonObject.get("type");

                if (typeList.size() > 1) {
                    pokemon.setType1(getType(typeList.get(0).toString()));
                    pokemon.setType2(getType(typeList.get(1).toString()));
                } else {
                    pokemon.setType1(getType(typeList.get(0).toString()));
                }

                JSONArray baseStatArray = (JSONArray) pokemonObject.get("baseStats");
                List<PokemonBaseStat> baseStats = new ArrayList<>();

                if (baseStatArray != null) {
                    for (Object o : baseStatArray) {
                        JSONObject s = (JSONObject) o;
                        baseStats.add(new PokemonBaseStat(
                                String.valueOf(s.get("name")),
                                Integer.parseInt(String.valueOf(s.get("baseStat"))),
                                Integer.parseInt(String.valueOf(s.get("effortValue")))))
                        ;
                    }
                }

                pokemon.setBaseStats(baseStats);

                JSONArray moveArray = (JSONArray) pokemonObject.get("moves");

                pokemon.setPokemonMovesList(setMoves(moveArray));
                pokemon.setBaseHappiness(Integer.parseInt(String.valueOf(pokemonObject.get("baseHappiness"))));
                pokemon.setCaptureRate(Integer.parseInt(String.valueOf(pokemonObject.get("captureRate"))));

                switch (String.valueOf(pokemonObject.get("growthRate"))) {
                    case "Erratic" -> pokemon.setExpType(ExpType.Erratic);
                    case "Fast" -> pokemon.setExpType(ExpType.Fast);
                    case "Medium-Fast" -> pokemon.setExpType(ExpType.MediumFast);
                    case "Medium-Slow" -> pokemon.setExpType(ExpType.MediumSlow);
                    case "Slow" -> pokemon.setExpType(ExpType.Slow);
                    default -> pokemon.setExpType(ExpType.Fluctuating);
                }

                JSONArray eggGroupArray = (JSONArray) pokemonObject.get("eggGroup");
                List<String> eggGroup = new ArrayList<>();

                if (eggGroupArray != null) {
                    for (Object o : eggGroupArray) {
                        String egg = (String) o;
                        eggGroup.add(String.valueOf(egg));
                    }
                }

                pokemon.setEggGroups(eggGroup);
                pokemon.setDescription(String.valueOf(pokemonObject.get("description")));

                pokemonDatabase.put(pokemon.getName(), pokemon);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Pokemon getPokemon(String pokemonName) {
        return pokemonDatabase.get(pokemonName);
    }

    private Type getType(String type) {
        return switch (type) {
            case "Fire" -> Type.Fire;
            case "Water" -> Type.Water;
            case "Grass" -> Type.Grass;
            case "Electric" -> Type.Electric;
            case "Ice" -> Type.Ice;
            case "Fighting" -> Type.Fighting;
            case "Poison" -> Type.Poison;
            case "Ground" -> Type.Ground;
            case "Flying" -> Type.Flying;
            case "Psychic" -> Type.Psychic;
            case "Bug" -> Type.Bug;
            case "Rock" -> Type.Rock;
            case "Ghost" -> Type.Ghost;
            case "Dark" -> Type.Dark;
            case "Dragon" -> Type.Dragon;
            case "Steel" -> Type.Steel;
            case "Fairy" -> Type.Fairy;
            default -> Type.Normal;
        };
    }

    private MoveCategory getMoveCategory(String move) {
        return switch (move) {
            case "Physical" -> MoveCategory.Physical;
            case "Special" -> MoveCategory.Special;
            case "Status" -> MoveCategory.Status;
            default -> null;
        };
    }

    private List<PokemonMove> setMoves(JSONArray moves) {
        List<PokemonMove> movesList = new ArrayList<>();

        if (moves != null) {
            for (Object o : moves) {
                JSONObject move = (JSONObject) o;
                int power = 0;
                int accuracy = 0;

                if (move.get("power") != null) {
                    power = Integer.parseInt(move.get("power").toString());
                }

                if (move.get("accuracy") != null) {
                    accuracy = Integer.parseInt(move.get("accuracy").toString());
                }

                movesList.add(new PokemonMove(
                        String.valueOf(move.get("name")),
                        getType(String.valueOf(move.get("type"))),
                        power,
                        accuracy,
                        Integer.parseInt(String.valueOf(move.get("powerPoints"))),
                        Integer.parseInt(String.valueOf(move.get("powerPoints"))),
                        getMoveCategory(String.valueOf(move.get("moveCategory"))),
                        Integer.parseInt(String.valueOf(move.get("priority"))),
                        Integer.parseInt(String.valueOf(move.get("levelLearnedAt"))),
                        String.valueOf(move.get("description"))
                ));
            }
        }


        return movesList;
    }
}
