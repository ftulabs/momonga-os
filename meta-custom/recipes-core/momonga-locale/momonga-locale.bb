SUMMARY = "Momonga default UTF-8 locale"
DESCRIPTION = "Sets C.UTF-8 as the default locale and installs its glibc locale data."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch

RDEPENDS:${PN} = "locale-base-c"

do_install() {
    install -d ${D}${sysconfdir}/profile.d
    printf 'LANG=C.UTF-8\n' > ${D}${sysconfdir}/locale.conf
    printf 'export LANG=C.UTF-8\n' > ${D}${sysconfdir}/profile.d/00-locale.sh
}

FILES:${PN} = "${sysconfdir}/locale.conf ${sysconfdir}/profile.d/00-locale.sh"
