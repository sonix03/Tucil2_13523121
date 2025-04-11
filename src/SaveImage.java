package src;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SaveImage {
    public static void writeImage(BufferedImage image, String outputPath) {
        try {
            File output = new File(outputPath);
            ImageIO.write(image, "jpg", output);
            System.out.println("Gambar berhasil disimpan di: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error menyimpan gambar: " + e.getMessage());
        }
    }

    
}

