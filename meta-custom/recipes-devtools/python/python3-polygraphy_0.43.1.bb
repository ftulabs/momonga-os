SUMMARY = "NVIDIA TensorRT inference debugging and prototyping toolkit"
HOMEPAGE = "https://github.com/NVIDIA/TensorRT/tree/release/8.5/tools/Polygraphy"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI = "git://github.com/NVIDIA/TensorRT.git;protocol=https;branch=release/8.5"
SRCREV = "ad932f72126f875392a4336d9ee45b2756d934a0"

S = "${WORKDIR}/git/tools/Polygraphy"

inherit setuptools3

RDEPENDS:${PN} += "python3-numpy python3-tensorrt"
