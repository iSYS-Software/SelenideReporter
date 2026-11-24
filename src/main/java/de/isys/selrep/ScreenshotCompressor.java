package de.isys.selrep;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reduces the size of screenshots. Screens made up of few colors (regular UI) are stored as a PNG
 * with a palette of 256 colors, screens with lots of colors (e. g. photos) are stored as JPEG,
 * because a palette would visibly posterize them.
 */
class ScreenshotCompressor {

    private static final int PALETTE_SIZE = 256;

    // share of pixels the palette must match exactly, otherwise the screen is considered photographic
    private static final double MIN_PALETTE_COVERAGE = 0.97;

    private static final float JPEG_QUALITY = 0.85f;

    private ScreenshotCompressor() {
    }

    /**
     * @param png a screenshot in PNG format
     * @return the compressed screenshot, which is either the overwritten PNG or a new JPEG replacing it
     */
    static File compress(File png) throws IOException {
        BufferedImage image = ImageIO.read(png);
        if (image == null) {
            throw new IOException("Not a readable image: " + png);
        }
        int width = image.getWidth(), height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[] palette = mostFrequentColors(pixels);
        if (coverage(pixels, palette) >= MIN_PALETTE_COVERAGE) {
            writePalettePng(pixels, width, height, palette, png);
            return png;
        }
        File jpg = new File(png.getParentFile(), png.getName().replaceFirst("\\.png$", "") + ".jpg");
        writeJpeg(image, jpg);
        Files.delete(png.toPath());
        return jpg;
    }

    private static int[] mostFrequentColors(int[] pixels) {
        Map<Integer, int[]> counts = new HashMap<>();
        for (int pixel : pixels) {
            counts.computeIfAbsent(pixel & 0xFFFFFF, k -> new int[1])[0]++;
        }
        List<Map.Entry<Integer, int[]>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]));
        int[] palette = new int[Math.min(PALETTE_SIZE, entries.size())];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = entries.get(i).getKey();
        }
        return palette;
    }

    private static double coverage(int[] pixels, int[] palette) {
        Set<Integer> inPalette = new HashSet<>();
        for (int color : palette) {
            inPalette.add(color);
        }
        long covered = 0;
        for (int pixel : pixels) {
            if (inPalette.contains(pixel & 0xFFFFFF)) covered++;
        }
        return (double) covered / pixels.length;
    }

    private static void writePalettePng(int[] pixels, int width, int height, int[] palette, File out) throws IOException {
        byte[] r = new byte[palette.length], g = new byte[palette.length], b = new byte[palette.length];
        for (int i = 0; i < palette.length; i++) {
            r[i] = (byte) (palette[i] >> 16);
            g[i] = (byte) (palette[i] >> 8);
            b[i] = (byte) palette[i];
        }
        IndexColorModel colorModel = new IndexColorModel(8, palette.length, r, g, b);
        BufferedImage indexed = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        byte[] data = ((DataBufferByte) indexed.getRaster().getDataBuffer()).getData();

        Map<Integer, Integer> nearestIndex = new HashMap<>();
        for (int i = 0; i < pixels.length; i++) {
            data[i] = (byte) (int) nearestIndex.computeIfAbsent(pixels[i] & 0xFFFFFF, color -> nearest(color, palette));
        }
        ImageIO.write(indexed, "png", out);
    }

    private static int nearest(int color, int[] palette) {
        int best = 0;
        long bestDistance = Long.MAX_VALUE;
        for (int i = 0; i < palette.length; i++) {
            int dr = ((color >> 16) & 0xFF) - ((palette[i] >> 16) & 0xFF);
            int dg = ((color >> 8) & 0xFF) - ((palette[i] >> 8) & 0xFF);
            int db = (color & 0xFF) - (palette[i] & 0xFF);
            // weighted by the eye's sensitivity to each channel
            long distance = 2L * dr * dr + 4L * dg * dg + 3L * db * db;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }

    private static void writeJpeg(BufferedImage image, File out) throws IOException {
        // JPEG has no alpha channel
        BufferedImage rgb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        rgb.getGraphics().drawImage(image, 0, 0, null);

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(JPEG_QUALITY);
        try (ImageOutputStream stream = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(stream);
            writer.write(null, new IIOImage(rgb, null, null), param);
        }
        finally {
            writer.dispose();
        }
    }

}
