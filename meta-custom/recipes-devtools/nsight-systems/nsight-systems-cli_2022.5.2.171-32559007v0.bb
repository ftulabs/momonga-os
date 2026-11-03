DESCRIPTION = "NVIDIA Nsight Systems is a multi-core CPU sampling profiler that \
    provides an interactive view of captured profiling data, helping improve \
    overall application performance."
HOMEPAGE = "https://developer.nvidia.com/nsight-systems"
LICENSE = "Proprietary"
PR = "r1"
BASE_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
LIC_FILES_CHKSUM = "file://opt/nvidia/nsight-systems/${BASE_VERSION}/EULA.txt;md5=5c45accabbea4eeeb539ce4cd133d5c2"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "nsight-systems-${BASE_VERSION}_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "444d62345248749e33dcac40668b509c96735263cf8a42391015eba6ec87118a"

S = "${WORKDIR}/${BPN}"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/nsight-systems/${BASE_VERSION}/target-linux-tegra-armv8/ ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/ ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/

    # Strip GUI binaries, Qt libraries, and non-CLI dependencies from host-linux-armv8
    # Keep QdstrmImporter and its headless libraries for CLI decoding and reporting
    rm -rf ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/nsys-ui* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/CrashReporter \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/Mesa \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/Plugins \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/resources \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/translations \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libexec \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libQt6* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libNvQtGui* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libQtPropertyBrowser* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libTimelineUIUtils* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libTimelineWidget* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libAppLib* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libInterfaceSharedCore* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libInterfaceShared.so* \
           ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/libicu*

    install -d ${D}${bindir}
    ln -sf /opt/nvidia/nsight-systems/${BASE_VERSION}/target-linux-tegra-armv8/nsys ${D}${bindir}/nsys
    ln -sf /opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/QdstrmImporter ${D}${bindir}/QdstrmImporter
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
EXCLUDE_FROM_SHLIBS = "1"

PACKAGES =+ "${PN}-qdstrmimporter"
ALLOW_EMPTY:${PN}-qdstrmimporter = "1"
RDEPENDS:${PN}-qdstrmimporter = "${PN}"

FILES:${PN} = " \
    ${bindir}/nsys \
    ${bindir}/QdstrmImporter \
    /opt/nvidia/nsight-systems/${BASE_VERSION}/target-linux-tegra-armv8 \
    /opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8 \
"
FILES:${PN}-qdstrmimporter = ""
INSANE_SKIP:${PN} = "ldflags file-rdeps dev-so"
INSANE_SKIP:${PN}-qdstrmimporter = "ldflags file-rdeps dev-so"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
