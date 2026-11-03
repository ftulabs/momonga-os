SUMMARY = "Cloudflare Tunnel client"
HOMEPAGE = "https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/"
LICENSE = "CLOSED"

SRC_URI = "https://github.com/cloudflare/cloudflared/releases/download/${PV}/cloudflared-linux-arm64;name=cloudflared"
SRC_URI[cloudflared.sha256sum] = "3df43aedc9ff291d1735b5cb576cb97e0b157488796db2e32b28aecabb445775"

S = "${WORKDIR}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "already-stripped"

do_compile() {
    :
}

do_install() {
    install -D -m 0755 ${WORKDIR}/cloudflared-linux-arm64 ${D}${bindir}/cloudflared
}

FILES:${PN} = "${bindir}/cloudflared"
COMPATIBLE_HOST = "aarch64.*-linux"
