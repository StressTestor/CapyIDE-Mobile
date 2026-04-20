# Vercel Breach Rotation — 2026-04-19 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rotate every non-Sensitive env var across 5 Vercel projects following the 2026-04-19 ShinyHunters/BreachForums disclosure, re-add each with the Sensitive flag set, redeploy, and smoke-test.

**Architecture:** Sequential per-project rotation via Vercel CLI v50+. New random secrets generated with `openssl rand -hex 32`. Supabase keys rotated via the Supabase dashboard (MCP assist available) — requires explicit user approval before each destructive reset. Env vars piped directly into `vercel env add` to avoid stdout leakage. Rollback via `vercel rollback` on smoke-test failure.

**Tech Stack:** Vercel CLI v50.34.1, openssl, curl, bash, Supabase dashboard / MCP (for ppqpazvivjomjyetkacm)

---

## Constraints — read before running a single command

- **SKIP** any var already showing as `Encrypted` in `vercel env ls` output — those are Sensitive-flagged and were not exposed
- **NEVER** echo or print a secret value to stdout, write it to a file, or commit it
- **STOP AND ASK** before any Supabase key reset — this is irreversible and breaks existing integrations until the new key is deployed
- **DO NOT roll back** capy-ide if its smoke test fails — that project's build was already broken before this rotation; rolling back would restore exposed secrets
- **Roll back** all other projects with `vercel rollback` on smoke-test failure, then surface logs
- Team scope for every Vercel CLI command: `--scope joes-projects-65e9bd31`

---

## File map (no code files created — this is ops, not a code change)

| Location | Purpose |
|----------|---------|
| `/tmp/breach-rotation-log.txt` | Running ops log (local only, never committed) |
| `/Users/joesephgrey/docs/superpowers/specs/2026-04-19-vercel-breach-summary.md` | Final summary report written in Task 7 |
| `{each-repo}/docs/superpowers/specs/2026-04-19-vercel-breach-rotation.md` | Copy of this plan placed in each local repo after execution |

---

## Task 0: Preflight

### Task 0.1: Verify Vercel CLI auth and list projects

**Files:** none

- [ ] **Step 1: Verify auth**

```bash
vercel whoami --scope joes-projects-65e9bd31
```

Expected: prints your username and confirms the team. If it fails: run `vercel login` first.

- [ ] **Step 2: List all projects and note exact names**

```bash
vercel ls --scope joes-projects-65e9bd31
```

Expected output includes: `calibratediq`, `byli`, `ratiochat`, `capy-ide`, `social-media-post-previewer`. Write down the exact names as printed — they're used verbatim in every `--project` flag below.

- [ ] **Step 3: Initialize ops log**

```bash
echo "=== Vercel breach rotation log ===" > /tmp/breach-rotation-log.txt
echo "Started: $(date -u +%Y-%m-%dT%H:%M:%SZ)" >> /tmp/breach-rotation-log.txt
echo "Operator: $(vercel whoami --scope joes-projects-65e9bd31 2>/dev/null)" >> /tmp/breach-rotation-log.txt
```

---

## Task 1: calibratediq (PRIORITY — deployed yesterday)

**Project:** `calibratediq` | **Domain:** `calibratediq.org` | **Stack:** Next.js | **Local:** `/Volumes/onn/calibratediq`

### Task 1.1: Enumerate non-Sensitive vars

- [ ] **Step 1: List all production env vars**

```bash
vercel env ls production --scope joes-projects-65e9bd31 --project calibratediq
```

Read the output. The Vercel CLI marks Sensitive vars as `Encrypted` in the Value column. Plain-text values (or values shown as masked-but-not-encrypted) must be rotated.

**Expected vars to find (rotate if not already Encrypted):**
- `RESULT_SIGNING_SECRET` — HMAC signing key for result URLs. Rotate unconditionally if plain.
- Any `ANTHROPIC_API_KEY`, `OPENAI_API_KEY`, or other AI provider keys — rotate if plain.
- Any analytics write-keys or backend tokens — rotate if plain.
- `NEXT_PUBLIC_*` vars — these are public by design. Skip unless they contain an actual secret (e.g., `NEXT_PUBLIC_SUPABASE_ANON_KEY` is safe; a `NEXT_PUBLIC_STRIPE_SECRET_KEY` would not be).

