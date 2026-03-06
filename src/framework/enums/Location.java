package framework.enums;

public enum Location {
    World("/sounds/azalea_town_soulsilver.wav"),
    PlayerHouse("/sounds/azalea_town_soulsilver.wav"),
    PokemonCenter("/sounds/pokemon_center_soulsilver.wav"),
    PokeMart("/sounds/poke_mart_soulsilver.wav");

    private final String musicPath;

    Location(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getMusicPath() {
        return musicPath;
    }
}
