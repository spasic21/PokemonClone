package framework;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoadImages {

    private List<BufferedImage> images;

    public LoadImages(String packageName) {
        List<File> imageFiles = getImagesFromFolder(packageName);

        images = new ArrayList<>();

        for(File imageFile : imageFiles) {
            try {
                BufferedImage image = ImageIO.read(imageFile);
                images.add(image);
            } catch (IOException e) {
                System.err.println("Error reading image: " + imageFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    private List<File> getImagesFromFolder(String folderPath) {
        try {
            return Files.list(Path.of(folderPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isImageFile(path.toString()))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading folder: " + folderPath);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean isImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();

        return lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".gif");
    }

    public List<BufferedImage> getImages() {
        return images;
    }
}