- [ ] **Step 2: Write your rotation list to the ops log**

```bash
echo "--- calibratediq ---" >> /tmp/breach-rotation-log.txt
echo "Non-Sensitive vars to rotate: [fill in from Step 1 output]" >> /tmp/breach-rotation-log.txt
```

### Task 1.2: Rotate RESULT_SIGNING_SECRET

- [ ] **Step 1: Generate new HMAC secret (do not echo)**

```bash
NEW_SECRET=$(openssl rand -hex 32)
```

- [ ] **Step 2: Remove old secret**

```bash
vercel env rm RESULT_SIGNING_SECRET production --scope joes-projects-65e9bd31 --project calibratediq -y
```

Expected: `Removed Environment Variable RESULT_SIGNING_SECRET from Production`

- [ ] **Step 3: Add new secret with Sensitive flag**

```bash
printf '%s' "$NEW_SECRET" | vercel env add RESULT_SIGNING_SECRET production \
  --scope joes-projects-65e9bd31 --project calibratediq --sensitive
unset NEW_SECRET
```

Expected: `Added Environment Variable RESULT_SIGNING_SECRET to Production`

- [ ] **Step 4: Verify it is now Encrypted**

```bash
vercel env ls production --scope joes-projects-65e9bd31 --project calibratediq
```

Confirm `RESULT_SIGNING_SECRET` shows as `Encrypted`. If it still shows a plain value, the `--sensitive` flag did not take — remove and re-add.

### Task 1.3: Rotate any other non-Sensitive vars found

For **each additional** non-Sensitive, non-public var identified in Task 1.1, repeat this pattern:

- [ ] **Step 1: Generate replacement**

For random secrets (signing keys, tokens, webhook secrets):
```bash
NEW_VALUE=$(openssl rand -hex 32)
```

For provider API keys (Anthropic, OpenAI, analytics): go to the upstream provider's dashboard, revoke the old key, generate a new one, then set it in `$NEW_VALUE` — do not copy-paste into the terminal if you can avoid it; use pbpaste or a password manager CLI to pipe directly.

- [ ] **Step 2: Remove old var**

```bash
vercel env rm REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project calibratediq -y
```

- [ ] **Step 3: Add new var with Sensitive flag**

```bash
printf '%s' "$NEW_VALUE" | vercel env add REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project calibratediq --sensitive
unset NEW_VALUE
```

### Task 1.4: Redeploy and smoke test

- [ ] **Step 1: Trigger production redeploy**

```bash
vercel redeploy --prod --scope joes-projects-65e9bd31 --project calibratediq
```

If that fails because `--project` is unsupported by `vercel redeploy`, fall back to the local clone:
```bash
cd /Volumes/onn/calibratediq && vercel redeploy --prod --scope joes-projects-65e9bd31
```

- [ ] **Step 2: Poll for READY status**

```bash
for i in $(seq 1 24); do
  STATUS=$(vercel ls --scope joes-projects-65e9bd31 --project calibratediq 2>/dev/null \
    | grep -m1 -iE "ready|error|building" | head -1)
  echo "$(date +%H:%M:%S) | $STATUS"
  echo "$STATUS" | grep -qi "ready" && break
  sleep 10
done
```

- [ ] **Step 3: Smoke test**

```bash
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" https://calibratediq.org/)
echo "calibratediq.org → HTTP $HTTP_CODE"
```

Expected: `200`

If NOT 200:
```bash
vercel rollback --scope joes-projects-65e9bd31 --project calibratediq
vercel logs --scope joes-projects-65e9bd31 --project calibratediq 2>&1 | tail -40
```

- [ ] **Step 4: Log result**

