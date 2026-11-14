#!/usr/bin/env python3
"""
WCAG Contrast Checker for design tokens
Checks all fg/bg pairs in tokens.json against AA/AAA thresholds
"""

import json
import sys
import math

def hex_to_rgb(hex_color):
    """Convert hex color to RGB tuple (0-255)"""
    hex_color = hex_color.lstrip('#')
    if len(hex_color) == 3:
        hex_color = ''.join([c*2 for c in hex_color])
    if len(hex_color) == 8:  # ARGB
        hex_color = hex_color[2:]
    return tuple(int(hex_color[i:i+2], 16) for i in (0, 2, 4))

def rgb_to_luminance(r, g, b):
    """Calculate relative luminance per WCAG 2.1"""
    def channel(c):
        c = c / 255.0
        return c / 12.92 if c <= 0.03928 else ((c + 0.055) / 1.055) ** 2.4
    return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)

def contrast_ratio(hex_fg, hex_bg):
    """Calculate contrast ratio between two colors"""
    fg_rgb = hex_to_rgb(hex_fg)
    bg_rgb = hex_to_rgb(hex_bg)
    fg_lum = rgb_to_luminance(*fg_rgb)
    bg_lum = rgb_to_luminance(*bg_rgb)
    lighter = max(fg_lum, bg_lum)
    darker = min(fg_lum, bg_lum)
    return (lighter + 0.05) / (darker + 0.05)

def check_tokens(tokens_path):
    with open(tokens_path) as f:
        tokens = json.load(f)

    color_roles = tokens['color']['roles']
    pairs = tokens['color']['pairs']

    print(f"Checking {len(pairs)} color pairs...\n")

    failures = []
    warnings = []

    for pair in pairs:
        fg_key = pair['fg']
        bg_key = pair['bg']
        min_ratio = pair['min']
        context = pair['context']

        if fg_key not in color_roles or bg_key not in color_roles:
            print(f"  SKIP: {fg_key}/{bg_key} - role not found")
            continue

        fg_hex = color_roles[fg_key]['value']
        bg_hex = color_roles[bg_key]['value']

        ratio = contrast_ratio(fg_hex, bg_hex)
        passes_aa = ratio >= min_ratio
        passes_aaa = ratio >= 7.0 if min_ratio >= 4.5 else ratio >= 4.5

        status = "✓ PASS" if passes_aa else "✗ FAIL"
        aaa_status = " (AAA)" if passes_aaa else ""

        print(f"  {status}{aaa_status} | {fg_key} on {bg_key} | {ratio:.2f}:1 (need {min_ratio}:1) | {context}")
        print(f"         fg={fg_hex} bg={bg_hex}")

        if not passes_aa:
            failures.append({
                'fg': fg_key, 'bg': bg_key,
                'ratio': ratio, 'required': min_ratio,
                'context': context
            })
        elif not passes_aaa and min_ratio >= 4.5:
            warnings.append({
                'fg': fg_key, 'bg': bg_key,
                'ratio': ratio,
                'context': context
            })

    print(f"\n{'='*60}")
    print(f"Results: {len(pairs) - len(failures)}/{len(pairs)} passed AA")
    if failures:
        print(f"Failures: {len(failures)}")
        for f in failures:
            print(f"  - {f['fg']} on {f['bg']}: {f['ratio']:.2f}:1 (need {f['required']}:1) — {f['context']}")
    if warnings:
        print(f"AAA warnings: {len(warnings)} (AA pass but not AAA)")
        for w in warnings:
            print(f"  - {w['fg']} on {w['bg']}: {w['ratio']:.2f}:1 — {w['context']}")

    return len(failures) == 0

if __name__ == '__main__':
    path = sys.argv[1] if len(sys.argv) > 1 else 'replica/design/tokens.json'
    success = check_tokens(path)
    sys.exit(0 if success else 1)