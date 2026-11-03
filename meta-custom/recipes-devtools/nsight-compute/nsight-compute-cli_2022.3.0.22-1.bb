DESCRIPTION = "NVIDIA Nsight Compute CLI profiler for CUDA kernels"
HOMEPAGE = "https://developer.nvidia.com/nsight-compute"
LICENSE = "Proprietary"
BASE_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
LIC_FILES_CHKSUM = "file://opt/nvidia/nsight-compute/${BASE_VERSION}/EULA.txt;md5=5c45accabbea4eeeb539ce4cd133d5c2"

SRC_URI = "https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2004/arm64/nsight-compute-${BASE_VERSION}_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "99cf45dfb35199aef89f8a6663d1d4916e8aa0058dd6ff79665ad500f81b1167"

S = "${WORKDIR}/${BPN}"
B = "${S}"

COMPATIBLE_HOST = "aarch64.*-linux"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/nsight-compute/${BASE_VERSION}/target
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/nsight-compute/${BASE_VERSION}/target/linux-v4l_l4t-t210-a64 ${D}/opt/nvidia/nsight-compute/${BASE_VERSION}/target/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/nsight-compute/${BASE_VERSION}/sections ${D}/opt/nvidia/nsight-compute/${BASE_VERSION}/
    install -m 0755 ${B}/opt/nvidia/nsight-compute/${BASE_VERSION}/ncu ${D}/opt/nvidia/nsight-compute/${BASE_VERSION}/

    install -d ${D}${bindir}
    ln -sf /opt/nvidia/nsight-compute/${BASE_VERSION}/ncu ${D}${bindir}/ncu
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
EXCLUDE_FROM_SHLIBS = "1"

FILES:${PN} = " \
    ${bindir}/ncu \
    /opt/nvidia/nsight-compute/${BASE_VERSION} \
"
INSANE_SKIP:${PN} = "ldflags file-rdeps dev-so already-stripped"
