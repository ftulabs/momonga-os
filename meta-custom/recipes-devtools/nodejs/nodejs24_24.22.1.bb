SUMMARY = "Prebuilt Node.js 24 runtime and npm for AArch64"
PR = "r0"
HOMEPAGE = "https://nodejs.org/"
LICENSE = "MIT & ISC & BSD-2-Clause & BSD-3-Clause & Artistic-2.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=edc0683b77d2c503217642fa000b5b31"

SRC_URI = "https://nodejs.org/dist/v${PV}/node-v${PV}-linux-arm64.tar.xz;name=arm64"
SRC_URI[arm64.sha256sum] = "dbabf7a1cbf947e6059ffad2c27b80041db97dea1e553b62afdee7f13fca336e"

S = "${WORKDIR}/node-v${PV}-linux-arm64"

COMPATIBLE_HOST = "aarch64.*-linux"
RDEPENDS:${PN} += "bash"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}
    cp -a ${S}/bin ${S}/include ${S}/lib ${S}/share ${D}${prefix}/
    chown -R root:root ${D}${prefix}/bin ${D}${prefix}/include ${D}${prefix}/lib ${D}${prefix}/share
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} = "${prefix}/bin ${prefix}/include ${prefix}/lib ${prefix}/share"
