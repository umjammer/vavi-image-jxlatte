/*
 * Copyright (c) 2025 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.imageio.jxlatte;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import static java.lang.System.getLogger;


/**
 * JxlatteImageReaderSpi.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2025-07-28 umjammer initial version <br>
 */
public class JxlatteImageReaderSpi extends ImageReaderSpi {

    private static final Logger logger = getLogger(JxlatteImageReaderSpi.class.getName());

    static {
        try {
            try (InputStream is = JxlatteImageReaderSpi.class.getResourceAsStream("/META-INF/maven/vavi/vavi-image-jxlatte/pom.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    Version = props.getProperty("version", "undefined in pom.properties");
                } else {
                    Version = System.getProperty("vavi.test.version", "undefined");
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final String VendorName = "https://github.com/umjammer/vavi-image-jxlatte";
    private static final String Version;
    private static final String ReaderClassName =
        "vavi.imageio.jpegxl.JxlatteImageReader";
    private static final String[] Names = {
        "jpegxl", "JpegXL"
    };
    private static final String[] Suffixes = {
        "jxl"
    };
    private static final String[] mimeTypes = {
        "image/jpeg-xl"
    };
    static final String[] WriterSpiNames = {};
    private static final boolean SupportsStandardStreamMetadataFormat = false;
    private static final String NativeStreamMetadataFormatName = null;
    private static final String NativeStreamMetadataFormatClassName = null;
    private static final String[] ExtraStreamMetadataFormatNames = null;
    private static final String[] ExtraStreamMetadataFormatClassNames = null;
    private static final boolean SupportsStandardImageMetadataFormat = false;
    private static final String NativeImageMetadataFormatName = "jpeg-xl";
    private static final String NativeImageMetadataFormatClassName = null;
    private static final String[] ExtraImageMetadataFormatNames = null;
    private static final String[] ExtraImageMetadataFormatClassNames = null;

    /** */
    public JxlatteImageReaderSpi() {
        super(VendorName,
              Version,
              Names,
              Suffixes,
              mimeTypes,
              ReaderClassName,
              new Class[] { ImageInputStream.class },
              WriterSpiNames,
              SupportsStandardStreamMetadataFormat,
              NativeStreamMetadataFormatName,
              NativeStreamMetadataFormatClassName,
              ExtraStreamMetadataFormatNames,
              ExtraStreamMetadataFormatClassNames,
              SupportsStandardImageMetadataFormat,
              NativeImageMetadataFormatName,
              NativeImageMetadataFormatClassName,
              ExtraImageMetadataFormatNames,
              ExtraImageMetadataFormatClassNames);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Jpeg XL Image";
    }

    private static final byte[] magic1 = {(byte) 0xff, 0x0a};
    private static final byte[] magic2 = {0x00, 0x00, 0x00, 0x0c, 0x4a, 0x58, 0x4c, 0x20, 0x0d, 0x0a, (byte) 0x87, 0x0a};

    @Override
    public boolean canDecodeInput(Object obj) throws IOException {
logger.log(Level.DEBUG, "input: " + obj);
        if (obj instanceof ImageInputStream) {
            ImageInputStream stream = (ImageInputStream) obj;
            stream.mark();
            byte[] b = new byte[12];
            int l = 0;
            while (l < b.length) {
                int r = stream.read(b, l, b.length - l);
                if (r < 0) break;
                l += r;
            }
            stream.reset();
            return Arrays.equals(magic2, b) || Arrays.equals(magic1, 0, 1, b, 0, 1);
        } else {
            return false;
        }
    }

    @Override
    public ImageReader createReaderInstance(Object obj) {
        return new JxlatteImageReader(this);
    }
}