```bash
echo "calibratediq | smoke: $HTTP_CODE | $(date -u +%Y-%m-%dT%H:%M:%SZ)" >> /tmp/breach-rotation-log.txt
```

### Task 1.5: Place per-repo plan copy

- [ ] **Step 1: Create spec dir and copy plan**

```bash
mkdir -p /Volumes/onn/calibratediq/docs/superpowers/specs
cp /Users/joesephgrey/docs/superpowers/specs/2026-04-19-vercel-breach-rotation.md \
  /Volumes/onn/calibratediq/docs/superpowers/specs/
```

### Task 1.6: Fix git identity in calibratediq repo

⚠️ This sets future-commit identity only. Past commits retain the real name + hostname — history is NOT rewritten.

- [ ] **Step 1: Set per-repo git identity**

```bash
cd /Volumes/onn/calibratediq
git config user.name "StressTestor"
git config user.email "StressTestor@users.noreply.github.com"
git config --local user.name
git config --local user.email
```

Expected:
```
StressTestor
StressTestor@users.noreply.github.com
```

- [ ] **Step 2: Commit the spec file**

```bash
cd /Volumes/onn/calibratediq
git add docs/superpowers/specs/2026-04-19-vercel-breach-rotation.md
git status
```

Only commit if the repo is clean otherwise. Message to use: `docs(security): add vercel breach rotation spec 2026-04-19`

```bash
git commit -m "docs(security): add vercel breach rotation spec 2026-04-19"
```

---

## Task 2: byli / linkdrift.app

**Project:** `byli` | **Domain:** `linkdrift.app` | **Stack:** Next.js | **Local:** `/Volumes/onn/byli-work` | **Supabase project:** `ppqpazvivjomjyetkacm`

⚠️ **SUPABASE PAUSE POINT:** This project uses Supabase. Rotating `SUPABASE_SERVICE_ROLE_KEY` requires resetting it in the Supabase dashboard — the old key stops working the moment you hit reset. Do not proceed past Task 2.2 until Joe explicitly confirms.

### Task 2.1: Enumerate non-Sensitive vars

- [ ] **Step 1: List all production env vars**

```bash
vercel env ls production --scope joes-projects-65e9bd31 --project byli
```

**Expected vars (rotate if NOT Encrypted):**
- `SUPABASE_SERVICE_ROLE_KEY` — backend-only secret. Rotate with Supabase dashboard approval. **STOP before this one.**
- `CRON_SECRET` or similar cron/webhook signing key — rotate with `openssl rand -hex 32`
- `NEXTAUTH_SECRET` / `JWT_SECRET` — rotate with `openssl rand -hex 32`
- Any remaining Twitter/X API credentials — likely unused since scraper migration; **offer to delete** instead of rotate
- `NEXT_PUBLIC_SUPABASE_URL` — public URL, no secret; **skip**
- `NEXT_PUBLIC_SUPABASE_ANON_KEY` — public anon key, RLS enforces access; **skip** (anon key exposure is expected)

- [ ] **Step 2: Log the rotation list**

```bash
echo "--- byli ---" >> /tmp/breach-rotation-log.txt
echo "Non-Sensitive vars to rotate: [fill in]" >> /tmp/breach-rotation-log.txt
echo "Vars to delete (unused): [fill in]" >> /tmp/breach-rotation-log.txt
```

### Task 2.2: Delete unused leftover vars

For any vars that are unused (e.g., `TWITTER_API_KEY` left from old scraper):

- [ ] **Step 1: Confirm the var is unused**

Check the codebase:
```bash
grep -r "TWITTER_API_KEY\|TWITTERAPI" /Volumes/onn/byli-work/src 2>/dev/null
```

If the grep returns nothing, the var is safe to delete.

- [ ] **Step 2: Delete the unused var**

```bash
vercel env rm REPLACE_WITH_UNUSED_VAR production \
  --scope joes-projects-65e9bd31 --project byli -y
```

