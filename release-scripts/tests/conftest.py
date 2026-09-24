################################################################
#
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
#
################################################################

"""Shared fixtures for the macosx-remote-sign.sh tests.

No Developer ID key is available here, so the wrapper is exercised with a stub
macosx-codesign.sh. That covers the wrapper's mount/copy/rebuild/publish/cleanup
logic only; the signature itself is the delegate's responsibility.
"""

import os
import shutil
import stat
import subprocess
from pathlib import Path

import pytest

SCRIPT_DIR = Path(__file__).resolve().parent.parent
WRAPPER = SCRIPT_DIR / "macosx-remote-sign.sh"

IDENTITY = "Developer ID Application: Test"

requires_macos = pytest.mark.skipif(
    os.uname().sysname != "Darwin", reason="requires macOS (hdiutil and diskutil)"
)


def _run(args, **kwargs):
    return subprocess.run(args, capture_output=True, text=True, **kwargs)


def make_dmg(src: Path, volname: str, out: Path) -> None:
    result = _run(
        [
            "hdiutil", "create",
            "-srcfolder", str(src),
            "-volname", volname,
            "-fs", "HFS+",
            "-format", "UDZO",
            "-ov", str(out),
        ]
    )
    assert result.returncode == 0, result.stderr


FINDER_INFO_HEX = "00" * 10 + "FFFFFFFF" + "00" * 18


def set_finder_info(path: Path) -> None:
    r = _run(["xattr", "-wx", "com.apple.FinderInfo", FINDER_INFO_HEX, str(path)])
    assert r.returncode == 0, r.stderr


def fixture_dmg(
    root: Path,
    name: str,
    app_count: int = 1,
    extras: bool = True,
    root_mode: int | None = None,
    finder_info: bool = False,
) -> Path:
    """Build an install-dmg-like fixture and return the path to its in.dmg."""
    src = root / name / "src"
    src.mkdir(parents=True)
    if root_mode is not None:
        src.chmod(root_mode)
    for i in range(1, app_count + 1):
        app = src / f"App{i}.app" / "Contents"
        app.mkdir(parents=True)
        (app / "Info.plist").write_text("fixture\n")
    if extras:
        (src / "READMEs").mkdir()
        (src / "READMEs" / "readme.txt").write_text("readme\n")
        (src / "Applications").symlink_to("/Applications")
        # A nested .DS_Store survives dmg (re)creation; a root one written as a
        # plain file does not (hdiutil create drops it). See REVIEW F7.
        (src / "READMEs" / ".DS_Store").write_text("finder-layout\n")
    if finder_info:
        # Shipped AOO dmgs carry FinderInfo on read-only files, which xattr -c
        # cannot strip and codesign rejects as "detritus".
        lib = src / "App1.app" / "Contents" / "lib.dylib"
        lib.write_text("not really a dylib\n")
        set_finder_info(lib)
        lib.chmod(0o444)
        set_finder_info(src / "READMEs")
    out = root / name / "in.dmg"
    make_dmg(src, f"remote-sign-{name}", out)
    return out


def _stub_log(root: Path) -> Path:
    log = root / "stub.log"
    log.write_text("")
    return log


class Signer:
    """A scratch dir holding the wrapper plus a controllable stub delegate."""

    def __init__(self, root: Path, name: str, fail: str = "none"):
        self.root = root
        self.path = root / name
        self.path.mkdir(parents=True)
        self.log = _stub_log(self.path)
        self.xcrun_log = self.path / "xcrun.log"
        self.xcrun_log.write_text("")
        self.events = self.path / "events.log"
        self.events.write_text("")
        self.bin = self.path / ".test-bin"
        self.bin.mkdir()
        self.fail = fail
        shutil.copy(WRAPPER, self.path / "macosx-remote-sign.sh")
        self._write_stub()
        self._write_tool_stubs()

    def _write_stub(self):
        stub = self.path / "macosx-codesign.sh"
        body = [
            "#!/bin/bash",
            'printf "STUB: %s\\n" "$*" >> "$STUB_LOG"',
            'case "${!#}" in',
            '  *.dmg) printf "sign-dmg\\n" >> "$STUB_EVENTS" ;;',
            '  *) printf "sign-app\\n" >> "$STUB_EVENTS"; touch "${!#}/Contents/.signed-by-stub" ;;',
            "esac",
        ]
        if self.fail == "dmg":
            body += [
                'case "$*" in',
                '  *.dmg) echo "STUB refusing dmg sign" >&2; exit 1 ;;',
                "esac",
            ]
        body.append("exit 0")
        stub.write_text("\n".join(body) + "\n")
        stub.chmod(stub.stat().st_mode | stat.S_IXUSR)

    def _write_tool_stubs(self):
        codesign = self.bin / "codesign"
        codesign.write_text(
            "#!/bin/bash\n"
            'case "${!#}" in\n'
            '  *.dmg) authority="${STUB_DMG_AUTHORITY:-Developer ID Application: Test}" ;;\n'
            '  *) authority="${STUB_APP_AUTHORITY:-Developer ID Application: Test}" ;;\n'
            "esac\n"
            'printf "Authority=%s\\n" "$authority" >&2\n'
            "exit 0\n"
        )
        codesign.chmod(0o755)

        xcrun = self.bin / "xcrun"
        xcrun.write_text(
            "#!/bin/bash\n"
            'printf "XCRUN: %s\\n" "$*" >> "$STUB_XCRUN_LOG"\n'
            'printf "validate:%s\\n" "${!#}" >> "$STUB_EVENTS"\n'
            '[ -f "${!#}/Contents/.signed-by-stub" ] || exit 2\n'
            '[ "${STUB_STAPLER_FAIL:-no}" = no ] || exit 1\n'
            "exit 0\n"
        )
        xcrun.chmod(0o755)

    def run(self, *args, env=None):
        run_env = os.environ.copy()
        run_env["STUB_LOG"] = str(self.log)
        run_env["STUB_XCRUN_LOG"] = str(self.xcrun_log)
        run_env["STUB_EVENTS"] = str(self.events)
        if env:
            run_env.update(env)
        run_env["PATH"] = f"{self.bin}:{run_env['PATH']}"
        return subprocess.run(
            [str(self.path / "macosx-remote-sign.sh"), *args],
            capture_output=True,
            text=True,
            env=run_env,
        )

    def run_non_release(self, *args, env=None):
        return self.run("--non-release", *args, env=env)


