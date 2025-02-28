package framework.pokemon;

import objects.pokemon.Pokemon;
import objects.pokemon.PokemonBaseStat;
import objects.pokemon.PokemonMove;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PokemonGenerator {

    private final PokemonDatabase pokemonDatabase;

    private final String[] pokemonEncounters = {"Bulbasaur", "Ivysaur", "Pidgey", "Rattata", "Caterpie", "Weedle", "Zubat"};

    public PokemonGenerator(PokemonDatabase pokemonDatabase) {
        this.pokemonDatabase = pokemonDatabase;
    }

    public Pokemon generatePokemon(boolean isPlayerPokemon) {
        Pokemon pokemon = new Pokemon();

        if (isPlayerPokemon) {
            pokemon.setName("Charmander");
        } else {
            Random rand = new Random();
            pokemon.setName(pokemonEncounters[rand.nextInt(pokemonEncounters.length)]);
        }

        generateIndividualValues(pokemon, isPlayerPokemon);

        Pokemon databasePokemon = pokemonDatabase.getPokemon(pokemon.getName());

        for (PokemonBaseStat stat : databasePokemon.getBaseStats()) {
            switch (stat.name()) {
                case "hp" -> {
                    int health = calculateHP(stat.baseStat(), pokemon.getHealthIV(), 0, pokemon.getLevel());
                    pokemon.setCurrentHealth(health);
                    pokemon.setMaxHealth(health);
                }

                case "attack" -> {
                    int attack = calculateStat(stat.baseStat(), pokemon.getAttackIV(), 0, pokemon.getLevel());
                    pokemon.setAttack(attack);
                }

                case "defense" -> {
                    int defense = calculateStat(stat.baseStat(), pokemon.getDefenseIV(), 0, pokemon.getLevel());
                    pokemon.setDefense(defense);
                }

                case "specialAttack" -> {
                    int specialAttack = calculateStat(stat.baseStat(), pokemon.getSpecialAttackIV(), 0, pokemon.getLevel());
                    pokemon.setSpecialAttack(specialAttack);
                }

                case "specialDefense" -> {
                    int specialDefense = calculateStat(stat.baseStat(), pokemon.getSpecialDefenseIV(), 0, pokemon.getLevel());
                    pokemon.setSpecialDefense(specialDefense);
                }

                case "speed" -> {
                    int speed = calculateStat(stat.baseStat(), pokemon.getSpeedIV(), 0, pokemon.getLevel());
                    pokemon.setSpeed(speed);
                }
            }
        }

        pokemon.setDexNumber(databasePokemon.getDexNumber());
        pokemon.setType1(databasePokemon.getType1());

        if (databasePokemon.getType2() != null) pokemon.setType2(databasePokemon.getType2());

        pokemon.setFrontSprite(databasePokemon.getFrontSprite());
        pokemon.setBackSprite(databasePokemon.getBackSprite());

        pokemon.setExpType(databasePokemon.getExpType());
        pokemon.setExpYield(databasePokemon.getExpYield());
        pokemon.setPokemonMovesList(setMoves(pokemon));

        return pokemon;
    }

    private void generateIndividualValues(Pokemon pokemon, boolean isPlayerPokemon) {
        if (isPlayerPokemon) {
            pokemon.setLevel(15);
            pokemon.setHealthIV(31);
            pokemon.setAttackIV(31);
            pokemon.setDefenseIV(31);
            pokemon.setSpecialAttackIV(31);
            pokemon.setSpecialDefenseIV(31);
            pokemon.setSpeedIV(31);
        } else {
            Random random = new Random();

            pokemon.setLevel(random.nextInt(12) + 5);
            pokemon.setHealthIV(random.nextInt(32));
            pokemon.setAttackIV(random.nextInt(32));
            pokemon.setDefenseIV(random.nextInt(32));
            pokemon.setSpecialAttackIV(random.nextInt(32));
            pokemon.setSpecialDefenseIV(random.nextInt(32));
            pokemon.setSpeedIV(random.nextInt(32));
        }
    }

    private int calculateHP(int baseStat, int iv, int ev, int level) {
        double numerator = (2 * baseStat + iv + ((double) ev / 4)) * level;

        return (int) (numerator / 100) + level + 10;
    }

    private int calculateStat(int baseStat, int iv, int ev, int level) {
        double numerator = (2 * baseStat + iv + ((double) ev / 4)) * level;

        return (int) (numerator / 100) + 5;
    }

    private List<PokemonMove> setMoves(Pokemon pokemon) {
        List<PokemonMove> moves = pokemonDatabase.getPokemon(pokemon.getName()).getPokemonMovesList().stream()
                .filter(move -> pokemon.getLevel() >= move.getLevelLearnedAt())
                .sorted(Comparator.comparingInt(PokemonMove::getLevelLearnedAt))
                .toList();

        int size = moves.size();

        return moves.subList(Math.max(0, size - 4), size);
    }

    public Pokemon createMyPokemon() {
        Pokemon pokemon = generatePokemon(true);
        ExperienceCalculator experienceCalculator = new ExperienceCalculator();

        pokemon.setCurrentExp(0);
        pokemon.setExpNextLevel(experienceCalculator.calculateExpNextLevel(pokemon));

        return pokemon;
    }
}
