# Maya Clone — Component Specs

**Source:** `replica/recon.md` component inventory + `CommonComponents.kt` + 51 screens
**Tokens:** `replica/design/tokens.json` | `replica/design/tokens.css`

---

## 1. BackTopAppBar

**Variants:**
- Default (title + back)
- With actions (trailing icons/menu)
- With search (search field in title area)

**Sizes:** Full width, height 64dp

**States:**
- Default
- Scrolled (elevation increases, shadow appears)
- Loading (spinner in actions)

**Tokens:**
- `containerColor`: `surface` (#0F0F1A)
- `title`: `text` (#F8FAFC), `title-medium` (16sp, 600)
- `navigationIcon`: `text-muted` (#94A3B8)
- `actionIcons`: `text-muted` (#94A3B8)
- `elevation`: `elevation-1` → `elevation-3` on scroll

**A11y:**
- Real `<TopAppBar>` with proper semantics
- Back button: `contentDescription="Navigate up"`
- Focus order: back → title → actions
- Touch target ≥ 48dp

**Used on:** All screens (S01–S56)

**Compose:** `Components.BackTopAppBar` (line 228)

---

## 2. SettingsRow

**Variants:**
- Default (label only)
- With subtitle
- With leading icon
- With trailing (toggle, chevron, badge, custom)
- Clickable (navigates) / Non-clickable (info only)

**States:**
- Default
- Pressed (ripple on `surface-card` with `accent` at 12%)
- Focused (focus ring `accent` 2dp)
- Disabled (opacity 38%, no ripple)

**Tokens:**
- `background`: `surface-card` (#1A1A2E)
- `label`: `text` (#F8FAFC), `settings-label` (16sp, 500)
- `subtitle`: `text-muted` (#94A3B8), `settings-subtitle` (13sp, 400)
- `leadingIconBg`: `accent` at 10% (#7C4DFF1A), `radius-full`
- `leadingIcon`: `accent` (#7C4DFF)
- `trailingGap`: 8dp
- `verticalPadding`: 8dp
- `horizontalPadding`: 16dp (implicit via Row)

**A11y:**
- Clickable: real `Modifier.clickable` + `Role.Button`
- Non-clickable: `Role.Text`
- Focus visible with 2dp `accent` ring
- Subtitle announced as description

**Used on:** All settings screens (S04–S32)

**Compose:** `Components.SettingsRow` (line 23)

---

## 3. SettingsToggle

**Variants:** Single (switch)

**Sizes:** Track 36×36dp, thumb ~20dp

**States:**
- Off (default)
- On (checked)
- Disabled
- Focused
- Pressed

**Tokens:**
- `background`: `surface-card` (#1A1A2E)
- `label`: `text` (#F8FAFC), `settings-label` (16sp, 500)
- `subtitle`: `text-muted` (#94A3B8), `settings-subtitle` (13sp, 400)
- `trackOff`: `text-dim` (#64748B) stroke 2dp, `radius-full`
- `trackOn`: `accent` (#7C4DFF) stroke 2dp, `radius-full`
- `thumbOn`: `accent` (#7C4DFF) fill, rect 3×12dp at right
- `trackBg`: `accent` at 20% (#7C4DFF33), `radius-18`
- `verticalPadding`: 8dp

**A11y:**
- Real `Switch` role (custom canvas but semantics applied)
- `stateDescription` for on/off
- Touch target 48×48dp (row full width clickable)
- Focus ring 2dp `accent`

**Used on:** Settings screens with boolean prefs (S06, S12, S14, S18, S20, S21, S22, S25, S28, S29, S30, S31, S32)

**Compose:** `Components.SettingsToggle` (line 83)

---

## 4. SettingsSlider

**Variants:** Continuous / Stepped

**States:**
- Default
- Dragging (active)
- Focused
- Disabled

**Tokens:**
- `label`: `text-muted` (#94A3B8), `slider-label` (14sp, 400)
- `valueLabel`: `text` (#F8FAFC), `slider-value` (14sp, 500)
- `track`: Material3 default (uses `accent` via theme)
- `thumb`: Material3 default
- `activeTrack`: `accent` (#7C4DFF)
- `inactiveTrack`: `border` (#6A6A9A)

**A11y:**
- Material3 `Slider` — built-in semantics
- `contentDescription` with current value
- Step announcements for stepped variant
- Focus ring on thumb

**Used on:** S14 (voice engine), S19 (appearance), S21 (typing), S28 (touch guard)

**Compose:** `Components.SettingsSlider` (line 163)

---

## 5. SectionHeader

**Variants:** Default (title + divider)

**States:** Default only

**Tokens:**
- `title`: `accent` (#7C4DFF), `section-header` (14sp, 700), `h6` style
- `divider`: `border` (#6A6A9A), 1dp height
- `marginBottom`: 8dp (implicit via Column)

**A11y:**
- Heading level 2 (`semantics { heading() }`)
- Divider is decorative (`semantics { hidden() }`)

**Used on:** All settings screens (S04–S32)

**Compose:** `Components.SectionHeader` (line 214)

---

## 6. AgentCard

**Variants:**
- Default
- Selected (border `accent` 2dp)
- Loading (skeleton)

**Sizes:** ~160×200dp (responsive)

**States:**
- Default
- Pressed (ripple `accent` 12%)
- Focused (focus ring `accent` 2dp)
- Selected
- Loading

**Tokens:**
- `background`: `surface-card` (#1A1A2E)
- `border`: `border` (#6A6A9A) default, `accent` (#7C4DFF) selected
- `radius`: `radius-card` (16dp)
- `avatar`: 64dp, `radius-full`, `accent` bg at 10%
- `name`: `text` (#F8FAFC), `title-medium` (16sp, 500)
- `model`: `text-muted` (#94A3B8), `body-small` (12sp, 400)
- `tagChips`: `surface-elevated` (#16162A), `text-muted`, `radius-chip`
- `elevation`: `elevation-1`

**A11y:**
- Clickable → `Role.Button`
- Selected state announced
- Loading: `liveRegion="polite"` with "Loading agent"

**Used on:** S01 (Home), S03 (AgentPicker)

---

## 7. MessageBubble

**Variants:**
- User (right-aligned, `accent` bg)
- Assistant (left-aligned, `surface-card` bg)
- System (centered, `text-muted`, smaller)
- Error (left-aligned, `danger` bg, `danger-on` text)

**States:**
- Default
- Streaming (animated ellipsis, `accent` pulse)
- Failed (retry action, `danger` accent)
- Selected (long-press → contextual menu)

**Tokens:**
- `userBg`: `accent` (#7C4DFF)
- `userText`: `accent-on` (#F8FAFC)
- `assistantBg`: `surface-card` (#1A1A2E)
- `assistantText`: `text` (#F8FAFC)
- `systemText`: `text-muted` (#94A3B8), `body-small`
- `errorBg`: `danger` (#DC2626) at 20%
- `errorText`: `danger-on` (#F8FAFC)
- `radius`: `radius-lg` (16dp), bottom corner 4dp on sender side
- `padding`: 12dp horizontal, 8dp vertical
- `maxWidth`: 75% screen width
- `tail`: 16dp (custom shape)

**A11y:**
- `role="article"` with `aria-label="Message from {sender}"`
- Streaming: `aria-live="polite"` for new tokens
- Failed: `aria-live="assertive"` for error
- Copy action in context menu
- Time announced as metadata

**Used on:** S02 (ChatScreen)

---

## 8. VoiceButton

**Variants:**
- STT (microphone)
- TTS (speaker)
- Combined (mic when idle, stop when listening, speaker when speaking)

**Sizes:** 56×56dp (FAB), 40×40dp (inline)

**States:**
- Idle (default)
- Listening (pulsing `accent` ring, mic icon animated)
- Processing (spinner, `accent-secondary`)
- Speaking (speaker icon, `accent-tertiary` pulse)
- Error (shake, `danger`)
- Disabled

**Tokens:**
- `idleBg`: `surface-card` (#1A1A2E)
- `idleIcon`: `text-muted` (#94A3B8)
- `listeningBg`: `accent` at 15% (#7C4DFF26)
- `listeningRing`: `accent` (#7C4DFF) pulse animation
- `listeningIcon`: `accent` (#7C4DFF)
- `processingBg`: `accent-secondary` at 15%
- `processingIcon`: `accent-secondary` (#22D3EE)
- `speakingBg`: `accent-tertiary` at 15%
- `speakingIcon`: `accent-tertiary` (#D81B60)
- `errorBg`: `danger` at 15%
- `errorIcon`: `danger` (#DC2626)
- `radius`: `radius-full`
- `elevation`: `elevation-2` idle, `elevation-4` active

**A11y:**
- `Role.Button` with dynamic label: "Start voice input", "Stop listening", "Speaking, tap to stop"
- `aria-live="polite"` for state changes
- Haptic feedback on state change
- Large touch target (56dp)

**Used on:** S02 (ChatScreen)

---

## 9. Chip

**Variants:**
- Filter (selectable)
- Action (button-like)
- Status (read-only, colored)
- Input (with remove button)

**Sizes:** Height 32dp (sm), 40dp (md)

**States:**
- Default
- Selected (filter)
- Pressed
- Focused
- Disabled

**Tokens:**
- `filterBg`: `surface-card` (#1A1A2E)
- `filterSelectedBg`: `accent` at 20% (#7C4DFF33)
- `filterBorder`: `border` (#6A6A9A) default, `accent` (#7C4DFF) selected
- `actionBg`: `accent` (#7C4DFF)
- `actionText`: `accent-on` (#F8FAFC)
- `statusBg`: category color at 20%
- `statusText`: category color
- `radius`: `radius-chip` (9999dp)
- `padding`: 8dp horizontal, 4dp vertical
- `iconGap`: 4dp

**A11y:**
- Filter: `Role.Switch` with `aria-pressed`
- Action: `Role.Button`
- Status: `Role.Img` with label
- Remove: `Role.Button` with `aria-label="Remove {label}"`

**Used on:** S01 (categories), S03 (agent tags), S09 (skill categories)

---

## 10. EmptyState

**Variants:**
- Illustration + title + action (primary)
- Illustration + title + subtitle (no action)
- Minimal (text only)

**States:** Default only

**Tokens:**
- `illustration`: `text-dim` (#64748B), 120dp
- `title`: `text` (#F8FAFC), `title-large` (22sp, 500)
- `subtitle`: `text-muted` (#94A3B8), `body-medium` (14sp, 400)
- `action`: `Button` primary variant
- `spacing`: 16dp between elements
- `padding`: 32dp horizontal

**A11y:**
- Illustration: `contentDescription="@null"` (decorative)
- Title: heading level 2
- Action: standard button semantics

**Used on:** S01 (no agents), S03 (no agents), S09 (no skills), S33 (no awareness events)

---

## 11. LoadingOverlay / Skeleton

**Variants:**
- Full-screen (centered spinner)
- Inline (skeleton placeholders)
- Button (spinner replaces label)

**States:** Active only

**Tokens:**
- `spinner`: `accent` (#7C4DFF), 24dp
- `scrim`: `overlay-scrim` (#000000CC) for full-screen
- `skeletonBg`: `surface-elevated` (#16162A)
- `skeletonHighlight`: `surface-card` (#1A1A2E) animation
- `animation`: 1.5s ease-in-out infinite

**A11y:**
- Full-screen: `role="alertdialog"`, `aria-modal="true"`, `aria-label="Loading"`
- Inline: `aria-busy="true"` on container
- Button: `aria-disabled="true"`, keep label for screen readers

**Used on:** All async screens

---

## 12. BottomSheet

**Variants:**
- Modal (dismiss on backdrop tap)
- Persistent (drag handle, no backdrop dismiss)
- Half-expanded (peek) → Full

**Sizes:** Peek 40%, Half 50%, Full 90% screen height

**States:**
- Closed
- Peek
- Half
- Full
- Dragging

**Tokens:**
- `background`: `surface` (#0F0F1A)
- `handle`: `border` (#6A6A9A), 36×4dp, `radius-full`
- `radius`: `radius-bottom-sheet` (24dp) top only
- `scrim`: `overlay-scrim` (#000000CC)
- `elevation`: `elevation-4`

**A11y:**
- `role="dialog"`, `aria-modal="true"` (modal variant)
- Focus trapped when open
- Drag handle: `Role.Button`, `aria-label="Drag to resize sheet"`
- ESC key closes (modal)
- Back gesture closes

**Used on:** S03 (agent picker), S24 (face capture), S30 (auto-reply builder)

---

## 13. Dialog

**Variants:**
- Alert (title, message, 1-2 actions)
- Confirm (destructive action prominent)
- Input (text field + actions)
- List (radio/checkbox selection)
- Full-screen (complex forms)

**States:**
- Open
- Closing

**Tokens:**
- `background`: `surface` (#0F0F1A)
- `radius`: `radius-dialog` (28dp)
- `title`: `text` (#F8FAFC), `title-large` (22sp, 500)
- `content`: `text` (#F8FAFC), `body-medium` (14sp, 400)
- `primaryAction`: `Button` primary
- `secondaryAction`: `Button` ghost
- `destructiveAction`: `Button` danger
- `scrim`: `overlay-scrim` (#000000CC)
- `elevation`: `elevation-5`

**A11y:**
- `role="alertdialog"` (alert/confirm), `role="dialog"` (input/list)
- `aria-modal="true"`
- Focus trapped, initial focus on first action
- ESC closes (non-destructive)
- Labels associated with inputs

**Used on:** S07 (API key confirm), S25 (face lock fallback), S35 (key rotation)

---

## 14. Card

**Variants:**
- Elevated (shadow)
- Outlined (border)
- Filled (no shadow/border, `surface-card`)

**States:**
- Default
- Pressed (ripple)
- Focused (focus ring)
- Selected (outlined: `accent` border)
- Disabled

**Tokens:**
- `elevatedBg`: `surface-card` (#1A1A2E), `elevation-1`
- `outlinedBg`: `surface-card` (#1A1A2E), `border` (#6A6A9A) 1dp
- `filledBg`: `surface-card` (#1A1A2E)
- `radius`: `radius-card` (16dp)
- `padding`: 16dp
- `selectedBorder`: `accent` (#7C4DFF) 2dp

**A11y:**
- Clickable: `Role.Button`
- Selected: `aria-pressed="true"`
- Focus ring 2dp `accent`

**Used on:** S09 (skill cards), S11 (WhatsApp groups), S16 (connectors)

---

## 15. NavigationRail

**Variants:** 3–5 destinations with labels

**Sizes:** Width 72dp (compact), 280dp (expanded)

**States:**
- Default (compact, icon only)
- Expanded (icon + label)
- Selected
- Badge (notification count)

**Tokens:**
- `background`: `surface` (#0F0F1A)
- `selectedBg`: `accent` at 15% (#7C4DFF26)
- `iconSelected`: `accent` (#7C4DFF)
- `iconUnselected`: `text-muted` (#94A3B8)
- `label`: `text` (#F8FAFC), `label-small` (11sp, 500)
- `badge`: `danger` (#DC2626), `badge-on` (#F8FAFC), `radius-full`, min 20dp
- `divider`: `border` (#6A6A9A) 1dp bottom

**A11y:**
- `role="navigation"`
- Each item: `Role.Tab`, `aria-selected`
- Badge: `aria-label="{count} notifications"`
- Keyboard: arrow keys navigate, Enter/Space activates

**Used on:** S01 (tablet/landscape)

---

## 16. Button (Primitive)

**Variants:**
- Primary (filled `accent`)
- Secondary (filled `accent-secondary`)
- Tertiary (filled `accent-tertiary`)
- Danger (filled `danger`)
- Ghost (transparent, `text` label)
- Outlined (border `border`, `text` label)

**Sizes:**
- SM: 32dp height, 12dp horizontal padding, `label-small`
- MD: 40dp height, 16dp horizontal padding, `label-large`
- LG: 48dp height, 24dp horizontal padding, `title-medium`

**States:**
- Default
- Hover (web) / Pressed (mobile): overlay `opacity-pressed` (12%)
- Focused: 2dp `focus-ring` outline, offset 2dp
- Disabled: `opacity-disabled` (38%), no interaction
- Loading: spinner (20dp), label hidden from sight but kept for SR

**Tokens (Primary MD):**
- `bg`: `accent` (#7C4DFF)
- `text`: `accent-on` (#F8FAFC)
- `radius`: `radius-button` (12dp)
- `font`: `label-large` (14sp, 500)
- `elevation`: `elevation-1`

**A11y:**
- Real `<Button>` element
- Disabled: `aria-disabled="true"`
- Loading: `aria-busy="true"`, `aria-live="polite"` on spinner
- Focus visible always

---

## 17. TextField (Primitive)

**Variants:**
- Filled (bg `surface-card`, border `border-input`)
- Outlined (bg transparent, border `border-input`)
- Standard (bottom line only)

**States:**
- Default
- Focused (border `accent` 2dp, label floats)
- Error (border `danger`, helper text `danger`)
- Disabled (`opacity-disabled`)
- Filled (value present)

**Tokens:**
- `containerBg`: `surface-card` (#1A1A2E)
- `border`: `border-input` (#7A7AAA) default, `accent` (#7C4DFF) focused, `danger` (#DC2626) error
- `label`: `text-muted` (#94A3B8) → `accent` (#7C4DFF) focused
- `inputText`: `text` (#F8FAFC)
- `placeholder`: `text-dim` (#64748B)
- `helperText`: `text-muted` (#94A3B8) → `danger` (#DC2626) error
- `radius`: `radius-md` (12dp) top corners
- `padding`: 16dp horizontal, 12dp vertical (label floating)

**A11y:**
- `<input>` / `<textarea>` with `id`
- Label: `<label for="id">` or `aria-label`
- Error: `aria-invalid="true"`, `aria-describedby="error-id"`
- Helper: `aria-describedby="helper-id"`

---

## 18. Switch (Primitive)

**Variants:** Standard, Checkbox-style

**Tokens:**
- `trackOff`: `border` (#6A6A9A)
- `trackOn`: `accent` (#7C4DFF)
- `thumb`: `surface` (#0F0F1A) with shadow
- `size`: 36×20dp track, 20dp thumb
- `disabled`: `opacity-disabled`

**A11y:**
- `role="switch"`, `aria-checked`
- Label via `aria-labelledby` or wrapping label

---

## 19. Slider (Primitive)

**Variants:** Continuous, Stepped, Range (dual thumb)

**Tokens:**
- `trackActive`: `accent` (#7C4DFF)
- `trackInactive`: `border` (#6A6A9A)
- `thumb`: `accent` (#7C4DFF), 20dp, `elevation-2`
- `thumbFocused`: `focus-ring` 2dp ring
- `valueLabel`: `label-small`, `accent-on` bg `accent`

**A11y:**
- `role="slider"`, `aria-valuemin`, `aria-valuemax`, `aria-valuenow`
- `aria-orientation="horizontal"`
- Step: `aria-valuetext` for named steps

---

## 20. IconButton (Primitive)

**Variants:** Standard, Toggle, Filled, Outlined

**Sizes:** 40dp (sm), 48dp (md), 56dp (lg)

**Tokens:**
- `icon`: `text-muted` (#94A3B8) default, `text` (#F8FAFC) active
- `bg`: transparent default, `surface-card` filled
- `radius`: `radius-full`
- `ripple`: `accent` at 12%

**A11y:**
- `role="button"`, `aria-label` required
- Toggle: `aria-pressed`
- Touch target ≥ 48dp

---

## Contrast Report Summary

**All 15 pairs PASS AA** (see `contrast.py` output)

| Pair | Ratio | Threshold | Status |
|------|-------|-----------|--------|
| text on bg | 19.17:1 | 4.5:1 | ✓ AAA |
| text-muted on bg | 7.82:1 | 3.0:1 | ✓ AAA |
| text on surface | 18.19:1 | 4.5:1 | ✓ AAA |
| text-muted on surface | 7.42:1 | 3.0:1 | ✓ AAA |
| text on surface-card | 16.30:1 | 4.5:1 | ✓ AAA |
| text-muted on surface-card | 6.65:1 | 3.0:1 | ✓ AAA |
| text-dim on surface-card | 3.58:1 | 3.0:1 | ✓ AA |
| accent-on on accent | 4.60:1 | 4.5:1 | ✓ AA |
| accent-secondary-on on accent-secondary | 11.10:1 | 4.5:1 | ✓ AAA |
| accent-tertiary-on on accent-tertiary | 4.73:1 | 4.5:1 | ✓ AA |
| danger-on on danger | 4.62:1 | 4.5:1 | ✓ AA |
| success-on on success | 7.91:1 | 4.5:1 | ✓ AAA |
| warning-on on warning | 9.34:1 | 3.0:1 | ✓ AAA |
| text on surface-elevated | 16.98:1 | 4.5:1 | ✓ AAA |
| border on surface-card | 3.36:1 | 3.0:1 | ✓ AA |

**AAA warnings (3):** Primary, tertiary, danger buttons — pass AA but not AAA. Acceptable for non-body text.

---

## Next Step

`/replica-build` — implement screens in milestone order (M0 vertical slice first) using these tokens and component specs.