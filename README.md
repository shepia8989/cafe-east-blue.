# CAFE EAST BLUE — Android POS prototype

Starter Android point-of-sale app with a dark pirate-inspired East Blue look and Bluetooth ESC/POS receipt output for compatible 58 mm thermal printers.

## Features
- Ten sample cafe products and prices (editable in `MainActivity.java`)
- Add items to cart and calculate total
- Choose a previously paired Bluetooth printer
- Send receipt text over Bluetooth SPP
- GitHub Actions workflow to build a debug APK

## Build APK on GitHub
1. Upload the contents of this project to the repository's `main` branch.
2. Open **Actions** and choose **Build CAFE EAST BLUE APK**.
3. Press **Run workflow**.
4. After the run completes successfully, open the run and download artifact `cafe-east-blue-debug-apk`.
5. Extract the ZIP and install `app-debug.apk` on Android.

## Printer setup
Pair the printer in Android Settings > Bluetooth before using the app. The app lists paired devices and uses the standard Bluetooth Serial Port Profile UUID. Printer firmware varies; test with the actual 58 mm printer.

## Prototype limitations
Prices are examples. This version does not save sales history, manage inventory, calculate cash change, or create a signed release APK. Test the app on the target phone and printer before real sales use.
