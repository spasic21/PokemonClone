package objects.pokemon;

import framework.enums.MoveCategory;
import framework.enums.Type;

public class PokemonMove {

    private String name;
    private Type type;
    private int damage;
    private int accuracy;
    private int currentPowerPoints;
    private int maxPowerPoints;
    private MoveCategory moveCategory;
    private int priority;
    private int levelLearnedAt;
    private String description;

    public PokemonMove(String name, Type type, int damage, int accuracy, int currentPowerPoints, int maxPowerPoints, MoveCategory moveCategory) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.accuracy = accuracy;
        this.currentPowerPoints = currentPowerPoints;
        this.maxPowerPoints = maxPowerPoints;
        this.moveCategory = moveCategory;
    }

    public PokemonMove(String name, Type type, int damage, int accuracy, int currentPowerPoints, int maxPowerPoints, MoveCategory moveCategory, int priority, int levelLearnedAt, String description) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.accuracy = accuracy;
        this.currentPowerPoints = currentPowerPoints;
        this.maxPowerPoints = maxPowerPoints;
        this.moveCategory = moveCategory;
        this.priority = priority;
        this.levelLearnedAt = levelLearnedAt;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getLevelLearnedAt() {
        return levelLearnedAt;
    }

    public void setLevelLearnedAt(int levelLearnedAt) {
        this.levelLearnedAt = levelLearnedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
