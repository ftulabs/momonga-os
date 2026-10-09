SUMMARY = "NVIDIA TensorRT inference debugging and prototyping toolkit"
PR = "r1"
HOMEPAGE = "https://github.com/NVIDIA/TensorRT/tree/release/8.5/tools/Polygraphy"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI = "git://github.com/NVIDIA/TensorRT.git;protocol=https;branch=release/8.5"
SRCREV = "ad932f72126f875392a4336d9ee45b2756d934a0"

S = "${WORKDIR}/git/tools/Polygraphy"

inherit setuptools3

# Include allarch runtime dependencies when generating SPDX relationship data.
SPDX_MULTILIB_SSTATE_ARCHS:append = " all"

RDEPENDS:${PN} += " \
    python3-numpy \
    python3-onnx \
    python3-onnx-graphsurgeon \
    python3-tensorrt \
"
