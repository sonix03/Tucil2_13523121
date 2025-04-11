package src;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class InputImage {
    public static BufferedImage readImage(String inputPath) {
        try {
            File file = new File(inputPath);
            if (!file.exists()) {
                System.out.println("File tidak ditemukan: " + inputPath);
                return null;
            }

            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                System.out.println("Format gambar tidak didukung");
                return null;
            }

            System.out.println("bisa baca. Lebar: " + image.getWidth() + ", Tinggi: " + image.getHeight());
            return image;
        } catch (IOException e) {
            System.err.println("Error baca gambar: " + e.getMessage());
            return null;
        }
    }
}

