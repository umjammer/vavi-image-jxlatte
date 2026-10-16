[![Release](https://jitpack.io/v/umjammer/vavi-image-jxlatte.svg)](https://jitpack.io/#umjammer/vavi-image-jxlatte)
[![Java CI](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-image-jxlatte/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-image-jxlatte

<img src="src/test/resources/duke_jpegxl.png" width="160" alt="jpeg-xl logo"/>

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
 * https://github.com/Traneptora/jxlatte/pull/28 is done then remove the code and let it as a dependency 

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

---

<sub>image designed by @umjammer, drawn by nano banana</sub>
