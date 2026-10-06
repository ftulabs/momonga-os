set_root_shell_to_bash () {
    if [ -f "${IMAGE_ROOTFS}${sysconfdir}/passwd" ]; then
        sed -E -i -e 's#^root:(.*):/bin/(sh|zsh|bash)$#root:\1:/bin/bash#' "${IMAGE_ROOTFS}${sysconfdir}/passwd"
    fi
}
