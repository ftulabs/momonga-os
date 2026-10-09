SUMMARY = "Jetson system monitor and process viewer"
PR = "r1"
HOMEPAGE = "https://github.com/rbonghi/jetson_stats"
LICENSE = "AGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8763b57f0092c337eb12c354870a324a"

PYPI_PACKAGE = "jetson_stats"

SRC_URI[sha256sum] = "b28e0eba26b96d56a610cbbfbcb5d3d2b74ce22c70d9f4d1b090f7f5f9ff44b9"

inherit pypi setuptools3 systemd useradd

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system jtop"

SYSTEMD_SERVICE:${PN} = "jtop.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

RDEPENDS:${PN} += " \
    python3-distro \
    python3-nvidia-ml-py \
    python3-smbus2 \
"

do_install:append() {
    install -D -m 0644 ${S}/services/jtop.service ${D}${systemd_system_unitdir}/jtop.service
    sed -i 's|/usr/local/bin/jtop|${bindir}/jtop|' ${D}${systemd_system_unitdir}/jtop.service
    install -D -m 0644 ${S}/scripts/jtop_env.sh ${D}${sysconfdir}/profile.d/jtop_env.sh
}

FILES:${PN} += " \
    ${sysconfdir}/profile.d/jtop_env.sh \
    ${systemd_system_unitdir}/jtop.service \
    ${datadir}/jetson_stats \
"
