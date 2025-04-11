package src;

public class CompressionPercentage {
    public static long calculateCompression(long ori, long compress) {
        if (ori == 0L) {
            return 0L;
        } else {
            double percent = 1.0 - (double) compress / (double) ori;
            return Math.round(percent * 100.0); // dibulatkan ke persen
        }
    }
}
