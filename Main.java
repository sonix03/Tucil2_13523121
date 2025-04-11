import src.InputImage;
import src.SaveImage;
import src.ErrorCalculating;
import src.CompressionPercentage;
import src.QuadtreeCompression;
import src.ImageConverter;
import src.GifSequenceWriter;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Scanner;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input path gambar
        System.out.print("Masukkan alamat absolut gambar yang akan dikompresi: ");
        String inputString = "C:/Documents/a_semester_4/Stima/Lanjut2/Tucil2_13523121/test/input/"; // jangan lupa buat ganti ini
        String inputPath = scanner.nextLine();
        
        // Baca gambar
        BufferedImage image = InputImage.readImage(inputString + inputPath);
        if (image == null) {
            System.out.println("Gagal baca gambar");
            scanner.close();
            return;
        }

        // Konversi gambar menjadi array 2D
        int[][][] oriArr = ImageConverter.toRGBArr(image);
        

        // Milih perhitungan error
        int method;
        do {
            System.out.println("Metode: ");
            System.out.println("1. Variance");
            System.out.println("2. Mean Absolute Deviation");
            System.out.println("3. Max Pixel Difference");
            System.out.println("4. Entropy");
            System.out.println("5. Structural Similarity Index");
            System.out.print("Pilih metode perhitungan error: ");
            method = scanner.nextInt();
        } while (method < 1 || method > 5);

        // treshol
        System.out.print("Masukkan Threshold: ");
        double threshold = scanner.nextDouble();

        // blok minimum
        System.out.print("Masukkan ukuran blok minimum: ");
        int minBlockSize = scanner.nextInt();

        // persentase kompresi
        double targetCompression;
        do {
            System.out.print("Masukkan target kompresi (0.0 - 1.0): ");
            targetCompression = scanner.nextDouble();
            if (targetCompression < 0.0 || targetCompression > 1.0) {
                System.out.println("Error: Nilai harus antara 0.0 hingga 1.0.");
            }
        } while (targetCompression < 0.0 || targetCompression > 1.0);

        // Output path 
        scanner.nextLine();

        System.out.print("Masukkan alamat absolut gambar hasil kompresi: ");
        String outputString = "C:/Documents/a_semester_4/Stima/Lanjut2/Tucil2_13523121/test/output/"; // Jangan lupa buat ganti ini
        String outputPath = scanner.nextLine();

        // kompresi quadtree
        System.out.println("kompresi gambar......");
        QuadtreeCompression compressor = new QuadtreeCompression(image, threshold, minBlockSize, targetCompression);
        BufferedImage compressedImage = compressor.compress();
        List<BufferedImage> framesGIF = compressor.compressAndCaptureFrames();
        
        // Cek 
        if (compressedImage == null) {
            System.out.println("Kompresi gagal.");
            scanner.close();
            return;
        }

        // Simpan Image
        SaveImage.writeImage(compressedImage, outputString + outputPath);

        // Simpan GIF 
        System.out.print("Masukkan nama file GIF (kosongin untuk skip): ");
        String gifName = scanner.nextLine();

        if (!gifName.isEmpty()) {
            try {
                if (framesGIF.isEmpty()) {
                    System.out.println("gaada frame untuk GIF.");
                } else {
                    String gifPath = outputString + gifName;
                    File gifFile = new File(gifPath);
                    ImageOutputStream output = new FileImageOutputStream(gifFile);
                    GifSequenceWriter gifWriter = new GifSequenceWriter(output, framesGIF.get(0).getType(), 600, true);
                    
                    // mencari frame dari yang kasar menjadi detail
                    for (int i = framesGIF.size() - 1; i >= 0; i--) {
                        gifWriter.writeToSequence(framesGIF.get(i));
                    }
                    gifWriter.close();
                    System.out.println("GIF animasi berhasil disimpan di: " + gifPath);
                }
            } catch (Exception e) {
                System.out.println("error saat menyimpan GIF:");
                e.printStackTrace();
            }
        }
        

        // Konversi gambar hasil kompresi menjadi array 2D
        int[][][] compressedArr = ImageConverter.toRGBArr(compressedImage);
        if (compressedArr == null) {
            System.out.println("Konversi gambar hasil kompresi gagal.");
            scanner.close();
            return;
        }
        System.out.println("berhasil!!!");

        // Hitung error setelah kompresi
        double errorAfter = ErrorCalculating.calculateError(method, oriArr, compressedArr);

        // Hitung informasi output
        long oriSize = new File(inputString + inputPath).length();
        long compressedSize = new File(outputString + outputPath).length();
        double compressionPercentage = CompressionPercentage.calculateCompression(oriSize, compressedSize);

        // Output hasil
        System.out.println("\n--- Hasil Kompresi ---");
        System.out.println("Waktu eksekusi: " + compressor.getExecutionTime() + " ms");
        System.out.println("Ukuran gambar sebelum: " + oriSize + " bytes");
        System.out.println("Ukuran gambar setelah: " + compressedSize + " bytes");
        System.out.println("Persentase kompresi: " + compressionPercentage + "%");
        System.out.println("Kedalaman pohon: " + compressor.getTreeDepth());
        System.out.println("Banyak simpul pada pohon: " + compressor.getNodeCount());
        System.out.println("Nilai error setelah kompresi: " + errorAfter);
        System.out.println("Gambar hasil kompresi disimpan di: " + outputPath);


        scanner.close();
    }
}