Log it:
```bash
echo "Deleted unused var: REPLACE_WITH_UNUSED_VAR" >> /tmp/breach-rotation-log.txt
```

### Task 2.3: STOP — Supabase service role key rotation (requires Joe's confirmation)

**Before running anything in this task, surface the following to Joe:**

> `SUPABASE_SERVICE_ROLE_KEY` is a plain (non-Sensitive) env var in the Vercel dashboard for `byli`. It was potentially exposed in the breach.
>
> Rotating it requires:
> 1. Resetting the key in the Supabase dashboard for project `ppqpazvivjomjyetkacm`
> 2. The old key stops working immediately on reset
> 3. The new key must be deployed to Vercel before any server-side Supabase operations work again
>
> If you're on a live cron job schedule, wait until it's between runs. The window is short.
>
> **Proceed with Supabase service role key rotation? [Y/N]**

**If Joe says NO:** Skip to Task 2.4. Log `SUPABASE_SERVICE_ROLE_KEY: not rotated — user declined`.

**If Joe says YES:** Continue below.

- [ ] **Step 1: Reset the service role key in Supabase**

Option A — via Supabase MCP (if connected):
Use `mcp__claude_ai_Supabase__get_project` with project ref `ppqpazvivjomjyetkacm` to confirm project is live, then navigate the user to reset via dashboard. The MCP does not have a direct key-reset command.

Option B — manual:
1. Open https://supabase.com/dashboard/project/ppqpazvivjomjyetkacm/settings/api
2. Under "Project API keys", click "Reset" next to `service_role`
3. Copy the new key immediately — it's shown only once

**Assign the new key to a shell variable — do NOT print it:**
```bash
read -rs NEW_SERVICE_KEY
# (paste the new key at the prompt, press Enter)
```

- [ ] **Step 2: Remove old service role key from Vercel**

```bash
vercel env rm SUPABASE_SERVICE_ROLE_KEY production \
  --scope joes-projects-65e9bd31 --project byli -y
```

- [ ] **Step 3: Add new service role key as Sensitive**

```bash
printf '%s' "$NEW_SERVICE_KEY" | vercel env add SUPABASE_SERVICE_ROLE_KEY production \
  --scope joes-projects-65e9bd31 --project byli --sensitive
unset NEW_SERVICE_KEY
```

- [ ] **Step 4: Verify Encrypted**

```bash
vercel env ls production --scope joes-projects-65e9bd31 --project byli
```

Confirm `SUPABASE_SERVICE_ROLE_KEY` shows as `Encrypted`.

### Task 2.4: Rotate other non-Sensitive, non-Supabase vars

For each remaining plain var (CRON_SECRET, NEXTAUTH_SECRET, JWT_SECRET, etc.):

- [ ] **Step 1: Generate replacement**

```bash
NEW_VALUE=$(openssl rand -hex 32)
```

- [ ] **Step 2: Remove and re-add with Sensitive flag**

```bash
vercel env rm REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project byli -y
printf '%s' "$NEW_VALUE" | vercel env add REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project byli --sensitive
unset NEW_VALUE
```

### Task 2.5: Redeploy and smoke test

- [ ] **Step 1: Trigger production redeploy**

```bash
vercel redeploy --prod --scope joes-projects-65e9bd31 --project byli
```

Fallback if `--project` is unsupported:
```bash
cd /Volumes/onn/byli-work && vercel redeploy --prod --scope joes-projects-65e9bd31
```

- [ ] **Step 2: Poll for READY**

```bash
for i in $(seq 1 24); do
  STATUS=$(vercel ls --scope joes-projects-65e9bd31 --project byli 2>/dev/null \
    | grep -m1 -iE "ready|error|building" | head -1)
  echo "$(date +%H:%M:%S) | $STATUS"
  echo "$STATUS" | grep -qi "ready" && break
  sleep 10
done
```

- [ ] **Step 3: Smoke test**

```bash
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" https://linkdrift.app/)
echo "linkdrift.app → HTTP $HTTP_CODE"
```

Expected: `200`

