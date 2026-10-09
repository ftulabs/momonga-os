SUMMARY = "A cat clone with syntax highlighting and Git integration"
PR = "r1"
HOMEPAGE = "https://github.com/sharkdp/bat"
LICENSE = "Apache-2.0 & MIT"
LIC_FILES_CHKSUM = " \
    file://${S}/LICENSE-APACHE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://${S}/LICENSE-MIT;md5=c46eaa1315aaa0c727a29b157ad9170a \
"

SRC_URI = "https://github.com/sharkdp/bat/releases/download/v${PV}/bat-v${PV}-aarch64-unknown-linux-gnu.tar.gz;name=arm64"
SRC_URI[arm64.sha256sum] = "422eb73e11c854fddd99f5ca8461c2f1d6e6dce0a2a8c3d5daade5ffcb6564aa"

S = "${WORKDIR}/bat-v${PV}-aarch64-unknown-linux-gnu"

COMPATIBLE_HOST = "aarch64.*-linux"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/bat ${D}${bindir}/bat
    install -D -m 0644 ${S}/bat.1 ${D}${mandir}/man1/bat.1
    install -D -m 0644 ${S}/LICENSE-APACHE ${D}${datadir}/licenses/${PN}/LICENSE-APACHE
    install -D -m 0644 ${S}/LICENSE-MIT ${D}${datadir}/licenses/${PN}/LICENSE-MIT
    install -D -m 0644 ${S}/autocomplete/bat.bash ${D}${datadir}/bash-completion/completions/bat
    install -D -m 0644 ${S}/autocomplete/bat.fish ${D}${datadir}/fish/vendor_completions.d/bat.fish
    install -D -m 0644 ${S}/autocomplete/bat.zsh ${D}${datadir}/zsh/site-functions/_bat
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += " \
    ${datadir}/licenses/${PN}/LICENSE-APACHE \
    ${datadir}/licenses/${PN}/LICENSE-MIT \
    ${datadir}/bash-completion/completions/bat \
    ${datadir}/fish/vendor_completions.d/bat.fish \
    ${datadir}/zsh/site-functions/_bat \
"
