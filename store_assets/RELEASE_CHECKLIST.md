# FitTrack - Release Checklist & Indus Appstore Guide

## 🔑 Keytool Keystore Generation Command

Run this command in your terminal to create your release keystore file (`release-keystore.jks`):

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias fittrack-key
```

### Configure `keystore.properties`:
Create a file named `keystore.properties` in your root project folder (automatically ignored by Git) with:

```properties
storeFile=../release-keystore.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=fittrack-key
keyPassword=YOUR_KEY_PASSWORD
```

---

## 🧪 Manual Pre-Release Test Suite (Run on Real Device)

- [ ] **Fresh Install**: Launch app on a clean device. Verify Onboarding opens and saves profile into Room DB.
- [ ] **Exercise Search & Filter**: Test typing in search bar and clicking muscle group chips.
- [ ] **Active Workout Session**: Start a routine, complete sets, verify rest timer vibration, and tap Finish.
- [ ] **Workout History**: Verify completed workout appears in history with total volume and notes.
- [ ] **Progress & Charts**: Log weight and check that the Canvas Line Chart renders smoothly.
- [ ] **Nutrition & Water**: Tap +250ml water button and verify progress bar fills.
- [ ] **Calculators**: Test BMI, TDEE, and 1RM calculations.
- [ ] **Screen Rotation**: Rotate screen on Active Workout and verify state is preserved without crashes.
- [ ] **Process Death**: Send app to background, kill process, and reopen to ensure DataStore/Room restores state.
- [ ] **JSON Backup & Export**: Export JSON backup to storage and restore it back.

---

## 🚀 Uploading to Indus Appstore Console

1. Log into your [Indus Appstore Developer Console](https://developer.indusappstore.com/).
2. Click **Create New Application**.
3. Upload your signed release bundle (`app-release.aab` or `app-release.apk`).
4. Copy the app title, short description, and long description (both English and Hindi) from `store_assets/STORE_LISTING.md`.
5. Upload the 512x512 app icon, 1024x500 banner, and 6 screenshots.
6. Provide link to your hosted `PRIVACY_POLICY.md`.
7. Submit for review!
