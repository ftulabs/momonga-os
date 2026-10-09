SUMMARY = "Use any Linux distribution inside the terminal, in a container"
DESCRIPTION = "Shell wrappers that create and enter containers sharing the \
user's home, devices and display. Momonga ships Docker, not Podman, so the \
system config selects the docker backend and gives box users their host \
groups."
HOMEPAGE = "https://github.com/89luca89/distrobox"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=b8925bd214abfe93f6f34e5f6b721ae7"

SRC_URI = "git://github.com/89luca89/distrobox.git;protocol=https;branch=main \
           file://distrobox.conf \
           file://host-groups.sh"
# Tag 1.8.2.5
SRCREV = "40c3cd724faa434aeb0a23e28776665b92de68bd"

S = "${WORKDIR}/git"
inherit allarch

RDEPENDS:${PN} = "docker-moby"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir} ${D}${mandir}/man1 \
        ${D}${datadir}/bash-completion/completions ${D}${datadir}/zsh/site-functions
    install -m 0755 ${S}/distrobox* ${D}${bindir}/
    install -m 0644 ${S}/man/man1/* ${D}${mandir}/man1/
    install -m 0644 ${S}/completions/bash/* ${D}${datadir}/bash-completion/completions/
    install -m 0644 ${S}/completions/zsh/* ${D}${datadir}/zsh/site-functions/
    install -D -m 0644 ${WORKDIR}/distrobox.conf ${D}${sysconfdir}/distrobox/distrobox.conf
    install -m 0755 ${WORKDIR}/host-groups.sh ${D}${sysconfdir}/distrobox/host-groups.sh
}

FILES:${PN} += "${datadir}/bash-completion ${datadir}/zsh/site-functions"
CONFFILES:${PN} = "${sysconfdir}/distrobox/distrobox.conf"
