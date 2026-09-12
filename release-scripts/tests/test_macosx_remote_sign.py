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

"""Tests for release-scripts/macosx-remote-sign.sh."""

import hashlib
import os
import subprocess

import pytest

from conftest import IDENTITY, Signer, fixture_dmg, mount_point, requires_macos, volume_name

pytestmark = requires_macos


def test_help_exits_zero(signer):
    r = signer.run("--help")
    assert r.returncode == 0
    assert "unsigned.dmg" in r.stdout


def test_no_arguments_exits_two(signer):
    assert signer.run().returncode == 2


def test_unknown_option_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    assert signer.run("--bogus", src, signer.path / "out.dmg").returncode == 2


def test_missing_option_argument_exits_two(signer):
    assert signer.run("-i").returncode == 2


def test_third_positional_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    r = signer.run("-i", IDENTITY, src, signer.path / "out.dmg", "extra")
    assert r.returncode == 2


def test_missing_identity_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    assert signer.run(src, signer.path / "out.dmg").returncode == 2


def test_adhoc_identity_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    assert signer.run("-i", "-", src, signer.path / "out.dmg").returncode == 2


def test_missing_source_exits_one(signer, workdir):
    r = signer.run("-i", IDENTITY, workdir / "nope.dmg", signer.path / "out.dmg")
    assert r.returncode == 1


def test_missing_sibling_signer_exits_two(workdir, tmp_path):
    import shutil

    from conftest import WRAPPER

    d = tmp_path / "no-signer"
    d.mkdir()
    shutil.copy(WRAPPER, d / "macosx-remote-sign.sh")
    src = fixture_dmg(workdir, "args")
    r = subprocess.run(
        [str(d / "macosx-remote-sign.sh"), "-i", IDENTITY, str(src), str(d / "out.dmg")],
        capture_output=True,
        text=True,
    )
    assert r.returncode == 2


# ------------------------------------------------------------ output guards


def test_directory_output_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "isadir"
    out.mkdir()
    r = signer.run("-i", IDENTITY, src, out)
    assert r.returncode == 2
    assert "directory" in r.stderr


def test_missing_dmg_suffix_is_normalized(signer, workdir):
    src = fixture_dmg(workdir, "args")
    r = signer.run("-i", IDENTITY, src, signer.path / "suffixed")
    assert r.returncode == 0
    assert (signer.path / "suffixed.dmg").exists()
    assert not (signer.path / "suffixed").exists()


def test_output_equal_to_input_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    r = signer.run("-i", IDENTITY, src, src)
    assert r.returncode == 2
    assert "input" in r.stderr


# ------------------------------------------------------------- checksum (F2)


def _digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


@pytest.mark.parametrize(
    "fmt",
    [
        pytest.param(lambda p, d: d, id="bare-digest"),
        pytest.param(lambda p, d: f"{d}  {p.name}", id="shasum-line"),
        pytest.param(lambda p, d: f"{d} *{p.name}", id="hash-sign-line"),
    ],
)
def test_sha256_accepts_digest_and_lines(signer, workdir, fmt):
    src = fixture_dmg(workdir, "args")
    value = fmt(src, _digest(src))
    out = signer.path / f"sum-{abs(hash(value))}.dmg"
    r = signer.run("-i", IDENTITY, "--sha256", value, src, out)
    assert r.returncode == 0, r.stderr


def test_sha256_mismatch_exits_one_and_writes_nothing(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "sum-bad.dmg"
    r = signer.run("-i", IDENTITY, "--sha256", "0" * 64, src, out)
    assert r.returncode == 1
    assert not out.exists()


# ---------------------------------------------------------- .app discovery


def test_no_app_exits_one(signer, workdir):
    src = fixture_dmg(workdir, "zeroapp", app_count=0, extras=False)
    r = signer.run("-i", IDENTITY, src, signer.path / "zero.dmg")
    assert r.returncode == 1
    assert "no .app" in r.stderr


def test_multiple_app_exits_one(signer, workdir):
    src = fixture_dmg(workdir, "twoapp", app_count=2, extras=False)
    r = signer.run("-i", IDENTITY, src, signer.path / "two.dmg")
    assert r.returncode == 1
    assert "exactly one" in r.stderr


# --------------------------------------------------------------- happy path


def test_normal_run_publishes_output_and_no_temp(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "out.dmg"
    r = signer.run("-i", IDENTITY, src, out)
    assert r.returncode == 0, r.stderr
    assert out.exists()
    assert list(signer.path.glob("out.dmg.tmp.*")) == []


def test_whole_volume_survives(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "out.dmg"
    assert signer.run("-i", IDENTITY, src, out).returncode == 0
    with mount_point(out) as mp:
        assert (mp / "READMEs").is_dir()
        assert (mp / "Applications").is_symlink()
        assert (mp / "READMEs" / ".DS_Store").is_file()
        assert (mp / "App1.app").is_dir()


def test_original_volume_name_reused(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "out.dmg"
    assert signer.run("-i", IDENTITY, src, out).returncode == 0
    assert volume_name(out) == "remote-sign-args"


def test_volume_name_parse_keeps_colons(signer, workdir):
    """The wrapper reads the volume name from `diskutil info` output without
    splitting on a colon that is part of the name. HFS+ cannot store ':' in a
    volume name (hdiutil rewrites it to '/'), so the value cannot be produced
    end-to-end; a stub `diskutil` on PATH drives the script's own parse."""
    stub_bin = signer.path / "bin"
    stub_bin.mkdir()
    stub = stub_bin / "diskutil"
    stub.write_text(
        "#!/bin/bash\n"
        "printf '   Volume Name:               Foo: Bar\\n'\n"
    )
    stub.chmod(0o755)
    src = fixture_dmg(workdir, "colon", extras=False)
    out = signer.path / "colon.dmg"
    r = signer.run(
        "-i", IDENTITY, src, out, env={"PATH": f"{stub_bin}:{os.environ['PATH']}"}
    )
    assert r.returncode == 0, r.stderr
    assert "volume: Foo: Bar" in r.stdout


# ----------------------------------------------- F1: no half-published output


def test_dmg_sign_failure_writes_nothing(tmp_path, workdir):
    signer = Signer(tmp_path, "w-dmgfail", fail="dmg")
    src = fixture_dmg(workdir, "args")
    out = signer.path / "final.dmg"
    r = signer.run("-i", IDENTITY, src, out)
    assert r.returncode != 0
    assert not out.exists()
    assert list(signer.path.glob("final.dmg.tmp.*")) == []


def test_app_sign_failure_writes_nothing(tmp_path, workdir):
    signer = Signer(tmp_path, "w-appfail")
    stub = signer.path / "macosx-codesign.sh"
    stub.write_text("#!/bin/bash\nexit 3\n")
    stub.chmod(0o755)
    src = fixture_dmg(workdir, "args")
    out = signer.path / "final.dmg"
    r = signer.run("-i", IDENTITY, src, out)
    assert r.returncode != 0
    assert not out.exists()


# ------------------------------------------------------------ paths with spaces


def test_spaces_in_paths(tmp_path, workdir):
    signer = Signer(tmp_path, "sp ace")
    src = fixture_dmg(workdir, "args")
    spaced_in = signer.path / "in sp.dmg"
    import shutil

    shutil.copy(src, spaced_in)
    out = signer.path / "out dir" / "signed out.dmg"
    r = signer.run("-i", IDENTITY, spaced_in, out)
    assert r.returncode == 0, r.stderr
    assert out.exists()
