# System Architecture Design: DermAssist Mobile Application

This document provides a comprehensive technical overview of the DermAssist system architecture, based on the current implementation of the Android application and its integration with Firebase services.

---

## 1. Technology Stack Analysis

The DermAssist application is built using a modern Android development stack, emphasizing reactive programming, dependency injection, and cloud-native services.

*   **Frontend (Mobile Application):**
    *   **Kotlin:** The primary programming language.
    *   **Jetpack Compose:** Declarative UI toolkit.
    *   **Material Design 3:** UI component library.
    *   **Hilt (Dagger):** Dependency Injection (DI) framework.
    *   **Kotlin Coroutines & Flow:** Asynchronous programming and reactive data streams.
    *   **Jetpack Navigation:** Type-safe navigation management.
    *   **Credential Manager API:** Modern authentication (Google Sign-In).

*   **Backend & Storage (Firebase Ecosystem):**
    *   **Firebase Authentication:** Identity management (Email/Password, Google).
    *   **Cloud Firestore:** NoSQL document database.
    *   **Firebase App Check:** Security layer with Play Integrity.
    *   **Google Services Plugin:** Integration orchestrator.

*   **Justification:** This stack is appropriate for DermAssist because it allows for rapid development with high scalability. Firebase's NoSQL structure is ideal for evolving medical data schemas, while Jetpack Compose allows for highly responsive UI layouts necessary for medical reporting.

---

## 2. Hardware Requirements

*   **End-User Hardware:**
    *   **Android Smartphone:** Required to run the application.
    *   **High-Resolution Camera:** Essential for capturing clear skin images.
    *   **Internet Connectivity:** Necessary for Firebase synchronization and authentication.
    *   **Minimum Specifications:** Android 7.0 (API 24) or higher, 4GB RAM recommended, ARM64-v8a architecture.

*   **Backend Infrastructure:**
    *   **Google Cloud Platform (GCP):** Managed serverless infrastructure (Firebase).
    *   **Cloud Firestore Clusters:** Geographically distributed NoSQL nodes.

---

## 3. System Architecture (UML Deployment Diagram Description)

### a) Nodes & Components

#### Node 1: Mobile Device (Android Smartphone)
*   **Operating System:** Android 7.0+
*   **Role:** Client interface, image capture, local state management.
*   **Components:**
    *   **UI Layer:** Composable screens (Home, History, Report, Profile).
    *   **ViewModels:** Business logic and state holders (e.g., `HomeViewModel`, `HistoryScreenViewModel`).
    *   **Repositories:** `AppRepositoryImpl`, `ScanRepositoryImpl`.
    *   **Local State:** `LoadingStateDelegate` (Global loading manager).
    *   **SDKs:** Firebase Auth SDK, Firestore SDK, Credential Manager.

#### Node 2: Firebase Backend (Cloud Infrastructure)
*   **Operating System:** Managed Google Linux environment.
*   **Role:** Centralized data storage, user authentication, and secure access.
*   **Components:**
    *   **Firebase Authentication Service:** Validates tokens and sessions.
    *   **Cloud Firestore Database:** Stores hierarchical NoSQL data.

### b) Communication & Data Flow
*   **Protocols:** 
    *   **HTTPS/TLS:** Standard for all RESTful and SDK-based traffic.
    *   **Firestore Binary Protocol:** Used for real-time synchronization.
*   **Data Flow:**
    1.  **Auth Flow:** User authenticates via Google/Email -> Firebase Auth returns JWT -> App stores token locally.
    2.  **Scan Flow:** Image captured -> `ScanRepository` pushes `ScanEntity` to Firestore -> Triggering real-time listeners.
    3.  **Real-time Updates:** Firestore Snapshot Listeners push data updates to the `HistoryScreen` automatically.

---

## 4. Data Structure Design

### a) Database Schema (ERD Description)

The system uses a Document-Collection model in Cloud Firestore.

#### Entity: User (Document)
*   **Path:** `/users/{userId}`
*   **Fields:**
    *   `id` (String, PK): Firebase UID.
    *   `name` (String): User's full name.
    *   `email` (String): User's email address.
    *   `age` (Number): User's age.
    *   `memberSince` (Timestamp): Registration date.
