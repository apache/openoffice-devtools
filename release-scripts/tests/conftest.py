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
    os.uname().sysname != "Darwin", reason="requires macOS (hdiutil, diskutil, plutil)"
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


def fixture_dmg(root: Path, name: str, app_count: int = 1, extras: bool = True) -> Path:
    """Build an install-dmg-like fixture and return the path to its in.dmg."""
    src = root / name / "src"
    src.mkdir(parents=True)
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
        self.fail = fail
        shutil.copy(WRAPPER, self.path / "macosx-remote-sign.sh")
        self._write_stub()

    def _write_stub(self):
        stub = self.path / "macosx-codesign.sh"
        body = [
            "#!/bin/bash",
            'printf "STUB: %s\\n" "$*" >> "$STUB_LOG"',
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

    def run(self, *args, env=None):
        run_env = os.environ.copy()
        run_env["STUB_LOG"] = str(self.log)
        if env:
            run_env.update(env)
        return subprocess.run(
            [str(self.path / "macosx-remote-sign.sh"), *args],
            capture_output=True,
            text=True,
            env=run_env,
        )


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
