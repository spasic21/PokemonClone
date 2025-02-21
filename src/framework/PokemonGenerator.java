package framework;

import framework.enums.ExpType;
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
import java.util.Random;

public class PokemonGenerator {

    public PokemonGenerator() {

    }

    public Pokemon generatePokemon() {
        Pokemon pokemon = new Pokemon();

        setBaseStats(pokemon);
        generateIndividualValues(pokemon);

        int health = calculateHP(pokemon.getBaseHP(), pokemon.getHealthIV(), 0, pokemon.getLevel());
        int attack = calculateStat(pokemon.getBaseAttack(), pokemon.getAttackIV(), 0, pokemon.getLevel());
        int defense = calculateStat(pokemon.getBaseDefense(), pokemon.getDefenseIV(), 0, pokemon.getLevel());
        int specialAttack = calculateStat(pokemon.getBaseSpecialAttack(), pokemon.getSpecialAttackIV(), 0, pokemon.getLevel());
        int specialDefense = calculateStat(pokemon.getBaseSpecialDefense(), pokemon.getSpecialDefenseIV(), 0, pokemon.getLevel());
        int speed = calculateStat(pokemon.getBaseSpeed(), pokemon.getSpeedIV(), 0, pokemon.getLevel());

        pokemon.setCurrentHealth(health);
        pokemon.setMaxHealth(health);
        pokemon.setAttack(attack);
        pokemon.setDefense(defense);
        pokemon.setSpecialAttack(specialAttack);
        pokemon.setSpecialDefense(specialDefense);
        pokemon.setSpeed(speed);
        pokemon.setPokemonMovesList(setMoves(pokemon.getType1()));

        return pokemon;
    }

