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
import stat
import subprocess

import pytest

from conftest import IDENTITY, SCRIPT_DIR, Signer, legacy_fixture_dmg, modern_fixture_dmg, xattrs, fixture_dmg, mount_point, requires_macos, volume_name

pytestmark = requires_macos


def test_help_exits_zero(signer):
    r = signer.run("--help")
    assert r.returncode == 0
    assert "input.dmg" in r.stdout
    assert "--non-release" in r.stdout


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


@pytest.mark.parametrize(
    "mode_args",
    [
        pytest.param((), id="neither"),
        pytest.param(("--release",), id="release-only"),
        pytest.param(("--notarize", "test-profile"), id="notarize-only"),
    ],
)
def test_release_requires_notarize_and_release(signer, workdir, mode_args):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "out.dmg"
    r = signer.run("-i", IDENTITY, *mode_args, src, out)
    assert r.returncode == 2
    assert "requires --notarize PROFILE and --release" in r.stderr
    assert not out.exists()


def test_release_and_non_release_conflict(signer, workdir):
    src = fixture_dmg(workdir, "args")
    r = signer.run(
        "-i", IDENTITY, "--release", "--non-release", src, signer.path / "out.dmg"
    )
    assert r.returncode == 2
    assert "cannot be used together" in r.stderr


def test_missing_source_exits_one(signer, workdir):
    r = signer.run_non_release(
        "-i", IDENTITY, workdir / "nope.dmg", signer.path / "out.dmg"
    )
    assert r.returncode == 1


def test_failed_attach_removes_mountpoint(signer):
    src = signer.path / "invalid.dmg"
    src.write_text("not a disk image\n")
    mount_root = signer.path / "mount-tmp"
    mount_root.mkdir()
    r = signer.run_non_release(
        "-i", IDENTITY, src, signer.path / "out.dmg", env={"TMPDIR": str(mount_root)}
    )
    assert r.returncode != 0
    assert list(mount_root.iterdir()) == []


def test_missing_sibling_signer_exits_two(workdir, tmp_path):
    import shutil

    from conftest import WRAPPER

    d = tmp_path / "no-signer"
    d.mkdir()
    shutil.copy(WRAPPER, d / "macosx-remote-sign.sh")
    src = fixture_dmg(workdir, "args")
    r = subprocess.run(
        [
            str(d / "macosx-remote-sign.sh"),
            "--non-release",
            "-i",
            IDENTITY,
            str(src),
            str(d / "out.dmg"),
        ],
        capture_output=True,
        text=True,
    )
    assert r.returncode == 2


# ------------------------------------------------------------ output guards


def test_directory_output_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "isadir"
    out.mkdir()
    r = signer.run_non_release("-i", IDENTITY, src, out)
    assert r.returncode == 2
    assert "directory" in r.stderr


def test_normalized_directory_output_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    requested = signer.path / "isadir"
    normalized = signer.path / "isadir.dmg"
    normalized.mkdir()
    r = signer.run_non_release("-i", IDENTITY, src, requested)
    assert r.returncode == 2
    assert "directory" in r.stderr
    assert list(normalized.iterdir()) == []


def test_missing_dmg_suffix_is_normalized(signer, workdir):
    src = fixture_dmg(workdir, "args")
    r = signer.run_non_release("-i", IDENTITY, src, signer.path / "suffixed")
    assert r.returncode == 0
    assert (signer.path / "suffixed.dmg").exists()
    assert not (signer.path / "suffixed").exists()


def test_output_equal_to_input_exits_two(signer, workdir):
    src = fixture_dmg(workdir, "args")
    r = signer.run_non_release("-i", IDENTITY, src, src)
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
    r = signer.run_non_release("-i", IDENTITY, "--sha256", value, src, out)
    assert r.returncode == 0, r.stderr


