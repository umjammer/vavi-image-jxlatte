[![Release](https://jitpack.io/v/umjammer/vavi-image-jxlatte.svg)](https://jitpack.io/#umjammer/vavi-image-jxlatte)
[![Java CI](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-image-jxlatte

<img src="https://upload.wikimedia.org/wikipedia/commons/0/06/JPEG_XL_logo.svg" width="120" alt="jpeg-xl logo"/>&nbsp;&nbsp;<sub>© <a href="https://jpeg.org/jpegxl/">JPEG</a></sub>

Pure java JPEG XL Decoder Java ImageIO SPI Plugin powered by [jxlatte](https://github.com/Traneptora/jxlatte)

## Install

 * [maven](https://jitpack.io/#umjammer/vavi-image-jxlatte)

## Usage

```java
    BufferedImage image = ImageIO.read(Paths.get("/foo/bar.jxl").toFile());
```

## References

 * [original](https://github.com/Traneptora/jxlatte)

## TODO

 * ~~something wrong~~ -> alpha detection, index and bitDepth
   * ~~blendmodes_5.jxl~~
   * ~~wb-rainbow.jxl~~ 

---

# [Original](https://github.com/Traneptora/jxlatte)

## Features

Supported features:

- All static Modular images
  - All lossless images
  - All JXL Art
  - All Lossy Modular images
- All static VarDCT Images
  - All JPEG reconstructions
  - Other static VarDCT images
  - Varblock Visualization
- Output:
  - PNG
    - SDR
    - HDR
  - PFM

Features not yet supported at this time:

- Progressive Decoding
- Region-of-interest Decoding
- Animation
