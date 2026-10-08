# The base recipe disables dynamic modules. Powerlevel10k and fzf-tab need
# modules such as zsh/files, zsh/mathfunc, zsh/stat, and zsh/mapfile.
EXTRA_OECONF:remove = "--disable-dynamic"
EXTRA_OECONF:append = " --enable-dynamic"
EXTRA_OECONF:remove = "--disable-gdbm"
EXTRA_OECONF:append = " --enable-gdbm"
PR:append = ".2"

# Keep the dynamically loadable modules in the zsh RPM.
FILES:${PN} += "${libdir}/zsh"
