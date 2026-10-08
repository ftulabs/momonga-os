# Agent and maintainer notes

## Momonga ARM64 RPM feed

- The extra-package BitBake layer is `meta-custom/`.
- Current extra package recipes: Neovim, `bat`, `fzf`, Node.js/npm, and the Zsh dynamic-module/GDBM configuration.
- `fzf` is packaged from upstream's prebuilt Linux ARM64 release binary (currently recipe `fzf_0.74.4.bb`); it is not compiled in the Yocto build. The RPM includes the binary and MIT license. Shell bindings/completions are not included by this recipe.
- The build output and RPM repository are in `build-packages/tmp/deploy/rpm/armv8a_tegra/`.
- RPMs are signed with GPG key ID `BE274FDAFE86B911E0BAC3DB5B2AA4FE9449ABF1`. Export and publish only the ASCII-armored public key as `RPM-GPG-KEY-momonga`. Never export or upload the private key.
- RPM signatures are enabled on clients (`gpgcheck=1`); repository metadata signatures are not enabled (`repo_gpgcheck=0`).

## Build and publish package updates

Run from the Poky checkout:

```sh
source ./oe-init-build-env build-packages
bitbake neovim bat fzf nodejs24 zsh gdbm
```

For a single package, build only its recipe and needed runtime packages. Before updating an upstream release, update the recipe version and verify all source checksums.

Sign any new or changed RPMs with the repository signing key. Then regenerate repository metadata; do not run package-index before package creation/signing, because its checksums must match the final signed RPM files:

```sh
bitbake package-index
```

The repository is published through GitHub Releases and GitHub Pages, not R2. Pages assembles a complete feed from one release, so every feed release must contain the **complete current RPM set**, not only the changed package. Create a new release for each update; do not replace or edit an old feed release. Use tags of the form `momonga-rpm-feed-YYYY.MM.DD` and add a suffix such as `-1` for another release on the same date.

Each published release must include:

1. Every `*.rpm` from `build-packages/tmp/deploy/rpm/armv8a_tegra/`.
2. The public key `RPM-GPG-KEY-momonga`.
3. An archive named exactly `feed-repodata.tar.gz`, containing the `repodata/` directory.

The exact metadata asset name matters: the Pages workflow downloads it by that name. Publishing a release whose tag starts with `momonga-rpm-feed-` triggers `.github/workflows/publish-momonga-rpm-feed.yml`. That workflow can also be run manually with `workflow_dispatch`, providing the published `release_tag`.

Before creating a release, commit and push the approved recipe/workflow changes to `scarthgap`. A release event uses the workflow file at the tagged commit, so tagging an older commit can run an outdated publisher. Then, from the Poky checkout, build the exact assets and publish a new release. Replace the example tag with a unique date/suffix:

```sh
TAG=momonga-rpm-feed-2026.10.08-2
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

`gh release create` publishes immediately and triggers Pages deployment. If making a draft through GitHub's web interface or `gh release create --draft`, publish the draft after checking its assets to trigger deployment. Do not reuse an existing release tag.

## Pages layout and directory indexes

- The DNF repository URL is `https://momonga.ftds.online/rpm/momonga/aarch64/` once that custom domain is configured.
- GitHub Pages is static; it does not provide Apache's automatic index generation. The workflow renders static listings with Jinja2 using `scripts/generate_pages_indexes.py` and `scripts/templates/pages-directory-index.html.j2`.
- The generator writes an `index.html` in each site directory, with links to immediate child files/directories and file sizes. It runs after the feed assets and metadata have been assembled.
- To preview locally, assemble a site tree with the intended files, then run:

  ```sh
  python3 -m pip install Jinja2
  python3 scripts/generate_pages_indexes.py \
    --root /path/to/site \
    --template scripts/templates/pages-directory-index.html.j2
  ```

- A local preview was generated at `/tmp/opencode/momonga-pages-preview/`; it is temporary and is not part of the repository.
- A previous Pages API check reported `yoroi.ftds.online` as the configured CNAME, and the `ftulabs.github.io` URL redirected there. Check the repository's Pages custom-domain setting and DNS before relying on `momonga.ftds.online`; do not change the domain without authorization.

## Current deployment context

- The Momonga feed release with fzf is `momonga-rpm-feed-2026.10.08-1`.
- That release contains the full set of 135 RPMs, the public key, and repository metadata. Its first Pages run failed because the metadata archive had a dated filename; the asset was corrected to `feed-repodata.tar.gz`, and a manual Pages deployment completed successfully.
- The local recipe is named `nodejs24` to follow Fedora's versioned runtime naming. It bundles Node.js 24 and npm in one RPM; Fedora's `nodejs24-bin` is a separate unversioned-symlink subpackage and is not an exact structural match.
- The published release still contains the old `nodejs-bin` RPM. The local rename has been built, signed, and indexed, but has not been committed or published; a future feed release must replace the old RPM set with the current deploy-directory contents.
- Directory-index changes are currently uncommitted. Keep them uncommitted until the maintainer reviews the local preview.
- Xavier references elsewhere in the repository describe the Jetson hardware/image and should not be renamed as part of Momonga feed naming.
