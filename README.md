# TempRoot Checker — Safe v1

Independent **Check Only** Android app for inspecting non-privileged device information. No root engine, no native exploit, no kernel modules, no `su`, no network permissions.

This is **not** a modified root-capable release of Temporary Root 2.0. It is a clean, intentionally limited checker created after inspecting the user's original source archive.

## Build from Android phone using GitHub
1. Create a private GitHub repository named `TempRoot-Checker`.
2. Upload the **contents** of this ZIP, preserving the `.github/workflows/build.yml` path.
3. Open Actions > Build Safe Checker APK > Run workflow.
4. After a successful run, download artifact `TempRoot-Checker-Safe-debug` and extract the APK.
5. Install the APK only if you trust the repository and build result. Android may request permission to install from your file manager.

The GitHub workflow **compiles** an APK; it never runs code on the phone. Installing and launching the APK are separate actions.

## Known limitations
- Does not determine root exploit compatibility.
- Does not verify bootloader status or SELinux state.
- Does not grant root or support Magisk modules.
- APK build and device test have not been executed here.
