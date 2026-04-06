# Finance Companion

An intelligent, automated personal finance tracker featuring on-device machine learning, predictive analytics, and seamless UPI integration. Built natively for Android using Jetpack Compose and Clean Architecture principles.

## Screenshots

<div align="center">
    <img src="screenshots/appIcon.png" alt="Finance Companion Icon" width="128"/>
</div>

<br/>

<div align="center">
    <h3>Core Showcase</h3>
    <img src="screenshots/home.png" alt="Home Screen" width="220"/>
    <img src="screenshots/insightsAndChart.png" alt="Insights Screen" width="220"/>
    <img src="screenshots/AddTransaction.png" alt="Add Transaction Screen" width="220"/>
    <br/>
    <img src="screenshots/homedark.png" alt="Home Screen Dark Mode" width="220"/>
    <img src="screenshots/Insightsdark.png" alt="Insights Screen Dark Mode" width="220"/>
    <img src="screenshots/profile.png" alt="Profile Screen" width="220"/>
</div>

<details>
<summary><b>View All App Gallery (30+ Screenshots)</b></summary>
<br/>

**Home & Dashboards**
<p>
<img src="screenshots/homescreen.png" width="200"/>
<img src="screenshots/dashboard.png" width="200"/>
<img src="screenshots/DashboardStreak.png" width="200"/>
<img src="screenshots/MagicTracking.png" width="200"/>
</p>

**Insights & Analytics**
<p>
<img src="screenshots/Insights2Dark.png" width="200"/>
<img src="screenshots/donutChart.png" width="200"/>
<img src="screenshots/topSpendingCategories.png" width="200"/>
<img src="screenshots/insight1.png" width="200"/>
<img src="screenshots/insight2.png" width="200"/>
<img src="screenshots/insight3.png" width="200"/>
<img src="screenshots/insight4.png" width="200"/>
<img src="screenshots/insight5.png" width="200"/>
<img src="screenshots/smartInsight.png" width="200"/>
<img src="screenshots/smartInsight2.png" width="200"/>
</p>

**Transactions & Navigation**
<p>
<img src="screenshots/AddTransactionDark.png" width="200"/>
<img src="screenshots/searchBar.png" width="200"/>
<img src="screenshots/history.png" width="200"/>
<img src="screenshots/recentActivity.png" width="200"/>
<img src="screenshots/recentActivitiesDark.png" width="200"/>
</p>

**User Onboarding & Profile Settings**
<p>
<img src="screenshots/LoginScreen.png" width="200"/>
<img src="screenshots/RegisterScreen.png" width="200"/>
<img src="screenshots/ProfileDark.png" width="200"/>
<img src="screenshots/settings.png" width="200"/>
<img src="screenshots/demodata.png" width="200"/>
</p>

**Empty States & Dynamic Theming**
<p>
<img src="screenshots/empty.png" width="200"/>
<img src="screenshots/colorTheme.jpg" width="200"/>
</p>
</details>

## Overview

Finance Companion transcends standard CRUD budgeting applications. It operates as a proactive financial assistant, bridging the gap between manual data entry and complete automation. By leveraging system-level Android services to intercept UPI payments, implementing a custom rule-based insights engine, and utilizing expressive data visualization, this application provides a flagship-tier user experience.

## Core Technology Stack

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material 3 Expressive)
- **Architecture:** Clean Architecture (Presentation, Domain, Data layers), MVVM, Single Source of Truth
- **Design Patterns:** Strategy Pattern (Insights Engine), Observer Pattern (Lifecycle-aware states)
- **Concurrency:** Kotlin Coroutines & Flow
- **Local Storage:** Room Database, SharedPreferences
- **Cloud Synchronization:** Firebase Authentication, Firestore, and Firebase Storage
- **System APIs:** NotificationListenerService, CameraX, Google ML Kit

## Key Features

### Automated "Magic Tracking" (UPI Integration)
Manual entry is the failure point of most finance apps. Finance Companion utilizes a background `NotificationListenerService` to detect payments made via common payment applications (Google Pay, Paytm, PhonePe).
- **Regex Parsing:** Instantly extracts the INR (₹) amount from the notification payload.
- **Smart Interception:** Triggers a high-priority, actionable local notification asking if the user wishes to log the detected transaction.
- **Contextual Permissions:** Implements a graceful permission flow, utilizing a dismissible "Discovery Card" in the home feed and a permanent toggle in the Settings screen, complete with lifecycle observers to detect when the user returns from Android System Settings.

