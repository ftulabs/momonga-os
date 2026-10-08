SUMMARY = "Hyperextensible text editor"
HOMEPAGE = "https://neovim.io/"
LICENSE = "Apache-2.0 & Vim"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE.txt;md5=05b485c2880cb0b3d7dc27f07456ace1"

SRC_URI = " \
    https://github.com/neovim/neovim/releases/download/v${PV}/nvim-linux-arm64.tar.gz;name=arm64 \
    https://raw.githubusercontent.com/neovim/neovim/v${PV}/LICENSE.txt;name=license;downloadfilename=LICENSE.txt \
"
SRC_URI[arm64.sha256sum] = "1aa5ca085249580ae0f91eb14f27ec0919773ff2d99a163d03f3d6c21ac29725"
SRC_URI[license.sha256sum] = "de23202ef9a51f5e654034190539739a4abece5505f2ce75e780791106707011"

S = "${WORKDIR}/nvim-linux-arm64"

COMPATIBLE_HOST = "aarch64.*-linux"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}
    cp -a ${S}/bin ${S}/lib ${S}/share ${D}${prefix}/
    chown -R root:root ${D}${prefix}/bin ${D}${prefix}/lib ${D}${prefix}/share
    rm -rf ${D}${datadir}/applications ${D}${datadir}/icons
    install -D -m 0644 ${WORKDIR}/LICENSE.txt ${D}${datadir}/licenses/${PN}/LICENSE.txt
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} = "${prefix}/bin ${prefix}/lib ${prefix}/share"
