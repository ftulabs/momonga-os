# The headless image omits X11, but Vulkan still needs the NVIDIA ICD manifest.
FILES:${PN}:append = " ${datadir}/vulkan"

do_install:append() {
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json \
        ${D}/usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json
    install -d ${D}${datadir}/vulkan/icd.d
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json \
        ${D}${datadir}/vulkan/icd.d/nvidia_icd.json
    install -d ${D}${sysconfdir}/vulkan/icd.d
    ln -sf /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json \
        ${D}${sysconfdir}/vulkan/icd.d/nvidia_icd.json
}
