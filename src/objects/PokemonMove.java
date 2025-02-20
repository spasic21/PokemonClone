package objects;

import framework.enums.Type;

public class PokemonMove {

    public enum MoveCategory {
        Physical,
        Special,
        Status
    }

    private String name;
    private String description;
    private Type type;
    private int damage;
    private int accuracy;

    private int maxPowerPoints;

    private int currentPowerPoints;

    private MoveCategory moveCategory;

    public PokemonMove() {}

    public PokemonMove(String name, Type type, int damage, int accuracy, int currentPowerPoints, int maxPowerPoints, MoveCategory moveCategory) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.accuracy = accuracy;
        this.currentPowerPoints = currentPowerPoints;
        this.maxPowerPoints = maxPowerPoints;
        this.moveCategory = moveCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getCurrentPowerPoints() {
        return currentPowerPoints;
    }

    public void setCurrentPowerPoints(int currentPowerPoints) {
        this.currentPowerPoints = currentPowerPoints;
    }

    public int getMaxPowerPoints() {
        return maxPowerPoints;
    }

    public void setMaxPowerPoints(int maxPowerPoints) {
        this.maxPowerPoints = maxPowerPoints;
    }

    public MoveCategory getMoveCategory() {
        return moveCategory;
    }

    public void setMoveCategory(MoveCategory moveCategory) {
        this.moveCategory = moveCategory;
    }

    public void moveEffect(Pokemon playerPokemon, Pokemon trainerPokemon) {}
}