On failure:
```bash
vercel rollback --scope joes-projects-65e9bd31 --project byli
vercel logs --scope joes-projects-65e9bd31 --project byli 2>&1 | tail -40
```

- [ ] **Step 4: Log result**

```bash
echo "byli/linkdrift.app | smoke: $HTTP_CODE | $(date -u +%Y-%m-%dT%H:%M:%SZ)" >> /tmp/breach-rotation-log.txt
```

### Task 2.6: Place per-repo plan copy and fix git identity

- [ ] **Step 1: Copy plan**

```bash
mkdir -p /Volumes/onn/byli-work/docs/superpowers/specs
cp /Users/joesephgrey/docs/superpowers/specs/2026-04-19-vercel-breach-rotation.md \
  /Volumes/onn/byli-work/docs/superpowers/specs/
```

- [ ] **Step 2: Set per-repo git identity**

```bash
cd /Volumes/onn/byli-work
git config user.name "StressTestor"
git config user.email "StressTestor@users.noreply.github.com"
git config --local user.name && git config --local user.email
```

Expected:
```
StressTestor
StressTestor@users.noreply.github.com
```

- [ ] **Step 3: Commit spec file**

```bash
cd /Volumes/onn/byli-work
git add docs/superpowers/specs/2026-04-19-vercel-breach-rotation.md
git commit -m "docs(security): add vercel breach rotation spec 2026-04-19"
```

---

## Task 3: ratiochat (dormant)

**Project:** `ratiochat` | **Domain:** `ratiochat.app` | **Stack:** Express | **Local:** not found locally — use `--project` flag

Since this project is dormant, prefer **deleting** unused vars over rotating them. Only rotate vars that are needed for the app to respond at all.

### Task 3.1: Enumerate non-Sensitive vars

- [ ] **Step 1: List all production env vars**

```bash
vercel env ls production --scope joes-projects-65e9bd31 --project ratiochat
```

- [ ] **Step 2: Classify each non-Sensitive var**

For each plain var:
- If it's a session/JWT/signing secret and the app is serving traffic → rotate
- If the app is truly dormant and the var is unused → delete
- If unsure → surface to Joe: "Found `VAR_NAME` in ratiochat — rotate or delete?"

Log the classification:
```bash
echo "--- ratiochat ---" >> /tmp/breach-rotation-log.txt
echo "Rotate: [list] | Delete: [list] | Ask Joe: [list]" >> /tmp/breach-rotation-log.txt
```

### Task 3.2: Delete unused vars

For each var confirmed unused:

- [ ] **Step 1: Delete**

```bash
vercel env rm REPLACE_WITH_UNUSED_VAR production \
  --scope joes-projects-65e9bd31 --project ratiochat -y
echo "Deleted: REPLACE_WITH_UNUSED_VAR" >> /tmp/breach-rotation-log.txt
```

### Task 3.3: Rotate required vars

For each var that must be kept:

- [ ] **Step 1: Generate replacement**

```bash
NEW_VALUE=$(openssl rand -hex 32)
```

- [ ] **Step 2: Remove and re-add**

```bash
vercel env rm REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project ratiochat -y
printf '%s' "$NEW_VALUE" | vercel env add REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project ratiochat --sensitive
unset NEW_VALUE
```

### Task 3.4: Redeploy and smoke test

- [ ] **Step 1: Redeploy**

```bash
vercel redeploy --prod --scope joes-projects-65e9bd31 --project ratiochat
```

- [ ] **Step 2: Poll for READY**

```bash
for i in $(seq 1 24); do
  STATUS=$(vercel ls --scope joes-projects-65e9bd31 --project ratiochat 2>/dev/null \
    | grep -m1 -iE "ready|error|building" | head -1)
  echo "$(date +%H:%M:%S) | $STATUS"
  echo "$STATUS" | grep -qi "ready" && break
  sleep 10
done
```

- [ ] **Step 3: Smoke test**

```bash
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" https://ratiochat.app/)
echo "ratiochat.app → HTTP $HTTP_CODE"
```

