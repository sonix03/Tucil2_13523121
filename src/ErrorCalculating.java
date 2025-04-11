package src;
import java.lang.Math;

public class ErrorCalculating {
    public static double calculateError(int method, int[][][] ori, int[][][] compressed) {
        switch (method) {
            case 1: return variance(ori, compressed);
            case 2: return meanAbsoluteDeviation(ori, compressed);
            case 3: return maxPixelDifference(ori, compressed);
            case 4: return entropy(ori);
            case 5: return structuralSimilarityIndex(ori, compressed);
            default:
                throw new IllegalArgumentException("Metode error tidak valid.");
        }
    }

    private static double variance(int[][][] ori, int[][][] compressed) {
        double sumR = 0, sumG = 0, sumB = 0;
        int height = ori.length;
        int width = ori[0].length;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sumR += Math.pow(ori[i][j][0] - compressed[i][j][0], 2);
                sumG += Math.pow(ori[i][j][1] - compressed[i][j][1], 2);
                sumB += Math.pow(ori[i][j][2] - compressed[i][j][2], 2);
            }
        }

        int count = height * width;
        return (sumR + sumG + sumB) / (3 * count);
    }

    private static double meanAbsoluteDeviation(int[][][] ori, int[][][] compressed) {
        double sumR = 0, sumG = 0, sumB = 0;
        int height = ori.length;
        int width = ori[0].length;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sumR += Math.abs(ori[i][j][0] - compressed[i][j][0]);
                sumG += Math.abs(ori[i][j][1] - compressed[i][j][1]);
                sumB += Math.abs(ori[i][j][2] - compressed[i][j][2]);
            }
        }

        int count = height * width;
        return (sumR + sumG + sumB) / (3 * count);
    }

    private static double maxPixelDifference(int[][][] ori, int[][][] compressed) {
        int maxR = 0, maxG = 0, maxB = 0;
        int height = ori.length;
        int width = ori[0].length;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maxR = Math.max(maxR, Math.abs(ori[i][j][0] - compressed[i][j][0]));
                maxG = Math.max(maxG, Math.abs(ori[i][j][1] - compressed[i][j][1]));
                maxB = Math.max(maxB, Math.abs(ori[i][j][2] - compressed[i][j][2]));
            }
        }

        return (maxR + maxG + maxB) / 3.0;
    }

    private static double entropy(int[][][] ori) {
        double entropyR = 0, entropyG = 0, entropyB = 0;
        int[][] hist = new int[3][256];
        int height = ori.length;
        int width = ori[0].length;
        int pixelCount = height * width;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                hist[0][ori[i][j][0]]++;
                hist[1][ori[i][j][1]]++;
                hist[2][ori[i][j][2]]++;
            }
        }

        for (int i = 0; i < 256; i++) {
            if (hist[0][i] > 0) {
                double prob = (double) hist[0][i] / pixelCount;
                entropyR -= prob * (Math.log(prob) / Math.log(2));
            }
            if (hist[1][i] > 0) {
                double prob = (double) hist[1][i] / pixelCount;
                entropyG -= prob * (Math.log(prob) / Math.log(2));
            }
            if (hist[2][i] > 0) {
                double prob = (double) hist[2][i] / pixelCount;
                entropyB -= prob * (Math.log(prob) / Math.log(2));
            }
        }
        return (entropyR + entropyG + entropyB) / 3.0;
    }

    private static double structuralSimilarityIndex(int[][][] ori, int[][][] compressed) {
        double sumR = 0, sumG = 0, sumB = 0;
        int height = ori.length;
        int width = ori[0].length;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int r1 = ori[i][j][0], r2 = compressed[i][j][0];
                int g1 = ori[i][j][1], g2 = compressed[i][j][1];
                int b1 = ori[i][j][2], b2 = compressed[i][j][2];

                sumR += (2.0 * r1 * r2 + 1) / (r1 * r1 + r2 * r2 + 1);
                sumG += (2.0 * g1 * g2 + 1) / (g1 * g1 + g2 * g2 + 1);
                sumB += (2.0 * b1 * b2 + 1) / (b1 * b1 + b2 * b2 + 1);
            }
        }

        int count = height * width;
        return (sumR + sumG + sumB) / (3.0 * count);
    }
}