*   **Relationships:** One-to-Many with **Scan**.

#### Entity: Scan (Document)
*   **Path:** `/users/{userId}/scans/{scanId}`
*   **Fields:**
    *   `id` (String, PK): Auto-generated.
    *   `userId` (String, FK): Reference to parent user.
    *   `createdAt` (Number): Timestamp of scan.
    *   `scanArea` (String): Body part scanned (e.g., "Face").
    *   `overallScore` (Number): Health score (0-100).
    *   `conditions` (Array<String>): List of detected condition labels.
    *   `imageUrl` (String): Reference to stored image.
    *   `metrics` (Array<Map>): List of `{ name, value, colorHex }`.
    *   `recommendations` (Array<Map>): List of `{ title, description, iconName, ... }`.

### b) Data Standards
The implementation uses a simplified internal schema optimized for NoSQL performance. While not fully FHIR-compliant out-of-the-box, the field structures are designed to be mapped to FHIR `Observation` and `DiagnosticReport` resources for future interoperability.

---

## 5. Integration Documentation

External systems can integrate with the DermAssist data layer via the following:

*   **API Structure:** Firebase REST API or Admin SDK.
*   **Authentication:** OAuth 2.0 / Firebase JWT.
*   **Data Format:** JSON.
*   **Example Request (Add Scan):**
    ```json
    POST /v1/projects/dermassist/databases/(default)/documents/users/{uid}/scans
    {
      "fields": {
        "scanArea": { "stringValue": "Left Arm" },
        "overallScore": { "integerValue": 85 }
      }
    }
    ```

---

## 6. Security, Privacy & GDPR Compliance

### a) Authentication & Authorization
*   **Mechanism:** Firebase Authentication.
*   **Authorization:** Firestore Security Rules ensure that data is only accessible if `request.auth.uid == userId`.

### b) Data Encryption
*   **In Transit:** Forced HTTPS/TLS for all communication.
*   **At Rest:** Automatic AES-256 encryption provided by Cloud Firestore.

### c) Privacy & GDPR
*   **Data Collected:** Personal identity (Email, Name) and biometric-linked health data (Skin conditions).
*   **Consent:** Obtained via Onboarding/Splash screen flow.
*   **Right to Erase:** Users can invoke `clearUserData()`, which deletes both their Firestore profile and their Authentication record.

### d) Incident Response Plan
*   **Monitoring:** Firebase Cloud Logging and Audit Logs.
*   **Strategy:** Automated alerts for unusual access patterns; immediate credential revocation via Google IAM.

---

## 7. System Diagrams (Text Representation)

### UML Deployment Diagram
```text
+-------------------------------------------------------+
|                Node: Mobile Device (Android)          |
|  +-------------------------------------------------+  |
|  | Components: UI (Compose), ViewModels, Hilt      |  |
|  | SDKs: Firebase Auth, Firestore, Credential Mgr  |  |
|  +-----------------------+-------------------------+  |
|                          |                            |
|                          | Protocol: HTTPS / gRPC     |
|                          |                            |
+--------------------------+----------------------------+
                           |
+--------------------------v----------------------------+
|                Node: Firebase Cloud (GCP)             |
|  +-------------------------------------------------+  |
|  | Services:                                       |  |
|  | - Firebase Authentication (Identity Provider)    |  |
|  | - Cloud Firestore (NoSQL Storage)                |  |
|  +-------------------------------------------------+  |
+-------------------------------------------------------+
```

### Entity Relationship Diagram (ERD)
```text
[ USER ]
  |-- id (PK)
  |-- name
  |-- email
  |-- age
  |-- memberSince
  |
  +---< [ SCAN ]
          |-- id (PK)
          |-- createdAt
          |-- scanArea
          |-- overallScore
          |-- conditions (Array)
          |
          +---[ METRICS ] (Embedded Map)
          |
          +---[ RECOMMENDATIONS ] (Embedded Map)
```