Expected: `200`

On failure:
```bash
vercel rollback --scope joes-projects-65e9bd31 --project ratiochat
vercel logs --scope joes-projects-65e9bd31 --project ratiochat 2>&1 | tail -40
```

- [ ] **Step 4: Log result**

```bash
echo "ratiochat | smoke: $HTTP_CODE | $(date -u +%Y-%m-%dT%H:%M:%SZ)" >> /tmp/breach-rotation-log.txt
```

---

## Task 4: capy-ide (dormant — last deploy ERROR'd Nov 2025)

**Project:** `capy-ide` | **Domain:** `capyide.dev` | **Stack:** Next.js | **Local:** `/Volumes/onn/CapyIDE-Mobile` (web portion, if applicable)

⚠️ **The smoke test will likely fail** because the build was broken before this rotation. That is expected and does not indicate a rotation problem. **Do NOT roll back if the smoke test returns non-200.** Rolling back would restore exposed secrets. Surface the build error to Joe for a separate fix.

### Task 4.1: Enumerate non-Sensitive vars

- [ ] **Step 1: List all production env vars**

```bash
vercel env ls production --scope joes-projects-65e9bd31 --project capy-ide
```

- [ ] **Step 2: Classify each non-Sensitive var**

Same rule as ratiochat: dormant project → prefer delete over rotate. Surface unknowns to Joe.

```bash
echo "--- capy-ide ---" >> /tmp/breach-rotation-log.txt
echo "Rotate: [list] | Delete: [list] | Ask Joe: [list]" >> /tmp/breach-rotation-log.txt
```

### Task 4.2: Delete unused vars

- [ ] **Step 1: Delete each unused var**

```bash
vercel env rm REPLACE_WITH_UNUSED_VAR production \
  --scope joes-projects-65e9bd31 --project capy-ide -y
```

### Task 4.3: Rotate required vars

- [ ] **Step 1: Generate replacement**

```bash
NEW_VALUE=$(openssl rand -hex 32)
```

- [ ] **Step 2: Remove and re-add**

```bash
vercel env rm REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project capy-ide -y
printf '%s' "$NEW_VALUE" | vercel env add REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project capy-ide --sensitive
unset NEW_VALUE
```

### Task 4.4: Attempt redeploy and note result (no rollback)

- [ ] **Step 1: Redeploy**

```bash
vercel redeploy --prod --scope joes-projects-65e9bd31 --project capy-ide
```

This may surface the pre-existing build error. That's expected.

- [ ] **Step 2: Smoke test (non-200 expected)**

```bash
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" https://capyide.dev/)
echo "capyide.dev → HTTP $HTTP_CODE (non-200 expected due to pre-existing broken build)"
echo "capy-ide | smoke: $HTTP_CODE (expected failure, pre-existing) | $(date -u +%Y-%m-%dT%H:%M:%SZ)" \
  >> /tmp/breach-rotation-log.txt
```

**DO NOT run `vercel rollback` here** — the old deployment had the exposed secrets. Forward-only.

### Task 4.5: Place per-repo plan copy

- [ ] **Step 1: Copy plan to CapyIDE-Mobile repo**

```bash
mkdir -p /Volumes/onn/CapyIDE-Mobile/docs/superpowers/specs
cp /Users/joesephgrey/docs/superpowers/specs/2026-04-19-vercel-breach-rotation.md \
  /Volumes/onn/CapyIDE-Mobile/docs/superpowers/specs/
cd /Volumes/onn/CapyIDE-Mobile
git add docs/superpowers/specs/2026-04-19-vercel-breach-rotation.md
git commit -m "docs(security): add vercel breach rotation spec 2026-04-19"
```

Note: `ratiochat` and `social-media-post-previewer` have no local clones found on this machine. Plan copy for those two projects is skipped — they can be added if the repos are cloned later.

---

## Task 5: social-media-post-previewer (dormant, no custom domain)

