SUMMARY = "Neofetch system information tool"
DESCRIPTION = "A command-line system information tool written in Bash."
HOMEPAGE = "https://github.com/dylanaraps/neofetch"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "https://raw.githubusercontent.com/dylanaraps/neofetch/7.1.0/neofetch;name=script \
           file://0001-add-momonga-ascii-art.patch"
SRC_URI[script.sha256sum] = "3dc33493e54029fb1528251552093a9f9a2894fcf94f9c3a6f809136a42348c7"

S = "${WORKDIR}"
PACKAGE_ARCH = "all"
RDEPENDS:${PN} = "bash coreutils grep sed gawk"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/neofetch ${D}${bindir}/neofetch
}

FILES:${PN} = "${bindir}/neofetch"
