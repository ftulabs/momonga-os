SUMMARY = "Cloudflare Tunnel client"
HOMEPAGE = "https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/"
LICENSE = "CLOSED"

SRC_URI = "https://github.com/cloudflare/cloudflared/releases/download/${PV}/cloudflared-linux-arm64;name=cloudflared"
SRC_URI[cloudflared.sha256sum] = "2b7203b4fb0ba28a0c1e12f8855ce2c25f76d266a8d5edafac5540b6812acff6"

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
