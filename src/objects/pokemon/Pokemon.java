package objects.pokemon;

import framework.enums.ExpType;
import framework.enums.Type;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {

    private String name;
    private int dexNumber;
    private int gender;
    private int height;
    private int weight;
    private Type type1;
    private Type type2;
    private String ability;
    private String nature;
    private ExpType expType;
    private int level;
    private int maxHealth;
    private int currentHealth;
    private int expNextLevel;
    private int currentExp;
    private int expYield;

    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;

    private List<PokemonBaseStat> baseStats;

    private int healthIV;
    private int attackIV;
    private int defenseIV;
    private int specialAttackIV;
    private int specialDefenseIV;
    private int speedIV;

    private int baseHappiness;

    private int captureRate;

    private List<String> eggGroups;

    private String description;

    private boolean shiny;

    private boolean fainted = false;

    private PokemonFrontSprite frontSprite;

    private PokemonBackSprite backSprite;

    private List<PokemonMove> pokemonMovesList = new ArrayList<>(4);

    public Pokemon() {
    }

    public Pokemon(String name, Type type1, int level) {
        this.name = name;
        this.type1 = type1;
        this.type2 = null;
        this.level = level;
    }

    public Pokemon(String name, Type type1, Type type2, int level) {
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public void setDexNumber(int dexNumber) {
        this.dexNumber = dexNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Type getType1() {
        return type1;
    }

    public void setType1(Type type1) {
        this.type1 = type1;
    }

    public Type getType2() {
        return type2;
    }

    public void setType2(Type type2) {
        this.type2 = type2;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getExpNextLevel() {
        return expNextLevel;
    }

    public void setExpNextLevel(int expNextLevel) {
        this.expNextLevel = expNextLevel;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    public int getExpYield() {
        return expYield;
    }

    public void setExpYield(int expYield) {
        this.expYield = expYield;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public ExpType getExpType() {
        return expType;
    }

    public void setExpType(ExpType expType) {
        this.expType = expType;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(int specialAttack) {
        this.specialAttack = specialAttack;
    }

    public int getSpecialDefense() {
        return specialDefense;
    }

    public void setSpecialDefense(int specialDefense) {
        this.specialDefense = specialDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public List<PokemonBaseStat> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(List<PokemonBaseStat> baseStats) {
        this.baseStats = baseStats;
    }

    public int getHealthIV() {
        return healthIV;
    }

    public void setHealthIV(int healthIV) {
        this.healthIV = healthIV;
    }

    public int getAttackIV() {
        return attackIV;
    }

    public void setAttackIV(int attackIV) {
        this.attackIV = attackIV;
    }

    public int getDefenseIV() {
        return defenseIV;
    }

    public void setDefenseIV(int defenseIV) {
        this.defenseIV = defenseIV;
    }

    public int getSpecialAttackIV() {
        return specialAttackIV;
    }

    public void setSpecialAttackIV(int specialAttackIV) {
        this.specialAttackIV = specialAttackIV;
    }

    public int getSpecialDefenseIV() {
        return specialDefenseIV;
    }

    public void setSpecialDefenseIV(int specialDefenseIV) {
        this.specialDefenseIV = specialDefenseIV;
    }

    public int getSpeedIV() {
        return speedIV;
    }

    public void setSpeedIV(int speedIV) {
        this.speedIV = speedIV;
    }

    public int getBaseHappiness() {
        return baseHappiness;
    }

    public void setBaseHappiness(int baseHappiness) {
        this.baseHappiness = baseHappiness;
    }

    public int getCaptureRate() {
        return captureRate;
    }

    public void setCaptureRate(int captureRate) {
        this.captureRate = captureRate;
    }

    public List<String> getEggGroups() {
        return eggGroups;
    }

    public void setEggGroups(List<String> eggGroups) {
        this.eggGroups = eggGroups;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public boolean isFainted() {
        return fainted;
    }

    public void setFainted(boolean fainted) {
        this.fainted = fainted;
    }

    public List<PokemonMove> getPokemonMovesList() {
        return pokemonMovesList;
    }

    public void setPokemonMovesList(List<PokemonMove> pokemonMovesList) {
        this.pokemonMovesList = pokemonMovesList;
    }

    public PokemonMove getMove(int index) {
        return pokemonMovesList.get(index);
    }

    public PokemonFrontSprite getFrontSprite() {
        return frontSprite;
    }

    public void setFrontSprite(PokemonFrontSprite frontSprite) {
        this.frontSprite = frontSprite;
    }

    public PokemonBackSprite getBackSprite() {
        return backSprite;
    }

    public void setBackSprite(PokemonBackSprite backSprite) {
        this.backSprite = backSprite;
    }
}
