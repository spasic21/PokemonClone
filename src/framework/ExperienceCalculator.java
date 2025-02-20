package framework;

import objects.Pokemon;

public class ExperienceCalculator {

    private boolean isWild = false;
    private boolean holdingLuckyEgg = false;
    private boolean isFriendly = false;
    private boolean usingExpPower = false;
    private boolean usingExpShare = false;
    private boolean isForeign = false;
    private boolean goingToEvolve = false;

    public ExperienceCalculator() {
    }

    /**
     * This function uses Gen 6 experience equation to calculate experience gained.
     *
     * @return
     */
    public int calculateExp(Pokemon defender) {
        float wildPokemonValue = 1f;

        if (!isWild) {
            wildPokemonValue = 1.5f;
        }

        float baseExpYield = defender.getExpYield();

        float luckyEggValue = 1;

        if (holdingLuckyEgg) {
            luckyEggValue = 1.5f;
        }

        float friendshipValue = 1;

        if (isFriendly) {
            friendshipValue = (float) 4915 / 4096;
        }

        int defenderLevel = defender.getLevel();

        float expPower = 1;

        if (usingExpPower) {
            expPower = 1.5f;
        }

        float expShareValue = 1f;

        if (usingExpShare) {
            expShareValue = 2f;
        }

        float foreignValue = 1;

        if (isForeign) {
            foreignValue = (float) 6963 / 4096;
        }

        float evolveValue = 1;

        if (goingToEvolve) {
            evolveValue = (float) 4915 / 4096;
        }

        float exp = ((baseExpYield * defenderLevel) / 7) * wildPokemonValue * (1 / expShareValue) * foreignValue * luckyEggValue * evolveValue * friendshipValue * expPower;

        return Math.round(exp);
    }

    public int calculateExpNextLevel(Pokemon pokemon) {
        switch (pokemon.getExpType()) {
            case Erratic -> {
                return calculateErratic(pokemon.getLevel() + 1) - calculateErratic(pokemon.getLevel());
            }
            case Fast -> {
                return calculateFast(pokemon.getLevel() + 1) - calculateFast(pokemon.getLevel());
            }
            case MediumFast -> {
                return calculateMediumFast(pokemon.getLevel() + 1) - calculateMediumFast(pokemon.getLevel());
            }
            case MediumSlow -> {
                return calculateMediumSlow(pokemon.getLevel() + 1) - calculateMediumSlow(pokemon.getLevel());
            }
            case Slow -> {
                return calculateSlow(pokemon.getLevel() + 1) - calculateSlow(pokemon.getLevel());
            }
            default -> {
                return calculateFluctuating(pokemon.getLevel() + 1) - calculateFluctuating(pokemon.getLevel());
            }
        }
    }

    public int calculateErratic(int level) {
        if (level == 1) {
            return 0;
        } else if (level < 50) {
            return (int) (Math.pow(level, 3) * (100 - level)) / 50;
        } else if (level < 68) {
            return (int) (Math.pow(level, 3) * (150 - level)) / 100;
        } else if (level < 98) {
            return (int) ((Math.pow(level, 3) * ((1911 - (10 * level)) / 3) / 500));
        } else {
            return (int) (Math.pow(level, 3) * (160 - level)) / 100;
        }
    }

    public int calculateFast(int level) {
        if (level == 1) {
            return 0;
        } else {
            return (int) ((4 * Math.pow(level, 3)) / 5);
        }
    }

    public int calculateMediumFast(int level) {
        if (level == 1) {
            return 0;
        } else {
            return (int) Math.pow(level, 3);
        }
    }

    public int calculateMediumSlow(int level) {
        if (level == 1) {
            return 0;
        } else {
            double calculatedExp = ((6 * Math.pow(level, 3)) / 5) - (15 * Math.pow(level, 2)) + (100 * level) - 140;

            return (int) Math.floor(calculatedExp);
        }
    }

    public int calculateSlow(int level) {
        if (level == 1) {
            return 0;
        } else {
            return (int) (5 * Math.pow(level, 3)) / 4;
        }
    }

    public int calculateFluctuating(int level) {
        if (level == 1) {
            return 0;
        } else if (level < 15) {
            double numerator = ((double) (level + 1) / 3) + 24;
            return (int) (Math.pow(level, 3) * numerator) / 50;
        } else if (level < 36) {
            return (int) ((Math.pow(level, 3) * (level + 14)) / 50);
        } else {
            double numerator = ((double) (level) / 2) + 32;
            return (int) (Math.pow(level, 3) * numerator) / 50;
        }
    }
}
