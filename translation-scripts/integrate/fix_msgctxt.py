#!/usr/bin/env python
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
"""Re-key exported PO units to a new template before pomerge.

oo2po uses the location as msgctxt for duplicate msgids, and Help locations
carry the old oldref value as lid (file#gid.lid.type.field).  Once the source
lost its oldref attributes the template contexts no longer match, and pomerge
then marks those units fuzzy or leaves them empty.

For each unit, locations are looked up in the template as they are and with
the lid removed.  A location counts only if the template unit there has the
same English; the unit then takes that unit's msgctxt and locations.  Other
locations are removed, since po2oo applies a translation to every location a
unit lists.  Units left without a location are kept for pomerge to place by
source text, unless that would overwrite a unit already placed by location.

usage: fix_msgctxt.py --template POTDIR --input PODIR --output OUTDIR
"""
from __future__ import print_function

import argparse
import io
import os
import sys

from translate.storage import factory


def norm(loc):
    return loc.replace('%23', '#')


def candidates(loc):
    loc = norm(loc)
    yield loc
    if '#' in loc:
        fname, rest = loc.split('#', 1)
        parts = rest.split('.')
        # gid.lid.type.field -> gid.type.field
        if len(parts) == 4:
            yield fname + '#' + '.'.join([parts[0]] + parts[2:])


def template_index(potpath):
    index = {}
    first = {}
    for unit in factory.getobject(potpath).units:
        if unit.isheader():
            continue
        first.setdefault(unit.source, unit.getcontext())
        for loc in unit.getlocations():
            index.setdefault(norm(loc), (unit.getcontext(), unit.source))
    return index, first


def set_locations(unit, locs):
    unit.sourcecomments = []
    for loc in locs:
        unit.addlocation(loc)


def fix_store(store, templ, stats):
    index, first = templ
    keep = []
    unplaced = []
    claimed = set()
    for unit in store.units:
        if unit.isheader() or unit.isobsolete():
            keep.append(unit)
            continue
        stats['units'] += 1
        good, other, ctx = [], [], None
        for loc in unit.getlocations():
            hit = None
            for cand in candidates(loc):
                if cand in index:
                    hit = cand
                    break
            if hit is None:
                if norm(loc) not in other:
                    other.append(norm(loc))
            elif index[hit][1] == unit.source:
                if hit not in good:
                    good.append(hit)
                if ctx is None:
                    ctx = index[hit][0]
                claimed.add(index[hit][0] + u'\x04' + unit.source)
            else:
                stats['locations_removed'] += 1
        keep.append(unit)
        if good:
            if good != unit.getlocations():
                set_locations(unit, good)
            if ctx != unit.getcontext():
                if ctx:
                    unit.setcontext(ctx)
                else:
                    unit.msgctxt = []
                stats['rewritten'] += 1
        else:
            if other != unit.getlocations():
                set_locations(unit, other)
            stats['unplaced'] += 1
            unplaced.append(unit)
    # pomerge falls back to the first unit with the same source; keep that
    # from overwriting a unit that was already placed by its location
    drop = set(id(u) for u in unplaced
               if u.source in first
               and first[u.source] + u'\x04' + u.source in claimed)
    stats['shadowed_dropped'] += len(drop)
    store.units = [u for u in keep if id(u) not in drop]


def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument('--template', required=True)
    ap.add_argument('--input', required=True)
    ap.add_argument('--output', required=True)
    args = ap.parse_args()

    keys = ('files', 'no_template', 'units', 'rewritten', 'locations_removed',
            'unplaced', 'shadowed_dropped')
    stats = dict((k, 0) for k in keys)
    for root, _dirs, files in os.walk(args.input):
        for name in sorted(files):
            if not name.endswith('.po'):
                continue
            src = os.path.join(root, name)
            rel = os.path.relpath(src, args.input)
            dst = os.path.join(args.output, rel)
            if not os.path.isdir(os.path.dirname(dst)):
                os.makedirs(os.path.dirname(dst))
            stats['files'] += 1
            pot = os.path.join(args.template, rel[:-3] + '.pot')
            store = factory.getobject(src)
            if os.path.exists(pot):
                fix_store(store, template_index(pot), stats)
            else:
                stats['no_template'] += 1
            with io.open(dst, 'wb') as out:
                store.serialize(out)
    for key in keys:
        print('%s: %d' % (key, stats[key]))
    return 0


if __name__ == '__main__':
    sys.exit(main())
