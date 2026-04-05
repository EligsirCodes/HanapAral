# HanapAral 📚

> **ITEW4 — Mobile Programming 2 | Midterm Laboratory Examination**
> A mobile application for student study group management with Firebase integration.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Team Members](#team-members)
- [Branch Structure & File Assignment](#branch-structure--file-assignment)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Firebase Configuration](#firebase-configuration)
- [Integration Points](#integration-points)

---

## Project Overview

**HanapAral** is a mobile application developed as part of the ITEW4 Midterm Laboratory Examination. The app provides a platform for students to manage study groups, authenticate via Firebase, and receive real-time push notifications.

**Repository:** https://github.com/EligsirCodes/HanapAral.git

### Core Features

- User authentication and login system (Email/Password + Biometric)
- Student profile creation and persistent Firestore storage
- Firebase backend integration (Firestore, Authentication, Remote Config)
- Study group creation, discovery, and management
- Push notifications via Firebase Cloud Messaging (FCM)
- Clean and accessible UI/UX design
- Structured codebase with Git branching workflow

---

## 👥 Team Members

| Name | Role | Primary Branch |
|------|------|----------------|
| **John Lester Penafiel** | Student Profile / Home Developer | `feature/home-and-announcements`, `feature/core-ui-and-routing` |
| **Joaquin Aaron Recio** | Authentication Developer & Documentation Lead | `feature/auth-and-security` |
| **Luiz Gabriel Rosales** | Project Lead / DevOps | All branches |
| **Trisha Rabano** | UI/UX Designer | `feature/profile-management`, `feature/group-dynamics` |
| **Marc Alvin Quitorio** | FCM / Cloud Messaging Developer | `feature/profile-management`, `feature/group-dynamics` |

---

## 🌿 Branch Structure & File Assignment

### `feature/auth-and-security`
> **Team:** Recio (Authentication & Documentation Lead), Rosales

| File | Assigned Coder |
|------|---------------|
| `LoginScreen` | Recio |
| `AuthViewModel` | Recio |
| `AuthRepository` | Recio |
| `BiometricPromptManager` | Rosales |

---

### `feature/profile-management`
> **Team:** Rabano, Quitorio, Rosales

| File | Assigned Coder |
|------|---------------|
| `ProfileScreen` | Rabano |
| `ProfileViewModel` | Rabano |
| `ProfileRepository` | Quitorio |
| `UserProfile` | Quitorio |
| `AppConfigRepository` | Rosales |

---

### `feature/group-dynamics`
> **Team:** Rosales, Quitorio, Rabano

| File | Assigned Coder |
|------|---------------|
| `StudyGroup` | Rosales |
| `GroupRepository` | Rosales |
| `GroupDetailScreen` | Quitorio |
| `GroupDetailViewModel` | Rosales |
| `GroupListScreen` | Rabano |
| `GroupViewModel` | Rosales |

---

### `feature/home-and-announcements`
> **Team:** Penafiel, Rosales

| File | Assigned Coder |
|------|---------------|
| `HomeScreen` | Penafiel |
| `HomeViewModel` | Rosales |
| `AnnouncementScreen` | Penafiel |

---

### `feature/core-ui-and-routing`
> **Team:** Penafiel, Rosales

| File | Assigned Coder |
|------|---------------|
| `NavGraph` | Penafiel |
| `NavRoutes` | Rosales |
| `MainActivity` | Rosales |

---

## 🛠 Tech Stack

- **Language:** Kotlin / Java (Android)
- **Backend:** Firebase (Firestore, Auth, Storage, Remote Config, FCM)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Version Control:** Git + GitHub

---

## 🗂 Project Structure

```
HanapAral/
├── app/
│   └── src/
│       └── main/
│           └── java/com/hanaparal/
│               ├── features/
│               │   ├── auth/
│               │   │   ├── LoginScreen
│               │   │   ├── AuthViewModel
│               │   │   ├── AuthRepository
│               │   │   └── BiometricPromptManager
│               │   ├── profile/
│               │   │   ├── ProfileScreen
│               │   │   ├── ProfileViewModel
│               │   │   ├── ProfileRepository
│               │   │   ├── UserProfile
│               │   │   └── AppConfigRepository
│               │   ├── groups/
│               │   │   ├── StudyGroup
│               │   │   ├── GroupRepository
│               │   │   ├── GroupDetailScreen
│               │   │   ├── GroupDetailViewModel
│               │   │   ├── GroupListScreen
│               │   │   └── GroupViewModel
│               │   └── home/
│               │       ├── HomeScreen
│               │       ├── HomeViewModel
│               │       └── AnnouncementScreen
│               └── navigation/
│                   ├── NavGraph
│                   ├── NavRoutes
│                   └── MainActivity
├── google-services.json        ← DO NOT COMMIT
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest stable)
- Android SDK (API 26+)
- A Firebase project configured for this app

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/EligsirCodes/HanapAral.git
   cd HanapAral
   ```

2. **Add Firebase config** (obtain from Rosales or Recio via secure channel)
    - Place `google-services.json` in the `app/` directory
    - This file is gitignored and must not be committed

3. **Open in Android Studio**
    - File → Open → select the project root
    - Let Gradle sync complete

4. **Run the app**
    - Select an emulator or physical device
    - Click **Run** or press `Shift+F10`

---

## 🔀 Development Workflow

### Branching Strategy

```
main          ← stable, production-ready
  └── dev     ← integration branch
        ├── feature/auth-and-security
        ├── feature/profile-management
        ├── feature/group-dynamics
        ├── feature/home-and-announcements
        └── feature/core-ui-and-routing
```

### Git Commit Convention

```
feat: add login screen UI
fix: resolve FCM token null exception
refactor: extract profile validation logic
docs: update README with setup steps
```

### Pull Request Process

1. Push your feature branch to origin
2. Open a PR targeting `dev`
3. Tag **Rosales** as reviewer
4. Address review comments
5. Merge only after approval

---

## 🔥 Firebase Configuration

### Services Used

| Service | Purpose | Owner |
|---------|---------|-------|
| Firebase Authentication | Email/Password login | Recio |
| Cloud Firestore | User profiles, groups, announcements | Recio |
| Firebase Storage | Profile photo uploads | Recio |
| Firebase Remote Config | Feature flags, dynamic content | Rosales |
| Firebase Cloud Messaging | Push notifications | Quitorio |

### Firestore Security Rules (Example)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Only authenticated users can read/write their own profile
    match /students/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    // Deny all unauthenticated access by default
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

---

## 🔗 Integration Points

| Member A | Member B | Integration |
|----------|----------|-------------|
| John Lester | Joaquin Aaron | Firestore rules must allow profile document writes per user UID |
| John Lester | Trisha Rabano | Profile creation screen UI designed by Trisha, implemented by John Lester |
| Marc Alvin | Joaquin Aaron | FCM requires Cloud Messaging enabled in Firebase Console |
| Marc Alvin | John Lester | FCM token stored inside student profile document (`fcmToken` field) |
| Luiz Gabriel | All Members | File structure and Git conventions defined by Luiz — all PRs reviewed by Luiz |
| Trisha Rabano | All Members | Design tokens (colors, fonts, spacing) consumed by all members implementing UI |

---

## 📝 Documentation

All project documentation is maintained by **Joaquin Aaron Recio** (Documentation Lead).

| Document | Description | Author |
|----------|-------------|--------|
| `README.md` | Project overview, setup guide, branch structure, contribution guidelines | Recio |
| `Final Report` | Team roles, file-level assignments, integration plan, role matrix | Recio |
| Branch Assignment Table | Finalized file-to-coder mapping per branch | Recio |

If any file assignment, role, or scope changes during development, notify Recio immediately so the documentation can be kept accurate and up to date before the final submission.

---

## 📄 License

This project was developed for academic purposes as part of ITEW4 — Mobile Programming 2.

---

*HanapAral — Penafiel • Recio • Rosales • Rabano • Quitorio*