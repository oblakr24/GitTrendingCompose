# Git Trending - Compose

A demo app displaying the trending git repositories, sorted by the number of stars descending, and for displaying the details of repositories.

Made using the latest Android development stack and architectural and UI development patterns, including Jetpack Compose and Material Design 3.

The app will load pages automatically once the user scrolls far enough (close enough to the last item) and supports offline mode and a togglable dark mode setting.

## Video

https://github.com/oblakr24/GitTrendingCompose/assets/32245831/eb864a02-b4ad-41b5-afba-5e4b1f3d4b80

## Features

1. **Listing trending repositories:** Trending repositories are listed in an infinitely scrollable list, with pages loaded dynamically. Scroll to-refresh to refresh the data.

2. **Repository details:** The repository can be opened to reveal the details and the rendered readme file markdown content.

3. **Offline mode:** The repositories are stored in a database for offline use and to make fewer requests when the data is not stale.

4. **Dark mode:** Toggle to override system settings. When toggled, the preference is persisted.

## Technologies

**This app is made using:**

- Jetpack Compose for UI with Material 3
- A mix of MVVM/MVI using Molecule for reactive state construction with Coroutines/Flows
- Room and Datastore for data and preferences persistence and offline support
- Standardized design and typography to match Material 3 and easy customization

**Stack:**
- MVVM architecture (mix of MVVM and MVI)
- Coroutines/Flows on the app module for interacting with RxJava3 observables from the BLE module
- [Jetpack Compose](https://developer.android.com/jetpack/compose) and [Compose Navigation](https://developer.android.com/jetpack/compose/navigation): UI
- [Hilt](https://dagger.dev/hilt/) for dependency injection
- [Molecule](https://github.com/cashapp/molecule) for the usage of Compose Compiler in the VM layer for reactive state construction
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization) for serialization and deserialization of models into and from files
- [Retrofit](https://github.com/square/retrofit) for network requests
- [Room](https://developer.android.com/training/data-storage/room) for data persistence
- [Extended Material icons](https://developer.android.com/jetpack/androidx/releases/compose-material) for vector images
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for persisting user preferences
- [Compose Markdown](https://github.com/jeziellago/compose-markdown) for Markdown rendering
- [Compose Shimmer](https://github.com/valentinilk/compose-shimmer) for loading item shimmer support
- [MockK](https://mockk.io/) for mocking in tests
- [Turbine](https://github.com/cashapp/turbine) for testing Flows

## Screenshots

<b>Trending Repositories</b>

<p align="center">
  <img src="https://github-production-user-asset-6210df.s3.amazonaws.com/32245831/246211925-6af1f399-f7c8-419b-9e87-918ea9eccf14.png" width="270" height="570">
  <img src="https://github-production-user-asset-6210df.s3.amazonaws.com/32245831/246211995-14138bc2-6f62-48c7-a6a0-7e05a4eb126f.png" width="270" height="570">
</p>

<b>Repository Details</b>

<p align="center">
  <img src="https://github-production-user-asset-6210df.s3.amazonaws.com/32245831/246212082-993f0d68-eee3-46c4-be4b-9ecfe08b680a.png" width="270" height="570">
  <img src="https://github-production-user-asset-6210df.s3.amazonaws.com/32245831/246212172-035211c7-7c56-4f5c-84fe-bed0f590d574.png" width="270" height="570">
</p>
