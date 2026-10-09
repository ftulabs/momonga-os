SUMMARY = "Open Neural Network Exchange Python API"
HOMEPAGE = "https://github.com/onnx/onnx"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PYPI_PACKAGE = "onnx"
SRC_URI[sha256sum] = "b33a282b038813c4b69e73ea65c2909768e8dd6cc10619b70632335daf094646"

inherit pypi setuptools3

DEPENDS += " \
    cmake-native \
    protobuf \
    protobuf-native \
    python3-numpy \
    python3-protobuf \
    python3-protobuf-native \
"

RDEPENDS:${PN} += " \
    python3-numpy \
    python3-protobuf \
"

export ONNX_ML = "1"
export ONNX_BUILD_TESTS = "0"

export CMAKE_ARGS = " \
    -DONNX_GEN_PB_TYPE_STUBS=ON \
    -DPYTHON_INCLUDE_DIR=${STAGING_INCDIR}/python3.12 \
    -DPYTHON_LIBRARY=${STAGING_LIBDIR}/libpython3.12.so \
    -DProtobuf_INCLUDE_DIR=${STAGING_INCDIR} \
    -DProtobuf_LIBRARY=${STAGING_LIBDIR}/libprotobuf.so \
    -DProtobuf_DIR=${STAGING_LIBDIR}/cmake/protobuf \
    -Dabsl_DIR=${STAGING_LIBDIR}/cmake/absl \
"
