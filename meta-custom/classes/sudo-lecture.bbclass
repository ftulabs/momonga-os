# Sudo lecture configuration
# Installs custom lecture text from config/sudoers.lecture and configures sudoers
# to display it using lecture_file.

SUDO_LECTURE_FILE ??= "${MOMONGA_ROOT}/config/sudoers.lecture"
SUDO_LECTURE_MODE ??= "always"

install_sudo_lecture () {
    if [ -f "${SUDO_LECTURE_FILE}" ] && [ -d "${IMAGE_ROOTFS}${sysconfdir}" ]; then
        install -D -m 0440 "${SUDO_LECTURE_FILE}" "${IMAGE_ROOTFS}${sysconfdir}/sudoers.lecture"
        install -d -m 0750 "${IMAGE_ROOTFS}${sysconfdir}/sudoers.d"
        cat > "${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/00-lecture" <<EOF
Defaults lecture_file = "/etc/sudoers.lecture"
Defaults lecture = ${SUDO_LECTURE_MODE}
EOF
        chmod 0440 "${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/00-lecture"
    fi
}

do_rootfs[file-checksums] += "${@'${SUDO_LECTURE_FILE}:True' if os.path.exists(d.getVar('SUDO_LECTURE_FILE') or '') else ''}"
