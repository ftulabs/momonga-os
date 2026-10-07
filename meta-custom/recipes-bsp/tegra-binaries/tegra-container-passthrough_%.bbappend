DEPENDS:append = " cuda-compat-12-2 tegra-libraries-vulkan"
RDEPENDS:${PN}:append = " cuda-compat-12-2 tegra-libraries-vulkan"
# This package deliberately stages NVIDIA libraries below the container mount root.
INSANE_SKIP:${PN} += "libdir"

do_install:append() {
    install -d ${D}${PASSTHRU_ROOT}${libdir}
    install -m 0644 ${RECIPE_SYSROOT}${libdir}/libnvidia-vulkan-producer.so \
        ${D}${PASSTHRU_ROOT}${libdir}/libnvidia-vulkan-producer.so
    install -d ${D}${PASSTHRU_ROOT}${datadir}/vulkan/icd.d
    install -m 0644 ${RECIPE_SYSROOT}${datadir}/vulkan/icd.d/nvidia_icd.json \
        ${D}${PASSTHRU_ROOT}${datadir}/vulkan/icd.d/nvidia_icd.json
    install -d ${D}${PASSTHRU_ROOT}/usr/local/cuda-12.2
    cp -R --preserve=mode,links,timestamps \
        ${RECIPE_SYSROOT}/usr/local/cuda-12.2/compat \
        ${D}${PASSTHRU_ROOT}/usr/local/cuda-12.2/
}
