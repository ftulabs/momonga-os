SUMMARY = "Command-line fuzzy finder"
HOMEPAGE = "https://github.com/junegunn/fzf"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=edd55e9a395baee52799d6fb23fef6e5"

SRC_URI = " \
    https://github.com/junegunn/fzf/releases/download/v${PV}/fzf-${PV}-linux_arm64.tar.gz;name=arm64 \
    https://raw.githubusercontent.com/junegunn/fzf/v${PV}/LICENSE;name=license;downloadfilename=LICENSE \
"
SRC_URI[arm64.sha256sum] = "5d673b849f494f0d64ec471d8640b153ca8849e3846a31da17abdcfce8df6b46"
SRC_URI[license.sha256sum] = "a296f423c0d30ce3581435e78e7e36c5fe73984a882d8720c72e713b4593588b"

S = "${WORKDIR}"

COMPATIBLE_HOST = "aarch64.*-linux"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${WORKDIR}/fzf ${D}${bindir}/fzf
    install -D -m 0644 ${WORKDIR}/LICENSE ${D}${datadir}/licenses/${PN}/LICENSE
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${datadir}/licenses/${PN}/LICENSE"
