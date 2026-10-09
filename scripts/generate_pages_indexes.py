#!/usr/bin/env python3
"""Generate Apache-style static directory indexes for a GitHub Pages tree."""

import argparse
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import quote

from jinja2 import Environment, FileSystemLoader, select_autoescape


def display_size(size: int) -> str:
    value = float(size)
    for unit in ("B", "KiB", "MiB", "GiB", "TiB"):
        if value < 1024 or unit == "TiB":
            return f"{value:.0f} {unit}" if unit == "B" else f"{value:.1f} {unit}"
        value /= 1024
    return f"{value:.1f} TiB"


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", required=True, type=Path, help="root of the Pages site")
    parser.add_argument("--template", required=True, type=Path, help="Jinja2 HTML template")
    args = parser.parse_args()

    root = args.root.resolve()
    template_path = args.template.resolve()
    env = Environment(
        loader=FileSystemLoader(template_path.parent),
        autoescape=select_autoescape(("html", "j2")),
    )
    template = env.get_template(template_path.name)

    for directory in sorted(path for path in root.rglob("*") if path.is_dir()) + [root]:
        children = []
        for child in sorted(directory.iterdir(), key=lambda item: (item.is_file(), item.name.casefold())):
            if child.name == "index.html":
                continue
            is_directory = child.is_dir()
            children.append(
                {
                    "name": child.name + ("/" if is_directory else ""),
                    "href": quote(child.name, safe="@:+,.-_~") + ("/" if is_directory else ""),
                    "kind": "Directory" if is_directory else "File",
                    "modified": datetime.fromtimestamp(
                        child.stat().st_mtime, timezone.utc
                    ).strftime("%Y-%m-%d %H:%M UTC"),
                    "size": "—" if is_directory else display_size(child.stat().st_size),
                }
            )

        relative = directory.relative_to(root).as_posix()
        display_path = "/" if relative == "." else f"/{relative}/"
        rendered = template.render(
            title=f"Index of {display_path}",
            path=display_path,
            parent_href=None if directory == root else "../",
            entries=children,
        )
        (directory / "index.html").write_text(rendered, encoding="utf-8")


if __name__ == "__main__":
    main()
