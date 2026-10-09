SUMMARY = "NVIDIA ONNX graph creation and transformation tools"
HOMEPAGE = "https://github.com/NVIDIA/TensorRT/tree/release/8.5/tools/onnx-graphsurgeon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

WHL_NAME = "onnx_graphsurgeon-${PV}-py2.py3-none-any.whl"
SRC_URI = "https://files.pythonhosted.org/packages/71/53/98334c4f64a9e289a8cb48f5e7966b8ff015414d0bf26587cf46d764f1d8/${WHL_NAME}"
SRC_URI[sha256sum] = "6f611ea29a8e4740fbab1aae52bf4c40b8b9918f8459058d20b99acc79fce121"

inherit python3-dir python3native

PACKAGE_ARCH = "all"

DEPENDS += "python3-native"
RDEPENDS:${PN} += "python3-numpy python3-onnx"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    ${PYTHON} -c 'import zipfile; zipfile.ZipFile("${WORKDIR}/${WHL_NAME}").extractall("${D}${PYTHON_SITEPACKAGES_DIR}")'
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/onnx_graphsurgeon*"
