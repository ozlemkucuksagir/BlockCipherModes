package org.example;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

    public static void encryptImageECB(String inputPath, String outputPath, SecretKey key) throws Exception {
        BufferedImage originalImage = ImageIO.read(new File(inputPath));
        BufferedImage grayscaleImage = convertToGrayscale(originalImage);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = grayscaleImage.getRGB(x, y);
                byte[] pixelBytes = intToBytes(pixel);
                byte[] encryptedBytes = cipher.doFinal(pixelBytes);
                int encryptedPixel = bytesToInt(encryptedBytes);
                grayscaleImage.setRGB(x, y, encryptedPixel);
            }
        }

        ImageIO.write(grayscaleImage, "png", new File(outputPath));
    }

    public static void encryptImageCBC(String inputPath, String outputPath, SecretKey key, IvParameterSpec iv)
            throws Exception {
        BufferedImage originalImage = ImageIO.read(new File(inputPath));
        BufferedImage grayscaleImage = convertToGrayscale(originalImage);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = grayscaleImage.getRGB(x, y);
                byte[] pixelBytes = intToBytes(pixel);
                byte[] encryptedBytes = cipher.doFinal(pixelBytes);
                int encryptedPixel = bytesToInt(encryptedBytes);
                grayscaleImage.setRGB(x, y, encryptedPixel);
            }
        }

        ImageIO.write(grayscaleImage, "png", new File(outputPath));
    }

    public static void main(String[] args) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();

        byte[] ivBytes = new byte[16];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        String inputImagePath = "HW.jpg";
        String ecbOutputPath = "encrypted_img_ecb.png";
        String cbcOutputPath = "encrypted_img_cbc.png";

        encryptImageECB(inputImagePath, ecbOutputPath, key);
        encryptImageCBC(inputImagePath, cbcOutputPath, key, iv);
    }

    private static BufferedImage convertToGrayscale(BufferedImage colorImage) {
        BufferedImage grayscaleImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscaleImage.getGraphics();
        g.drawImage(colorImage, 0, 0, null);
        g.dispose();
        return grayscaleImage;
    }

    private static byte[] intToBytes(int value) {
        return new byte[]{(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
    }

    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | bytes[3];
    }
}
