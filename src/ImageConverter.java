package src;
import java.awt.image.BufferedImage;

public class ImageConverter {
    public static int[][][] toRGBArr(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] rgbArr = new int[height][width][3];
    
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
    
                rgbArr[y][x][0] = r;
                rgbArr[y][x][1] = g;
                rgbArr[y][x][2] = b;
            }
        }
    
        return rgbArr;
    }
}