    private void setBaseStats(Pokemon pokemon) {
        try (FileReader baseStatReader = new FileReader("resources/baseStat.json");
             FileReader expTypeReader = new FileReader("resources/expType.json");
             FileReader effortValueReader = new FileReader("resources/effortValueYield.json")) {

            JSONParser parser = new JSONParser();
            JSONObject pokemonStatObject = (JSONObject) parser.parse(baseStatReader);
            JSONArray statArray = (JSONArray) pokemonStatObject.get("pokemon");

            JSONObject baseStatObject = (JSONObject) parser.parse(expTypeReader);
            JSONArray expTypeArray = (JSONArray) baseStatObject.get("pokemon");

            JSONObject effortValueObject = (JSONObject) parser.parse(effortValueReader);
            JSONArray effortValueArray = (JSONArray) effortValueObject.get("pokemon");

//            Random random = new Random();
//            int index = random.nextInt(random.nextInt(24));
            int index = 0;

            JSONObject pokemonObject = (JSONObject) statArray.get(index);
            JSONObject expObject = (JSONObject) expTypeArray.get(index);
            JSONObject effortObject = (JSONObject) effortValueArray.get(index);

            pokemon.setName(String.valueOf(pokemonObject.get("name")));
            pokemon.setLevel(30);

            JSONArray typeList = (JSONArray) pokemonObject.get("type");

            if (typeList.size() > 1) {
                pokemon.setType1(getType(typeList.get(0).toString()));
                pokemon.setType2(getType(typeList.get(1).toString()));
            } else {
                pokemon.setType1(getType(typeList.get(0).toString()));
            }

            pokemon.setBaseHP(Integer.parseInt(String.valueOf(pokemonObject.get("hp"))));
            pokemon.setBaseAttack(Integer.parseInt(String.valueOf(pokemonObject.get("attack"))));
            pokemon.setBaseDefense(Integer.parseInt(String.valueOf(pokemonObject.get("defense"))));
            pokemon.setBaseSpecialAttack(Integer.parseInt(String.valueOf(pokemonObject.get("specialAttack"))));
            pokemon.setBaseSpecialDefense(Integer.parseInt(String.valueOf(pokemonObject.get("specialDefense"))));
            pokemon.setBaseSpeed(Integer.parseInt(String.valueOf(pokemonObject.get("speed"))));

            int row = Integer.parseInt(pokemonObject.get("row").toString());
            int col = Integer.parseInt(pokemonObject.get("col").toString());

            pokemon.setFrontSprite(new PokemonFrontSprite(col, row, 58, 58));
            pokemon.setBackSprite(new PokemonBackSprite(col, row, 58, 58));

            switch(String.valueOf(expObject.get("growthRate"))) {
                case "Erratic" -> pokemon.setExpType(ExpType.Erratic);
                case "Fast" -> pokemon.setExpType(ExpType.Fast);
                case "Medium Fast" -> pokemon.setExpType(ExpType.MediumFast);
                case "Medium Slow" -> pokemon.setExpType(ExpType.MediumSlow);
                case "Slow" -> pokemon.setExpType(ExpType.Slow);
                default -> pokemon.setExpType(ExpType.Fluctuating);
            }

            pokemon.setExpYield(Integer.parseInt(String.valueOf(effortObject.get("exp"))));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void setMyPokemonStats(Pokemon pokemon, String name) {
        try (FileReader baseStatReader = new FileReader("resources/baseStat.json");
             FileReader expTypeReader = new FileReader("resources/expType.json")) {

            JSONParser parser = new JSONParser();
            JSONObject pokemonStatObject = (JSONObject) parser.parse(baseStatReader);
            JSONArray statArray = (JSONArray) pokemonStatObject.get("pokemon");

            JSONObject baseStatObject = (JSONObject) parser.parse(expTypeReader);
            JSONArray expTypeArray = (JSONArray) baseStatObject.get("pokemon");

            for (Object o : statArray) {
                JSONObject pokemonObject = (JSONObject) o;

                if (String.valueOf(pokemonObject.get("name")).equals(name)) {
                    pokemon.setName(name);
                    pokemon.setDexNumber(Integer.parseInt(String.valueOf(pokemonObject.get("dexNumber"))));
                    pokemon.setLevel(32);

                    JSONArray typeList = (JSONArray) pokemonObject.get("type");

                    if (typeList.size() > 1) {
                        pokemon.setType1(getType(typeList.get(0).toString()));
                        pokemon.setType2(getType(typeList.get(1).toString()));
                    } else {
                        pokemon.setType1(getType(typeList.get(0).toString()));
                    }

                    pokemon.setBaseHP(Integer.parseInt(String.valueOf(pokemonObject.get("hp"))));
                    pokemon.setBaseAttack(Integer.parseInt(String.valueOf(pokemonObject.get("attack"))));
                    pokemon.setBaseDefense(Integer.parseInt(String.valueOf(pokemonObject.get("defense"))));
                    pokemon.setBaseSpecialAttack(Integer.parseInt(String.valueOf(pokemonObject.get("specialAttack"))));
                    pokemon.setBaseSpecialDefense(Integer.parseInt(String.valueOf(pokemonObject.get("specialDefense"))));
                    pokemon.setBaseSpeed(Integer.parseInt(String.valueOf(pokemonObject.get("speed"))));

                    int row = Integer.parseInt(pokemonObject.get("row").toString());
                    int col = Integer.parseInt(pokemonObject.get("col").toString());

                    pokemon.setFrontSprite(new PokemonFrontSprite(col, row, 58, 58));
                    pokemon.setBackSprite(new PokemonBackSprite(col, row, 58, 58));
                }
            }

            for (Object o : expTypeArray) {
                JSONObject expTypeObject = (JSONObject) o;

                if (expTypeObject.get("name").equals(name)) {
                    ExperienceCalculator calculator = new ExperienceCalculator();

                    switch (String.valueOf(expTypeObject.get("growthRate"))) {
                        case "Erratic" -> pokemon.setExpType(ExpType.Erratic);
                        case "Fast" -> pokemon.setExpType(ExpType.Fast);
                        case "Medium Fast" -> pokemon.setExpType(ExpType.MediumFast);
                        case "Medium Slow" -> pokemon.setExpType(ExpType.MediumSlow);
                        case "Slow" -> pokemon.setExpType(ExpType.Slow);
                        default -> pokemon.setExpType(ExpType.Fluctuating);
                    }

                    int delta = calculator.calculateExpNextLevel(pokemon);

                    pokemon.setCurrentExp(0);
                    pokemon.setExpNextLevel(delta);
                }
            }


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void generateIndividualValues(Pokemon pokemon) {
        Random random = new Random();

        pokemon.setHealthIV(random.nextInt(32));
        pokemon.setAttackIV(random.nextInt(32));
        pokemon.setDefenseIV(random.nextInt(32));
        pokemon.setSpecialAttackIV(random.nextInt(32));
        pokemon.setSpecialDefenseIV(random.nextInt(32));
        pokemon.setSpeedIV(random.nextInt(32));
    }

    private int calculateHP(int baseStat, int iv, int ev, int level) {
        double numerator = (2 * baseStat + iv + ((double) ev / 4)) * level;

        return (int) (numerator / 100) + level + 10;
    }

    private int calculateStat(int baseStat, int iv, int ev, int level) {
        double numerator = (2 * baseStat + iv + ((double) ev / 4)) * level;

        return (int) (numerator / 100) + 5;
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

    private List<PokemonMove> setMoves(Type type) {
        List<PokemonMove> pokemonMoves = new ArrayList<>();

        switch (type) {
            default -> {
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

    public Pokemon createMyPokemon(String name) {
        Pokemon pokemon = new Pokemon();

        setMyPokemonStats(pokemon, name);

        pokemon.setHealthIV(31);
        pokemon.setAttackIV(31);
        pokemon.setDefenseIV(31);
        pokemon.setSpecialAttackIV(31);
        pokemon.setSpecialDefenseIV(31);
        pokemon.setSpeedIV(31);

        int health = calculateHP(pokemon.getBaseHP(), pokemon.getHealthIV(), 0, pokemon.getLevel());
        int attack = calculateStat(pokemon.getBaseAttack(), pokemon.getAttackIV(), 0, pokemon.getLevel());
        int defense = calculateStat(pokemon.getBaseDefense(), pokemon.getDefenseIV(), 0, pokemon.getLevel());
        int specialAttack = calculateStat(pokemon.getBaseSpecialAttack(), pokemon.getSpecialAttackIV(), 0, pokemon.getLevel());
        int specialDefense = calculateStat(pokemon.getBaseSpecialDefense(), pokemon.getSpecialDefenseIV(), 0, pokemon.getLevel());
        int speed = calculateStat(pokemon.getBaseSpeed(), pokemon.getSpeedIV(), 0, pokemon.getLevel());

        pokemon.setCurrentHealth(health);
        pokemon.setMaxHealth(health);
        pokemon.setAttack(attack);
        pokemon.setDefense(defense);
        pokemon.setSpecialAttack(specialAttack);
        pokemon.setSpecialDefense(specialDefense);
        pokemon.setSpeed(speed);
        pokemon.setPokemonMovesList(setMoves(pokemon.getType1()));

        return pokemon;
    }
}