**Project:** `social-media-post-previewer` | **Domain:** `*.vercel.app` | **Stack:** Vite | **Local:** none

Vite `VITE_*` vars are embedded in the browser bundle at build time — they're public by definition. This project likely has zero actual secrets. Verify and handle accordingly.

### Task 5.1: Enumerate non-Sensitive vars

- [ ] **Step 1: List all production env vars**

```bash
vercel env ls production --scope joes-projects-65e9bd31 --project social-media-post-previewer
```

- [ ] **Step 2: Classify**

- `VITE_*` vars — public, no rotation needed (they end up in the browser bundle)
- Any non-`VITE_*` secret var — rotate if not Encrypted

```bash
echo "--- social-media-post-previewer ---" >> /tmp/breach-rotation-log.txt
echo "Non-VITE, non-Sensitive vars found: [list or 'none']" >> /tmp/breach-rotation-log.txt
```

### Task 5.2: Rotate any non-VITE, non-Sensitive vars (if any)

If any are found:

- [ ] **Step 1: Generate replacement and rotate**

```bash
NEW_VALUE=$(openssl rand -hex 32)
vercel env rm REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project social-media-post-previewer -y
printf '%s' "$NEW_VALUE" | vercel env add REPLACE_WITH_VAR_NAME production \
  --scope joes-projects-65e9bd31 --project social-media-post-previewer --sensitive
unset NEW_VALUE
```

If none found: skip this task and log "social-media-post-previewer: no non-VITE secrets found, nothing to rotate."

### Task 5.3: Redeploy and smoke test

- [ ] **Step 1: Redeploy**

```bash
vercel redeploy --prod --scope joes-projects-65e9bd31 --project social-media-post-previewer
```

- [ ] **Step 2: Get the .vercel.app URL**

```bash
DEPLOY_URL=$(vercel ls --scope joes-projects-65e9bd31 --project social-media-post-previewer 2>/dev/null \
  | grep -o 'https://[^ ]*\.vercel\.app' | head -1)
echo "Deploy URL: $DEPLOY_URL"
```

- [ ] **Step 3: Smoke test**

```bash
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$DEPLOY_URL")
echo "social-media-post-previewer → $DEPLOY_URL HTTP $HTTP_CODE"
```

Expected: `200`

On failure:
```bash
vercel rollback --scope joes-projects-65e9bd31 --project social-media-post-previewer
```

- [ ] **Step 4: Log result**

```bash
echo "social-media-post-previewer | smoke: $HTTP_CODE | url: $DEPLOY_URL | $(date -u +%Y-%m-%dT%H:%M:%SZ)" \
  >> /tmp/breach-rotation-log.txt
```

---

## Task 6: Additional Cleanup

### Task 6.1: GitHub App installation scope audit

**No automated changes here — this is read + prompt only.**

- [ ] **Step 1: Check GitHub App scope**

Navigate to (user action — agent cannot click in browser without computer-use):
```
https://github.com/settings/installations
```

Find "Vercel" in the installed GitHub Apps list. Check whether its repository access is:
- **"All repositories"** — flag immediately
- **"Only select repositories"** — note which repos are listed; confirm all 5 Vercel project repos are included and nothing extra

- [ ] **Step 2: If scoped to "All repositories" — surface to Joe**

If the Vercel GitHub App has access to all repositories:

> **Joe — action needed:** The Vercel GitHub App is currently scoped to ALL repositories under StressTestor. This means the GitHub tokens that were included in the breach could read any repo you own, including private ones.
>
> Recommend: Click "Configure" → under "Repository access" → select "Only select repositories" → add only the 5 Vercel project repos:
> - calibratediq (or whatever the GitHub repo name is)
> - StressTestor/byli (or fork)
> - ratiochat
> - capy-ide
> - social-media-post-previewer
>
> This change takes effect immediately and does not require a redeploy.

- [ ] **Step 3: Log finding**

```bash
echo "github_app_scope: [all_repos|restricted] | $(date -u +%Y-%m-%dT%H:%M:%SZ)" \
  >> /tmp/breach-rotation-log.txt
```

