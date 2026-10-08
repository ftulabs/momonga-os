# Momonga ARM64 RPM repository: build, update, and publish

This section is the maintainer runbook for the extra-package RPM feed. The feed is published as complete GitHub Releases and served by GitHub Pages; it is not published to R2.

## Package scope and paths

- The custom layer is `meta-custom/`.
- Current package targets include Neovim, `bat`, `fzf`, `nodejs24`, Zsh, and GDBM. `nodejs24` packages the upstream Node.js 24 ARM64 binary and npm together. `fzf` packages its upstream ARM64 binary and MIT license; it does not currently package shell bindings or completions.
- The build/repository directory is `build-packages/tmp/deploy/rpm/armv8a_tegra/`. It contains the RPMs and generated `repodata/`.
- The DNF base URL is `https://kani.ftds.online/rpm/momonga/aarch64/`. The URL's `aarch64` is a public repository path; RPM filenames use Yocto's `armv8a_tegra` package architecture.
- The flash image's package selection is separate. `config/local.conf` contains `CORE_IMAGE_EXTRA_INSTALL`; the resolved installed-package list is in the image `.manifest` under `build/tmp/deploy/images/jetson-agx-xavier-devkit/` after a full image build.

## Build packages

Run from the Poky checkout:

```sh
source ./oe-init-build-env build-packages
bitbake neovim bat fzf nodejs24 zsh gdbm
```

For a single package, run `bitbake <recipe>` with its recipe name, for example `bitbake fzf`. BitBake builds that recipe and its task/build dependencies. It does not build a flash image. `bitbake package-index` indexes RPMs already in the deploy directory; it does not build recipes.

Before updating an upstream package version, update the recipe filename/PV and verify source checksums. If package contents change without a version change, increment the package release (`PR`) so DNF sees a newer EVR. RPMs such as `-dbg`, `-dev`, `-doc`, `-src`, and `-ptest` are optional for normal target use; `nodejs24-dev` is useful when native npm add-ons must be built on Xavier. The fzf shell bindings/completions would need to be added separately if wanted.

The local Poky fork can diverge from `upstream/scarthgap`. Inspect upstream commits before merging:

```sh
git fetch upstream
git rev-list --left-right --count HEAD...upstream/scarthgap
git log --oneline HEAD..upstream/scarthgap
```

The last inspection found 11 commits unique to each side, including core security fixes. Merging upstream is a real merge, not a fast-forward. Preserve local work and coordinate core library/image changes with the full-image maintainer. Publishing the extra-package feed alone does not deliver Poky core security updates to Xavier.

## Sign RPMs and repository metadata

The signing key fingerprint is `BE27 4FDA FE86 B911 E0BA C3DB 5B2A A4FE 9449 ABF1`. Sign new or changed RPMs before building repository metadata. The passphrase is entered through GPG's pinentry; never put it in a command or file.

BitBake's native `rpmsign` can be found and run as follows. Set `RPM_FILES` to only the new/changed RPMs; use the complete deploy RPM set when verifying:

```sh
KEY=BE274FDAFE86B911E0BAC3DB5B2AA4FE9449ABF1
RPM_SIGN=$(find build-packages/tmp/work/x86_64-linux/rpm-native \
  -type f -path '*/recipe-sysroot-native/usr/bin/rpmsign' -print -quit)
RPM_USR=${RPM_SIGN%/bin/rpmsign}
export LD_LIBRARY_PATH="$RPM_USR/lib:$RPM_USR/lib/rpm${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}"

for rpm in $RPM_FILES; do
  "$RPM_SIGN" --addsign --define "_gpg_name $KEY" "$rpm"
done
```

After all package creation and RPM signing is complete, regenerate `repodata/`:

```sh
bitbake package-index
```

Then create a detached ASCII-armored signature for the final `repomd.xml`:

```sh
SRC="$PWD/build-packages/tmp/deploy/rpm/armv8a_tegra"
gpg --armor --detach-sign --local-user "$KEY" \
  --output "$SRC/repodata/repomd.xml.asc" \
  "$SRC/repodata/repomd.xml"
gpg --verify "$SRC/repodata/repomd.xml.asc" "$SRC/repodata/repomd.xml"
```

Do not rerun `bitbake package-index` after signing `repomd.xml`; regenerate and re-sign if metadata changes. The published public key must be `RPM-GPG-KEY-momonga`. Export only the public key; never export or upload the private key.

## Publish a complete feed release

The Pages workflow assembles the repository from assets in one release. Every update release must contain the **complete current RPM set**, not just the changed package, plus the public key and a metadata archive. Use a new unique tag such as `momonga-rpm-feed-2026.10.08-3`; do not reuse or edit an older release. Commit and push approved recipe/workflow changes before tagging, so the release uses the intended workflow version.

The metadata asset must be named exactly `feed-repodata.tar.gz`, and it must contain the `repodata/` directory including `repomd.xml.asc`:

```sh
TAG=momonga-rpm-feed-YYYY.MM.DD-N
SRC="$PWD/build-packages/tmp/deploy/rpm/armv8a_tegra"
tar -C "$SRC" -czf /tmp/opencode/feed-repodata.tar.gz repodata

git tag -a "$TAG" -m "$TAG"
git push origin "refs/tags/$TAG"
gh release create "$TAG" \
  "$SRC"/*.rpm \
  "$SRC/RPM-GPG-KEY-momonga" \
  /tmp/opencode/feed-repodata.tar.gz \
  --repo ftulabs/momonga-os \
  --title "Momonga RPM feed update" \
  --notes "Complete signed Momonga ARM64 RPM repository."
```

`gh release create` publishes immediately. A published `momonga-rpm-feed-*` release triggers `.github/workflows/publish-momonga-rpm-feed.yml`, which assembles the feed under `/rpm/momonga/aarch64/`, generates Jinja2 directory indexes, and deploys Pages. The workflow can also be run manually with `workflow_dispatch` and a published `release_tag`.

## Configure and update Xavier

Use this DNF repo configuration after the custom domain is reachable:

```ini
[momonga-extra]
name=Momonga Extra Packages
baseurl=https://kani.ftds.online/rpm/momonga/aarch64/
enabled=1
gpgcheck=1
gpgkey=https://kani.ftds.online/rpm/momonga/aarch64/RPM-GPG-KEY-momonga
repo_gpgcheck=1
```

`gpgcheck=1` verifies RPM signatures. `repo_gpgcheck=1` verifies the detached `repomd.xml.asc` with the same published key. After adding the file under `/etc/yum.repos.d/`, refresh and preview the transaction before installing:

```sh
sudo dnf makecache --refresh
sudo dnf --assumeno install fzf bat nodejs24 zsh
sudo dnf install fzf bat nodejs24 zsh
```

Neovim's prebuilt binary requires glibc 2.34 or newer; check Xavier's glibc before installing it. Node.js 24 requires glibc 2.28 or newer. Do not assume the target RPM solver checks every required symbol version.

The latest feed release is `momonga-rpm-feed-2026.10.08-3`. It contains 135 RPMs and its `feed-repodata.tar.gz` asset includes a verified `repomd.xml.asc`. The Pages deployment ran successfully, but the public `repodata/` listing and `repomd.xml.asc` URL still returned the older deployment/404 when last checked. Verify the live signature URL before telling clients to enable `repo_gpgcheck=1`; if needed, rerun the Pages workflow for the published release after its required workflow check is deployed. The current Pages custom domain is `kani.ftds.online`; check its DNS/Pages configuration if the feed URL changes.
