package framework;

import framework.enums.Type;
import objects.Pokemon;
import objects.PokemonBackSprite;
import objects.PokemonFrontSprite;
import objects.PokemonMove;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokemonTeamBuilder {

    List<Pokemon> pokemonParty;

    public PokemonTeamBuilder() {
        this.pokemonParty = new ArrayList<>();
    }

    public List<Pokemon> createPokemonTeam() {
        try {
            FileReader fr = new FileReader("resources/pokemon_front_sprite.json");
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(fr);
            JSONArray array = (JSONArray) object.get("pokemon");

            for (Object o : array) {
                JSONObject pokemonObject = (JSONObject) o;

                if (pokemonObject.get("name").equals("Bulbasaur")) {
                    pokemonParty.add(createPokemon(pokemonObject));
                } else if (pokemonObject.get("name").equals("Charmander")) {
                    pokemonParty.add(createPokemon(pokemonObject));
                } else if (pokemonObject.get("name").equals("Squirtle")) {
                    pokemonParty.add(createPokemon(pokemonObject));
                }
//                if(pokemonObject.get("name").equals("Venusaur")) {
//                    pokemonParty.add(createPokemon(pokemonObject));
//                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return pokemonParty;
    }

    private Pokemon createPokemon(JSONObject object) {
        Pokemon pokemon = new Pokemon();

        setStats(object, pokemon);

        JSONArray typeList = (JSONArray) object.get("type");

        if (typeList.size() > 1) {
            pokemon.setType1(getType(typeList.get(0).toString()));
            pokemon.setType2(getType(typeList.get(1).toString()));
        } else {
            pokemon.setType1(getType(typeList.get(0).toString()));
        }

        pokemon.setPokemonMovesList(setMoves(pokemon.getType1()));

        return pokemon;
    }

    private void setStats(JSONObject object, Pokemon pokemon) {
        int row = Integer.parseInt(object.get("row").toString());
        int col = Integer.parseInt(object.get("col").toString());

        pokemon.setName(String.valueOf(object.get("name")));
        pokemon.setLevel(100);
        pokemon.setCurrentHealth(Integer.parseInt(String.valueOf(object.get("hp"))));
        pokemon.setMaxHealth(Integer.parseInt(String.valueOf(object.get("hp"))));
        pokemon.setAttack(Integer.parseInt(String.valueOf(object.get("attack"))));
        pokemon.setDefense(Integer.parseInt(String.valueOf(object.get("defense"))));
        pokemon.setSpecialAttack(Integer.parseInt(String.valueOf(object.get("specialAttack"))));
        pokemon.setSpecialDefense(Integer.parseInt(String.valueOf(object.get("specialDefense"))));
        pokemon.setSpeed(Integer.parseInt(String.valueOf(object.get("speed"))));
        pokemon.setFrontSprite(new PokemonFrontSprite(col, row, 58, 58));
        pokemon.setBackSprite(new PokemonBackSprite(col, row, 58, 58));
    }

    private List<PokemonMove> setMoves(Type type) {
        List<PokemonMove> pokemonMoves = new ArrayList<>();

        switch (type) {
            case Fire -> {
                pokemonMoves.add(new PokemonMove("Flamethrower", Type.Fire, 90, 100, 15, 15, PokemonMove.MoveCategory.Special));
                pokemonMoves.add(new PokemonMove("Dragon Claw", Type.Dragon, 80, 100, 9, 15, PokemonMove.MoveCategory.Physical));
                pokemonMoves.add(new PokemonMove("Dragon Dance", Type.Dragon, 0, 100, 10, 10, PokemonMove.MoveCategory.Status));
            }
            case Water -> {
                pokemonMoves.add(new PokemonMove("Hydro Pump", Type.Water, 120, 80, 5, 5, PokemonMove.MoveCategory.Special));
                pokemonMoves.add(new PokemonMove("Skull Bash", Type.Normal, 100, 100, 15, 15, PokemonMove.MoveCategory.Physical));
                pokemonMoves.add(new PokemonMove("Ice Beam", Type.Ice, 95, 100, 10, 10, PokemonMove.MoveCategory.Special));
            }
            case Grass -> {
                pokemonMoves.add(new PokemonMove("Giga Drain", Type.Grass, 60, 100, 10, 10, PokemonMove.MoveCategory.Special));
                pokemonMoves.add(new PokemonMove("Sludge Bomb", Type.Poison, 90, 100, 10, 10, PokemonMove.MoveCategory.Special));
                pokemonMoves.add(new PokemonMove("Earthquake", Type.Ground, 100, 100, 10, 10, PokemonMove.MoveCategory.Physical));
            }
        }

        return pokemonMoves;
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
}
