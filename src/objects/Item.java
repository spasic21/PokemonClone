package objects;

import framework.enums.ItemCategory;

import java.awt.image.BufferedImage;

public class Item {

    private String name, description;

    private int count;

    private BufferedImage image;

    private final ItemCategory category;

    public Item(String name, String description, int count, BufferedImage image, ItemCategory category) {
        this.name = name;
        this.description = description;
        this.count = count;
        this.image = image;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public BufferedImage getImage() {
        return image;
    }

    public ItemCategory getCategory() {
        return category;
    }
}
