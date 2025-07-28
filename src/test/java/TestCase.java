/*
 * Copyright (c) 2025 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.traneptora.jxlatte.JXLDecoder;
import com.traneptora.jxlatte.JXLImage;
import com.traneptora.jxlatte.io.PNGWriter;
import vavi.imageio.jxlatte.JxlatteImageReader;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2025-07-28 nsano initial version <br>
 */
@PropsEntity(url = "file:local.properties")
public class TestCase {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "file")
    String file = "src/test/resources/samples/ants.jxl";

    @BeforeEach
    void setup() throws IOException {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    @DisplayName("prototype")
    void test1() throws Exception {
        InputStream input = Files.newInputStream(Path.of(file));
        Path out = Path.of("tmp", "out.png");
        OutputStream output = Files.newOutputStream(out);
        if (!Files.exists(out.getParent())) Files.createDirectories(out.getParent());
        JXLDecoder decoder = new JXLDecoder(input);
        JXLImage image = decoder.decode();
Debug.printf("%d x %d", image.getWidth(), image.getHeight());
        PNGWriter writer = new PNGWriter(image);
        writer.write(output);
    }

    /** using cdl cause junit stops awt thread suddenly */
    private void show(BufferedImage image) throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { cdl.countDown(); }
        });
        JPanel panel = new JPanel() {
            @Override public void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        frame.setContentPane(new JScrollPane(panel));
        frame.setTitle("JPEG XL (jxlatte)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        cdl.await();
    }

    @Test
    @DisplayName("spi specified")
    void test02() throws Exception {
        ImageReader ir = ImageIO.getImageReadersByFormatName("jpegxl").next();
        assertInstanceOf(JxlatteImageReader.class, ir);
        ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(Paths.get(file)));
        ir.setInput(iis);
        BufferedImage image = ir.read(0);
        assertNotNull(image);
    }

    @Test
    @DisplayName("spi auto")
    void test03() throws Exception {
        BufferedImage image = ImageIO.read(new File(file));
        assertNotNull(image);
    }

    @Test
    @DisplayName("spi specified gui")
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test2() throws Exception {
long t = System.currentTimeMillis();
        ImageReader ir = ImageIO.getImageReadersByFormatName("jpegxl").next();
        ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(Paths.get(file)));
        ir.setInput(iis);
        BufferedImage image = ir.read(0);
Debug.println((System.currentTimeMillis() - t) + " ms");

        show(image);
    }

    @Test
    @DisplayName("spi auto gui")
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test3() throws Exception {
long t = System.currentTimeMillis();
        BufferedImage image = ImageIO.read(new File(file));
Debug.println((System.currentTimeMillis() - t) + " ms");

        show(image);
    }
}
