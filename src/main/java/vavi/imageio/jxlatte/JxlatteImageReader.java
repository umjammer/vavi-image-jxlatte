/*
 * Copyright (c) 2025 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.imageio.jxlatte;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Deflater;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import com.traneptora.jxlatte.JXLDecoder;
import com.traneptora.jxlatte.JXLImage;
import com.traneptora.jxlatte.JXLOptions;
import com.traneptora.jxlatte.color.CIEPrimaries;
import com.traneptora.jxlatte.color.CIEXY;
import com.traneptora.jxlatte.color.ColorFlags;
import com.traneptora.jxlatte.color.ColorManagement;
import com.traneptora.jxlatte.util.ImageBuffer;
import vavi.imageio.WrappedImageInputStream;

import static java.lang.System.getLogger;


/**
 * JxlatteImageReader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2025-07-28 umjammer initial version <br>
 */
public class JxlatteImageReader extends ImageReader {

    private static final Logger logger = getLogger(JxlatteImageReader.class.getName());

    /** */
    private BufferedImage image;

    /** */
    public JxlatteImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    @Override
    public int getNumImages(boolean allowSearch) throws IIOException {
        return 1;
    }

    /** */
    private void checkIndex(int imageIndex) {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException("bad index");
        }
    }

    @Override
    public int getWidth(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        return image.getWidth();
    }

    @Override
    public int getHeight(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        return image.getHeight();
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IIOException {

logger.log(Level.DEBUG, "decode start");
long t = System.currentTimeMillis();
        InputStream stream = new WrappedImageInputStream((ImageInputStream) input);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[8192];
            while (true) {
                int r = stream.read(b, 0, b.length);
                if (r < 0) break;
                baos.write(b, 0, r);
            }
            int l = baos.size();
logger.log(Level.DEBUG, "size: " + l);

            JXLImage jxlImage = new JXLDecoder(new ByteArrayInputStream(baos.toByteArray())).decode();
            image = toBufferedImage(jxlImage);

            return image;
        } catch (IOException e) {
            throw new IIOException(e.getMessage(), e);
} finally {
logger.log(Level.DEBUG, "time: " + (System.currentTimeMillis() - t));
        }
    }

    /** */
    private static BufferedImage toBufferedImage(JXLImage image) {
        boolean hdr = false;
        int bitDepth = hdr || image.getHeader().getBitDepthHeader().bitsPerSample > 8 ? 16 : 8;
        boolean gray = image.getColorEncoding() == ColorFlags.CE_GRAY;
        CIEPrimaries primaries = hdr ? ColorManagement.PRI_BT2100 : ColorManagement.PRI_SRGB;
        CIEXY whitePoint = ColorManagement.WP_D65;
        int tf = hdr ? ColorFlags.TF_PQ : ColorFlags.TF_SRGB;
        byte[] iccProfile = image.getICCProfile();
        image = iccProfile != null ? image : image.transform(primaries, whitePoint, tf, JXLOptions.PEAK_DETECT_AUTO);
        int maxValue = ~(~0 << bitDepth);
        int width = image.getWidth();
        int height = image.getHeight();
        int alphaIndex = image.getAlphaIndex();
        int colorChannels;
        if (gray) {
            colorChannels = 1;
        } else {
            colorChannels = 3;
        }
        boolean coerce = image.isAlphaPremultiplied();
        ImageBuffer[] buffer = image.getBuffer(false);
        if (!coerce) {
            for (int c = 0; c < buffer.length; c++) {
                if (buffer[c].isInt() && image.getTaggedBitDepth(c) != bitDepth) {
                    coerce = true;
                    break;
                }
            }
        }
        if (coerce) {
            for (int c = 0; c < buffer.length; c++) {
                buffer[c].castToFloatIfInt(~(~0 << image.getTaggedBitDepth(c)));
            }
        }
        if (image.isAlphaPremultiplied()) {
            float[][] a = buffer[alphaIndex].getFloatBuffer();
            for (int c = 0; c < colorChannels; c++) {
                float[][] buff = buffer[c].getFloatBuffer();
                for (int y = 0; y < buffer[c].height; y++) {
                    for (int x = 0; x < buffer[c].width; x++) {
                        buff[y][x] /= a[y][x];
                    }
                }
            }
        }
        for (int c = 0; c < buffer.length; c++) {
            if (buffer[c].isInt() && image.getTaggedBitDepth(c) == bitDepth) {
                buffer[c].clamp(maxValue);
            } else {
                buffer[c].castToIntIfFloat(maxValue);
            }
        }

        //
logger.log(Level.DEBUG, "%d x %d, %s, %d".formatted(width, height, image.isAlphaPremultiplied(), colorChannels));
        BufferedImage bufferedImage;
        if (image.isAlphaPremultiplied())
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        else
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int[] p = new int[colorChannels];
                for (int c = 0; c < colorChannels; c++)
                    p[c + (image.isAlphaPremultiplied() ? 1 : 0)] = buffer[c].getIntBuffer()[y][x];
                if (image.isAlphaPremultiplied()) {
                    p[0] = buffer[alphaIndex].getIntBuffer()[y][x];
                }
                bufferedImage.getRaster().setPixel(x, y, p);
            }
        return bufferedImage;
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IIOException {
        return null;
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        return null;
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        ImageTypeSpecifier specifier = new ImageTypeSpecifier(image);
        List<ImageTypeSpecifier> l = new ArrayList<>();
        l.add(specifier);
        return l.iterator();
    }
}