def test_sha256_mismatch_exits_one_and_writes_nothing(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "sum-bad.dmg"
    r = signer.run_non_release("-i", IDENTITY, "--sha256", "0" * 64, src, out)
    assert r.returncode == 1
    assert not out.exists()


# ---------------------------------------------------------- .app discovery


def test_no_app_exits_one(signer, workdir):
    src = fixture_dmg(workdir, "zeroapp", app_count=0, extras=False)
    r = signer.run_non_release("-i", IDENTITY, src, signer.path / "zero.dmg")
    assert r.returncode == 1
    assert "no .app" in r.stderr


def test_multiple_app_exits_one(signer, workdir):
    src = fixture_dmg(workdir, "twoapp", app_count=2, extras=False)
    r = signer.run_non_release("-i", IDENTITY, src, signer.path / "two.dmg")
    assert r.returncode == 1
    assert "exactly one" in r.stderr


# --------------------------------------------------------------- happy path


def test_normal_run_publishes_output_and_no_temp(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "out.dmg"
    r = signer.run_non_release("-i", IDENTITY, src, out)
    assert r.returncode == 0, r.stderr
    assert out.exists()
    assert list(signer.path.glob("out.dmg.tmp.*")) == []
    assert list(signer.path.glob(".macosx-remote-sign.*")) == []


def test_dmg_signed_under_final_name(signer, workdir):
    """codesign takes a dmg's signing identifier from its file name."""
    src = fixture_dmg(workdir, "args")
    out = signer.path / "final-name.dmg"
    assert signer.run_non_release("-i", IDENTITY, src, out).returncode == 0
    dmg_signs = [l for l in signer.log.read_text().splitlines() if l.endswith(".dmg")]
    assert len(dmg_signs) == 1
    assert dmg_signs[0].endswith("/final-name.dmg")


def test_release_forwards_options_and_validates_enclosed_staple(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "release.dmg"
    r = signer.run(
        "-i",
        IDENTITY,
        "-k",
        "/tmp/test.keychain-db",
        "-e",
        "/tmp/test-entitlements.plist",
        "--notarize",
        "test-profile",
        "--release",
        src,
        out,
    )
    assert r.returncode == 0, r.stderr
    assert out.exists()

    calls = signer.log.read_text().splitlines()
    assert len(calls) == 2
    expected = (
        f"-i {IDENTITY} -k /tmp/test.keychain-db -e /tmp/test-entitlements.plist "
        "--notarize test-profile --release"
    )
    assert expected in calls[0]
    assert expected in calls[1]

    staple_calls = signer.xcrun_log.read_text().splitlines()
    assert len(staple_calls) == 1
    assert staple_calls[0].startswith("XCRUN: stapler validate ")
    assert staple_calls[0].endswith("/App1.app")

    events = signer.events.read_text().splitlines()
    assert len(events) == 3
    assert events[:2] == ["sign-app", "sign-dmg"]
    assert events[2].startswith("validate:")
    assert "/macosx-remote-sign.mount." in events[2]
    assert events[2].endswith("/App1.app")


@pytest.mark.parametrize(
    "env",
    [
        pytest.param({"STUB_APP_AUTHORITY": "Apple Development: Test"}, id="app"),
        pytest.param({"STUB_DMG_AUTHORITY": "Apple Development: Test"}, id="dmg"),
    ],
)
def test_non_developer_id_signature_is_not_published(signer, workdir, env):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "wrong-identity.dmg"
    r = signer.run_non_release("-i", IDENTITY, src, out, env=env)
    assert r.returncode != 0
    assert "not signed with a Developer ID Application certificate" in r.stderr
    assert not out.exists()
    assert list(signer.path.glob("wrong-identity.dmg.tmp.*")) == []
    assert list(signer.path.glob(".macosx-remote-sign.*")) == []


def test_enclosed_staple_failure_is_not_published(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "bad-staple.dmg"
    r = signer.run(
        "-i",
        IDENTITY,
        "--notarize",
        "test-profile",
        "--release",
        src,
        out,
        env={"STUB_STAPLER_FAIL": "yes"},
    )
    assert r.returncode != 0
    assert not out.exists()
    assert list(signer.path.glob("bad-staple.dmg.tmp.*")) == []
    assert list(signer.path.glob(".macosx-remote-sign.*")) == []


def test_whole_volume_survives(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "out.dmg"
    assert signer.run_non_release("-i", IDENTITY, src, out).returncode == 0
    with mount_point(out) as mp:
        assert (mp / "READMEs").is_dir()
        assert (mp / "Applications").is_symlink()
        assert (mp / "READMEs" / ".DS_Store").is_file()
        assert (mp / "App1.app").is_dir()


@pytest.mark.parametrize("mode", [0o775, 0o755])
def test_volume_root_mode_preserved(signer, workdir, mode):
    src = fixture_dmg(workdir, "args", root_mode=mode)
    out = signer.path / "out.dmg"
    assert signer.run_non_release("-i", IDENTITY, src, out).returncode == 0
    with mount_point(src) as mp:
        assert stat.S_IMODE(mp.stat().st_mode) == mode
    with mount_point(out) as mp:
        assert stat.S_IMODE(mp.stat().st_mode) == mode


def test_app_finder_info_stripped_rest_kept(signer, workdir):
    src = fixture_dmg(workdir, "args", finder_info=True)
    out = signer.path / "out.dmg"
    with mount_point(src) as mp:
        lib = mp / "App1.app" / "Contents" / "lib.dylib"
        assert "com.apple.FinderInfo" in xattrs(lib)
    r = signer.run_non_release("-i", IDENTITY, src, out)
    assert r.returncode == 0, r.stderr
    with mount_point(out) as mp:
        lib = mp / "App1.app" / "Contents" / "lib.dylib"
        assert xattrs(lib) == []
        assert stat.S_IMODE(lib.stat().st_mode) == 0o444
        assert "com.apple.FinderInfo" in xattrs(mp / "READMEs")


def test_original_volume_name_reused(signer, workdir):
    src = fixture_dmg(workdir, "args")
    out = signer.path / "out.dmg"
    assert signer.run_non_release("-i", IDENTITY, src, out).returncode == 0
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
    r = signer.run_non_release(
        "-i", IDENTITY, src, out, env={"PATH": f"{stub_bin}:{os.environ['PATH']}"}
    )
    assert r.returncode == 0, r.stderr
    assert "volume: Foo: Bar" in r.stdout


# ----------------------------------------------- F1: no half-published output


def test_dmg_sign_failure_writes_nothing(tmp_path, workdir):
    signer = Signer(tmp_path, "w-dmgfail", fail="dmg")
    src = fixture_dmg(workdir, "args")
    out = signer.path / "final.dmg"
    r = signer.run_non_release("-i", IDENTITY, src, out)
    assert r.returncode != 0
    assert not out.exists()
    assert list(signer.path.glob("final.dmg.tmp.*")) == []
    assert list(signer.path.glob(".macosx-remote-sign.*")) == []


def test_app_sign_failure_writes_nothing(tmp_path, workdir):
    signer = Signer(tmp_path, "w-appfail")
    stub = signer.path / "macosx-codesign.sh"
    stub.write_text("#!/bin/bash\nexit 3\n")
    stub.chmod(0o755)
    src = fixture_dmg(workdir, "args")
    out = signer.path / "final.dmg"
    r = signer.run_non_release("-i", IDENTITY, src, out)
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
    r = signer.run_non_release("-i", IDENTITY, spaced_in, out)
    assert r.returncode == 0, r.stderr
    assert out.exists()


# ---------------------------------------------------------- legacy layout


def test_legacy_layout_moves_non_code_out_of_macos(signer, workdir):
    src = legacy_fixture_dmg(workdir, "legacy")
    out = signer.path / "out.dmg"
    r = signer.run_non_release("--legacy-layout", "-i", IDENTITY, src, out)
    assert r.returncode == 0, r.stderr
    with mount_point(out) as mp:
        contents = mp / "OpenOffice.app" / "Contents"
        macos = contents / "MacOS"
        assert os.readlink(macos / "unorc") == "../Resources/ooo-program/unorc"
        assert (macos / "unorc").read_text() == "unorc\n"
        assert stat.S_IMODE((contents / "Resources" / "ooo-program" / "unorc").stat().st_mode) == 0o444
        assert os.readlink(macos / "addin") == "../Resources/ooo-program/addin"
        assert (macos / "addin" / "a.rdb").read_text() == "addin\n"
        assert os.readlink(macos / "startup.sh") == "../Resources/ooo-program/startup.sh"
        assert os.readlink(macos / "regcomp") == "startup.sh"
        assert (macos / "regcomp").is_file()
        assert not (macos / "urelibs").is_symlink()
        for kept in ("soffice", "libcode.dylib"):
            assert (macos / kept).is_file() and not (macos / kept).is_symlink(), kept
        assert os.readlink(contents / "NOTICE") == "Resources/ooo-contents/NOTICE"
        assert (contents / "share" / "x.xcd").read_text() == "share\n"
        assert os.readlink(contents / "program") == "MacOS"
        for kept in ("Info.plist", "Library", "Resources"):
            assert not (contents / kept).is_symlink(), kept


def test_legacy_layout_without_flag_stops_before_signing(signer, workdir):
    src = legacy_fixture_dmg(workdir, "legacy")
    out = signer.path / "out.dmg"
    r = signer.run_non_release("-i", IDENTITY, src, out)
    assert r.returncode == 1
    assert "--legacy-layout" in r.stderr
    assert "unorc" in r.stderr and "addin" in r.stderr
    assert "libcode.dylib" not in r.stderr and "soffice" not in r.stderr
    assert not out.exists()
    assert signer.events.read_text() == ""


def test_modern_layout_signs_untouched_without_flag(signer, workdir):
    src = modern_fixture_dmg(workdir, "modern")
    out = signer.path / "out.dmg"
    r = signer.run_non_release("-i", IDENTITY, src, out)
    assert r.returncode == 0, r.stderr
    with mount_point(out) as mp:
        contents = mp / "OpenOffice.app" / "Contents"
        assert not (contents / "Resources" / "ooo-program").exists()
        assert (contents / "MacOS" / "soffice").is_file()
        assert os.readlink(contents / "MacOS" / "libcode.1.dylib") == "libcode.dylib"


def test_legacy_layout_refuses_code_in_moved_folder(signer, workdir):
    src = legacy_fixture_dmg(workdir, "legacy", code_in_subdir=True)
    out = signer.path / "out.dmg"
    r = signer.run_non_release("--legacy-layout", "-i", IDENTITY, src, out)
    assert r.returncode == 1
    assert "addin" in r.stderr
    assert not out.exists()
    assert "sign-app" not in signer.events.read_text()


# ------------------------------------------------------- bundled delegate


def test_bundled_delegate_accepts_forwarded_options(workdir):
    """The real sibling macosx-codesign.sh must parse every option the wrapper
    forwards; a missing path makes it stop right after option parsing."""
    delegate = SCRIPT_DIR / "macosx-codesign.sh"
    missing = workdir / "missing.dmg"
    r = subprocess.run(
        [str(delegate), "-i", IDENTITY, "-k", "k", "-e", "e",
         "--notarize", "p", "--release", str(missing)],
        capture_output=True, text=True,
    )
    assert r.returncode == 1, r.stderr
    assert f"no such path: {missing}" in r.stderr


def test_bundled_delegate_siblings_present():
    for name in ("macosx-codesign.sh", "macosx-check-load-commands.sh"):
        assert os.access(SCRIPT_DIR / name, os.X_OK), name
    assert (SCRIPT_DIR / "macosx-codesign-entitlements.plist").is_file()
