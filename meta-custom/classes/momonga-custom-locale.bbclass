python __anonymous() {
    recipe_file = d.getVar("FILE") or ""
    if "/meta-custom/recipes-" in recipe_file and d.getVar("PN") != "momonga-locale":
        d.appendVar("RDEPENDS:" + d.getVar("PN"), " momonga-locale")
}
