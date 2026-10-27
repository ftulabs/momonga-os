SUMMARY = "Jetson system monitor and process viewer"
PR = "r0"
HOMEPAGE = "https://github.com/rbonghi/jetson_stats"
LICENSE = "AGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8763b57f0092c337eb12c354870a324a"

PYPI_PACKAGE = "jetson_stats"

SRC_URI[sha256sum] = "78dd111431408e156b73a80d7dc9355f41aa59b5f487d99a52f4fa55eae8ebda"

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
