# Jai Malhar Pratisthan — Android starter project

**Design:** English UI, blue and white theme, one common UPI QR for all four collectors, Marathi-only thank-you SMS.

## Included in this starter
- Native Android app using Kotlin and Jetpack Compose
- Login screen and collector/admin-labelled UI shell
- Dashboard with received/pending totals
- New donation form (name, mobile, amount, Cash/UPI QR, status)
- Searchable donation records and pending-to-received action
- Receipt ID and local date/time on records
- Common QR placeholder and voice-input placeholder

## Important status
This is a **UI and workflow starter**, not a production-ready payment system. Records currently remain in app memory and disappear when the process is cleared. Login currently accepts any non-empty username/password and is **not secure authentication**. No Google Drive, Google Sheets, SMS gateway, speech recognition, real QR image upload, or automatic UPI payment verification has been connected. Do not use this build for real donations until those integrations and security controls are implemented and tested.

## Open and build
1. Install Android Studio (current stable) and a compatible JDK 17.
2. Extract this project ZIP.
3. In Android Studio choose **Open**, then select the extracted `JaiMalharPratisthan` folder.
4. Allow Gradle sync. If Android SDK Platform 35 is missing, install it from SDK Manager.
5. Test on an emulator or Android device.
6. To build a debug APK, use **Build > Build Bundle(s) / APK(s) > Build APK(s)**. Android Studio will show the generated APK location. For a release APK, configure signing before distribution.

## Production integration plan
1. Add a secure backend (e.g. Firebase Authentication + Firestore/Cloud Functions, or a secured server) and create exactly five authorized accounts: 1 super admin and 4 collectors. Enforce roles on the server, not only in the UI.
2. Store donation records in a database; export/sync authorized records and files to Google Drive/Sheets through a backend service account/OAuth flow. Never embed privileged Google credentials in the APK.
3. Store the common QR image in admin-controlled storage. A static QR alone cannot verify payment automatically.
4. Integrate speech recognition using Android speech APIs, ask the user to confirm recognized name/number, and request microphone permission at runtime.
5. Integrate an Indian SMS provider (for example MSG91) from the backend. Complete provider onboarding, DLT/header/template approvals as applicable. Keep API keys server-side and log provider delivery status.
6. Trigger the Marathi thank-you SMS only after verified receipt; prevent duplicate SMS using an idempotent receipt/event ID.
7. Add audit logs, backups, privacy notice, access controls, validation and testing before production.

## Suggested Marathi SMS template
🙏 जय मल्हार प्रतिष्ठान 🙏

नमस्कार,
आपण जय मल्हार प्रतिष्ठानला ₹{रक्कम} देणगी दिल्याबद्दल आपले मनःपूर्वक आभार!
आपल्या मोलाच्या सहकार्याबद्दल धन्यवाद.
— जय मल्हार प्रतिष्ठान

Use the exact wording and variables accepted by the SMS provider's approved template.
