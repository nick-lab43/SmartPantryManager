# Smart Pantry Manager

Smart Pantry Manager is a Java-based Android application for recording pantry ingredients and discovering recipes that can be prepared from available stock.

## Main features

- Add, edit, and delete pantry ingredients.
- Store ingredient names, quantities, units, and optional expiry dates in a local SQLite database.
- Display pantry ingredients in a RecyclerView.
- Seed the database with 20 example recipes and their required ingredients.
- Suggest only recipes for which all required ingredients are available in sufficient quantities.
- Normalise common ingredient-name variants and support common unit conversions.
- View recipe ingredients and preparation steps.
- Navigate between pantry, recipe suggestions, recipe details, and settings screens.
- Validate ingredient form input and show empty-state feedback.

## Technology

- Java
- Android SDK (compile SDK 35; minimum SDK 24)
- AndroidX AppCompat
- RecyclerView
- Material Components
- SQLiteOpenHelper

## Open and run

1. Extract the submission ZIP.
2. In Android Studio, choose **Open** and select the `SmartPantryManager` folder (the folder containing `settings.gradle.kts`).
3. Allow Gradle sync to finish. If prompted, use a JDK compatible with Android Gradle Plugin 8.7.3 (JDK 17 is recommended).
4. Select an Android emulator or connected device running Android 7.0 (API 24) or later.
5. Build and run the `app` configuration.

The first sync may need an internet connection to download Gradle and Maven dependencies that are not already cached locally.

## Data and privacy

Pantry and recipe data are stored locally using SQLite. The app does not require an account or a network connection for its core pantry and recipe functions.

## Project structure

- `app/src/main/java/za/co/richfield/smartpantrymanager/` — Java application classes.
- `app/src/main/res/layout/` — screen and list-item layouts.
- `app/src/main/res/values/` — app theme resources.
- `app/src/main/AndroidManifest.xml` — app activities and launcher configuration.