---

## Task 7: Write Summary Report

- [ ] **Step 1: Generate the summary**

Create the file at `/Users/joesephgrey/docs/superpowers/specs/2026-04-19-vercel-breach-summary.md`:

```bash
cat > /Users/joesephgrey/docs/superpowers/specs/2026-04-19-vercel-breach-summary.md << 'SUMMARY_EOF'
# Vercel Breach Rotation — Summary Report
**Generated:** REPLACE_WITH_TIMESTAMP
**Breach disclosure:** 2026-04-19 (ShinyHunters/BreachForums, $2M ask)
**Scope:** All non-Sensitive env vars across 5 Vercel projects under team joes-projects-65e9bd31

| Project | Secrets Rotated | Secrets Deleted | All Sensitive After? | Final Deploy URL | Smoke Test |
|---------|----------------|----------------|---------------------|-----------------|-----------|
| calibratediq | REPLACE | — | ✓ | https://calibratediq.org | REPLACE |
| byli/linkdrift.app | REPLACE | REPLACE | ✓ | https://linkdrift.app | REPLACE |
| ratiochat | REPLACE | REPLACE | ✓ | https://ratiochat.app | REPLACE |
| capy-ide | REPLACE | REPLACE | ✓ | https://capyide.dev | N/A (pre-existing broken build) |
| social-media-post-previewer | REPLACE | REPLACE | ✓ | REPLACE | REPLACE |

## Additional Actions
- **GitHub App scope:** REPLACE (all_repos → restricted / already restricted / prompted user)
- **Git identity — calibratediq:** per-repo config set to StressTestor (future commits only)
- **Git identity — byli:** per-repo config set to StressTestor (future commits only)
- **Supabase service role key (bili):** REPLACE (rotated / not rotated — user declined)

## Notes
- Past commits on public repos retain real name + Mac hostname. History not rewritten (out of scope for this rotation).
- capy-ide smoke test failure is a pre-existing broken build (Nov 2025), not caused by this rotation.
- NEXT_PUBLIC_SUPABASE_ANON_KEY intentionally not rotated — it is a public key by design, RLS enforces access control.
- Log: /tmp/breach-rotation-log.txt (local only, not committed)
SUMMARY_EOF
```

- [ ] **Step 2: Fill in the REPLACE placeholders from the ops log**

```bash
cat /tmp/breach-rotation-log.txt
```

Edit `/Users/joesephgrey/docs/superpowers/specs/2026-04-19-vercel-breach-summary.md` to replace all `REPLACE` placeholders with actual values from the log.

---

## Rollback Reference

If a smoke test fails post-rotation (except capy-ide):

```bash
# Roll back to the last known-good deployment
vercel rollback --scope joes-projects-65e9bd31 --project <PROJECT_NAME>

# Check deployment build/runtime logs
vercel logs --scope joes-projects-65e9bd31 --project <PROJECT_NAME> 2>&1 | tail -50

# Re-inspect env vars for mistakes
vercel env ls production --scope joes-projects-65e9bd31 --project <PROJECT_NAME>
```

**Do NOT roll back capy-ide** — rolling back restores old exposed secrets. The broken build is a separate issue.

---

<!-- /autoplan restore point: to be written by autoplan -->

## GSTACK REVIEW REPORT

| Review | Trigger | Why | Runs | Status | Findings |
|--------|---------|-----|------|--------|----------|
| CEO Review | `/plan-ceo-review` | Scope & strategy | 0 | — | — |
| Codex Review | `/codex review` | Independent 2nd opinion | 0 | — | — |
| Eng Review | `/plan-eng-review` | Architecture & tests (required) | 0 | — | — |
| Design Review | `/plan-design-review` | UI/UX gaps | 0 | — | — |
| DX Review | `/plan-devex-review` | Developer experience gaps | 0 | — | — |

**VERDICT:** Plan written — run `/autoplan` for full review pipeline, or approve manually above.