@pytest.fixture
def workdir(tmp_path):
    return tmp_path


@pytest.fixture
def signer(tmp_path):
    return Signer(tmp_path, "w-main")


def mount_point(dmg: Path):
    """Context manager mounting dmg read-only, yielding the mount point."""
    import contextlib

    @contextlib.contextmanager
    def _mount():
        mp = Path(_run(["mktemp", "-d"]).stdout.strip())
        attach = _run(
            ["hdiutil", "attach", "-readonly", "-nobrowse", "-mountpoint", str(mp), str(dmg)]
        )
        assert attach.returncode == 0, attach.stderr
        try:
            yield mp
        finally:
            _run(["hdiutil", "detach", str(mp), "-quiet"])
            shutil.rmtree(mp, ignore_errors=True)

    return _mount()


def volume_name(dmg: Path) -> str:
    with mount_point(dmg) as mp:
        info = _run(["diskutil", "info", str(mp)])
        assert info.returncode == 0, info.stderr
        for line in info.stdout.splitlines():
            if line.strip().startswith("Volume Name:"):
                return line.split(":", 1)[1].strip()
    raise AssertionError(f"no volume name for {dmg}")


def xattrs(path: Path) -> list[str]:
    r = _run(["xattr", str(path)])
    assert r.returncode == 0, r.stderr
    return r.stdout.split()


INFO_PLIST = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0"><dict><key>CFBundleExecutable</key><string>soffice</string></dict></plist>
"""


def legacy_fixture_dmg(root: Path, name: str, code_in_subdir: bool = False) -> Path:
    """An install dmg whose .app has the 4.1.x layout: data files and folders
    in Contents/MacOS and loose files directly in Contents/."""
    src = root / name / "src"
    contents = src / "OpenOffice.app" / "Contents"
    macos = contents / "MacOS"
    macos.mkdir(parents=True)
    (contents / "Info.plist").write_text(INFO_PLIST)
    (contents / "NOTICE").write_text("notice\n")
    (contents / "share").mkdir()
    (contents / "share" / "x.xcd").write_text("share\n")
    (contents / "Library").mkdir()
    (contents / "Resources").mkdir()
    (contents / "program").symlink_to("MacOS")
    # The launcher is the bundle's main executable; stays put whatever it is.
    (macos / "soffice").write_text("#!/bin/sh\n")
    shutil.copy("/usr/bin/true", macos / "libcode.dylib")
    (macos / "unorc").write_text("unorc\n")
    (macos / "unorc").chmod(0o444)
    (macos / "startup.sh").write_text("#!/bin/sh\n")
    (macos / "regcomp").symlink_to("startup.sh")
    (macos / "urelibs").symlink_to("../basis-link/ure-link/lib")
    (macos / "addin").mkdir()
    (macos / "addin" / "a.rdb").write_text("addin\n")
    if code_in_subdir:
        shutil.copy("/usr/bin/true", macos / "addin" / "helper")
    (src / "Applications").symlink_to("/Applications")
    out = root / name / "in.dmg"
    make_dmg(src, f"remote-sign-{name}", out)
    return out


def modern_fixture_dmg(root: Path, name: str) -> Path:
    """An install dmg whose .app has trunk's layout: only code in Contents/MacOS."""
    src = root / name / "src"
    contents = src / "OpenOffice.app" / "Contents"
    macos = contents / "MacOS"
    macos.mkdir(parents=True)
    (contents / "Info.plist").write_text(INFO_PLIST)
    (contents / "Resources").mkdir()
    (contents / "Resources" / "unorc").write_text("unorc\n")
    (macos / "soffice").write_text("#!/bin/sh\n")
    shutil.copy("/usr/bin/true", macos / "libcode.dylib")
    (macos / "libcode.1.dylib").symlink_to("libcode.dylib")
    out = root / name / "in.dmg"
    make_dmg(src, f"remote-sign-{name}", out)
    return out
