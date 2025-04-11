package src;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class QuadtreeCompression {
    private BufferedImage image;
    private double threshold;
    private int minBlockSize;
    private double targetCompression;
    private long executionTime;
    private int treeDepth = 0;
    private int nodeCount = 0;
    private final List<BufferedImage> frames = new ArrayList<>();
    private boolean captureFrames = false;
    private BufferedImage workingImage;
    
    public QuadtreeCompression(BufferedImage image, double threshold, int minBlockSize, 
    double targetCompression) {
        this.image = image;
        this.threshold = threshold;
        this.minBlockSize = minBlockSize;
        this.targetCompression = targetCompression;
    }

    public BufferedImage compress() {
        long startTime = System.currentTimeMillis();
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage compressedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = compressedImage.createGraphics();
        
        // Mulai rekursi quadtree
        divideAndConquer(0, 0, width, height, g2d, 1);
        g2d.dispose();
        
        executionTime = System.currentTimeMillis() - startTime;
        return compressedImage;
    }

    private void divideAndConquer(int x, int y, int width, int height, Graphics2D g2d, int depth) {
        boolean reachMinBlock = (width <= minBlockSize || height <= minBlockSize);
        boolean reachTargetCompression = (targetCompression > 0 && getCurrentCompression() >= targetCompression);
    
        if (reachMinBlock || reachTargetCompression) {
            fillBlock(x, y, width, height, g2d);
            return;
        }
    
        // Langsung bagi 4 tanpa cek threshold
        int halfWidth = width / 2;
        int halfHeight = height / 2;
    
        divideAndConquer(x, y, halfWidth, halfHeight, g2d, depth + 1);                            // a
        divideAndConquer(x + halfWidth, y, width - halfWidth, halfHeight, g2d, depth + 1);        // b
        divideAndConquer(x, y + halfHeight, halfWidth, height - halfHeight, g2d, depth + 1);      // c
        divideAndConquer(x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, g2d, depth + 1); // d

        if (captureFrames) {
            frames.add(deepCopy(workingImage));
        }
        
        nodeCount += 4;
        treeDepth = Math.max(treeDepth, depth);
    }
    
    private void fillBlock(int x, int y, int width, int height, Graphics2D g2d) {
        Color avgColor = getAverageColor(x, y, width, height);
        g2d.setColor(avgColor);
        g2d.fillRect(x, y, width, height);
    
        if (captureFrames) {
            Graphics2D gWork = workingImage.createGraphics();
            gWork.setColor(avgColor);
            gWork.fillRect(x, y, width, height);
            gWork.dispose();
            frames.add(deepCopy(workingImage));
        }
    }

    private Color getAverageColor(int x, int y, int width, int height) {
        int r = 0, g = 0, b = 0, count = 0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
                count++;
            }
        }
        return new Color(r / count, g / count, b / count);
    }


    private double getCurrentCompression() {
        int countPix = image.getWidth() * image.getHeight();
        return (double) nodeCount / countPix;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    // UNTUK BACA GIF
    public List<BufferedImage> compressAndCaptureFrames() {
        BufferedImage framesImage = deepCopy(image);
        int size = 2;
        while (size <= Math.min(framesImage.getWidth(), framesImage.getHeight())) {
            BufferedImage frame = deepCopy(framesImage);
            for (int y = 0; y < frame.getHeight(); y += size) {
                for (int x = 0; x < frame.getWidth(); x += size) {
                    int blockW = Math.min(size, frame.getWidth() - x);
                    int blockH = Math.min(size, frame.getHeight() - y);
                    if (isHomogeneousRGB(frame, x, y, blockW, blockH)) {
                        Color avg = getAverageColorRGB(frame, x, y, blockW, blockH);
                        fillRegion(frame, x, y, blockW, blockH, avg);
                    }
                }
            }
            frames.add(frame);
            size *= 2;
        }
        return frames;
    }

    private boolean isHomogeneousRGB(BufferedImage image, int x, int y, int width, int height) {
        Color avg = getAverageColorRGB(image, x, y, width, height);
        double sum = 0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                double dif = Math.sqrt(Math.pow(c.getRed() - avg.getRed(), 2)
                        + Math.pow(c.getGreen() - avg.getGreen(), 2)
                        + Math.pow(c.getBlue() - avg.getBlue(), 2));
                sum += dif;
            }
        }
        double res = sum / (width * height);
        return res < threshold;
    }

    private Color getAverageColorRGB(BufferedImage image, int x, int y, int width, int height) {
        long r = 0, g = 0, b = 0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        int countPix = width * height;
        return new Color((int)(r / countPix), (int)(g / countPix), (int)(b / countPix));
    }

    private void fillRegion(BufferedImage image, int x, int y, int width, int height, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.dispose();
    }
    
    private BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage cpy = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = cpy.getGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return cpy;
    }
}