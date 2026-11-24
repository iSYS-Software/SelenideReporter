package de.isys.selrep;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScreenshotCompressorTest {

    @TempDir
    Path tempDir;

    @Test
    public void uiScreenIsStoredAsPalettePng() throws IOException {
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 400, 300);
        g.setColor(new Color(0x1676F3));
        g.fillRect(20, 20, 200, 40);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Hello World", 30, 100);
        g.dispose();
        File png = write(image, "ui.png");

        File compressed = ScreenshotCompressor.compress(png);

        assertEquals(png, compressed);
        BufferedImage result = ImageIO.read(compressed);
        assertInstanceOf(IndexColorModel.class, result.getColorModel());
        assertEquals(image.getRGB(30, 30), result.getRGB(30, 30));
        assertEquals(image.getRGB(300, 200), result.getRGB(300, 200));
    }

    @Test
    public void photographicScreenIsStoredAsJpeg() throws IOException {
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Random random = new Random(42);
        for (int x = 0; x < 400; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(x, y, random.nextInt(0x1000000));
            }
        }
        File png = write(image, "photo.png");

        File compressed = ScreenshotCompressor.compress(png);

        assertEquals("photo.jpg", compressed.getName());
        assertTrue(compressed.exists());
        assertFalse(png.exists());
        assertEquals(400, ImageIO.read(compressed).getWidth());
    }

    private File write(BufferedImage image, String name) throws IOException {
        File file = tempDir.resolve(name).toFile();
        ImageIO.write(image, "png", file);
        return file;
    }

}