### Rule-Based Smart Insights Engine
Built using the Strategy Pattern for scalability and pure domain isolation, the app analyzes transaction history in real-time to provide actionable coaching. Current active rules include:
- **The Micro-Leak Rule:** Detects high volumes of low-value transactions.
- **Zero-Spend Master:** Gamified positive reinforcement for completing 3-day streaks with zero expenses.
- **Income Windfall:** Detects large deposits (e.g., salary) and suggests an immediate savings allocation.
- **Dining Spike:** Triggers a warning if food expenditures exceed a significant threshold of the total rolling budget.
- **Weekend Warrior:** Flags user behavior if over half of monthly spending occurs on Saturdays and Sundays.

### AI, Machine Learning & Predictive Analytics
- **Burn Rate Forecasting (Predictive ML):** Utilizes intelligent local algorithms to analyze historical spending patterns and calculate your daily average spend velocity. It intelligently maps this trajectory against your remaining month to accurately project future account balances.
- **On-Device Receipt Scanning (Computer Vision):** Integrates CameraX and advanced Google ML Kit text recognition models to passively scan physical receipts. The AI processes the image data locally, extracts text and totals, and automatically pre-fills the "Add Transaction" dialog to seamlessly eliminate manual data entry.

### Interactive Data Visualization
Custom-built Canvas components provide a premium analytical experience.
- **Spending Velocity Chart:** A scrubbable, interactive line chart.
- **Dynamic Donut Charts:** Evaluates the user's system theme at runtime to generate high-contrast, accessible slice colors that are optimized for both Light and Dark modes.

### Firebase Backend Integration
User security and data persistence are paramount. The application leverages a robust Firebase backend architecture:
- **Authentication:** Secure user onboarding via Firebase Auth, enabling reliable account creation, login, and session management.
- **Real-time Data Sync:** Powered by Firebase Firestore, transactions and user data seamlessly sync to the cloud. This guarantees that financial data remains accessible across multiple devices and is persistently backed up. If the application is reinstalled or the user upgrades their device, their single source of truth is instantly restored.

### Tactile UX & Material 3 Expressive Polish
The application is engineered to feel like a physical, premium tool, utilizing deep rounded corners, glassmorphic surface variants, and rigorous attention to micro-interactions.
- **Comprehensive Haptic Architecture:** Vibration feedback is precisely mapped to user intent (e.g., dial-tick effects for scrubbing charts, heavy confirmation thuds for destructive actions).
- **Motion & Animation:** Features a rolling balance counter and physics-based bouncing via Compose Spring animation specs.
- **Dynamic Font Scaling:** Solves edge-case layout breaks by dynamically shrinking hero text if the user's net worth exceeds standard character limits.

### Localization & Theme Perfection
- **Native INR Formatting:** Deep integration of the Indian Rupee (₹) symbol with localized comma formatting.
- **Dynamic Dark Mode:** Adapts to the user's system theme utilizing MaterialTheme semantic color tokens, ensuring full UI compatibility without hardcoded hex values.

## Development & Testing Tools

To facilitate testing and portfolio demonstration, the app includes built-in state management tools:
- **Cloud Synchronization:** Full Firebase integration for backing up and restoring data states.
- **Demo Data Injector:** A dedicated settings toggle to populate the local Room database with realistic, randomized transactions. This instantly accelerates the testing of AI Insights and Charting logic.

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/KaiParker21/FinanceCompanion
   ```
2. **Open the project** in Android Studio (Giraffe or newer recommended).
3. **Sync Gradle files.**
4. **Build and run** on an Emulator or Physical Device (API 26+).

**Testing Magic Tracking:**
To test the `NotificationListenerService` on an emulator, you must grant the "Magic Tracking" permission via the app's UI, then simulate a notification from a payment app like `com.google.android.apps.nbu.paisa.user` (Google Pay) using ADB commands or an emulator notification mocking tool.

## Releases

APK releases and version history will be documented here. You can download the latest APK from the releases section of this repository.

---
**Designed and engineered by Bhavishya Prajapat.**
