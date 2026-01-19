# Weather App (Android, Jetpack Compose)

This is a simple Android weather application built with **Kotlin** and **Jetpack Compose**.
The app shows current weather information (temperature and rain) for different cities using the **Open-Meteo API**.

---

## Features
- Show current **temperature** and **rain amount**
- Predefined cities:
  - Tampere
  - Ivalo
  - Joensuu
  - Ähtäri
  - Helsinki
- Search weather by **custom city name and coordinates**
- Save a city as **default page**
- Simple navigation between cities
- Modern UI built with **Jetpack Compose**

---

## Technologies Used
- Kotlin
- Jetpack Compose
- Android Navigation Compose
- Coroutines
- OkHttp
- DataStore (Preferences)
- Open-Meteo API

---

## How It Works
- Weather data is fetched from the Open-Meteo API using latitude and longitude
- Network calls are done with **OkHttp** and **coroutines**
- The selected default city is saved using **DataStore**
- UI state is handled with `remember` and `mutableStateOf`

---

## Screens
- Main weather screen showing:
  - City name
  - Temperature (°C)
  - Rain (mm)
- Buttons for quick navigation between cities
- Search card for adding a custom city

---

## Notes
- This project was created as part of an application programming course
- The focus is on:
  - Android basics
  - Compose UI
  - Networking
  - State handling

---
