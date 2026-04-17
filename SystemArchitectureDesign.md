# System Architecture Design: DermAssist Mobile Application

This document provides a comprehensive technical overview of the DermAssist system architecture, based on the current implementation of the Android application and its integration with Firebase services, as well as planned integration with external AI analysis providers.

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
    *   **Coil:** Image loading library for rendering remote scan results.
    *   **Retrofit & Moshi:** Networking stack for external AI API communication.

*   **Backend & Storage (Firebase Ecosystem):**
    *   **Firebase Authentication:** Identity management (Email/Password, Google).
    *   **Cloud Firestore:** NoSQL document database.
    *   **Firebase App Check:** Security layer with Play Integrity.
    *   **Google Services Plugin:** Integration orchestrator.

*   **External AI Services:**
    *   **Perfect Corp AI Skin Analysis API:** External third-party API used for processing skin images to detect conditions and calculate health metrics.

*   **Health Data Standards:**
    *   **HAPI FHIR R4:** Library used for mapping internal data to HL7 FHIR standards for medical interoperability.

*   **Justification:** This stack is appropriate for DermAssist because it allows for rapid development with high scalability. Firebase's NoSQL structure is ideal for evolving medical data schemas, while the integration of a specialized external AI API ensures high-quality diagnostic insights without the overhead of maintaining internal ML models.

---

## 2. Hardware Requirements

*   **End-User Hardware:**
    *   **Android Smartphone:** Required to run the application.
    *   **High-Resolution Camera:** Essential for capturing clear skin images for AI processing.
    *   **Internet Connectivity:** Necessary for Firebase synchronization, authentication, and external API calls.
    *   **Minimum Specifications:** Android 7.0 (API 24) or higher, 4GB RAM recommended, ARM64-v8a architecture.

*   **Backend Infrastructure:**
    *   **Google Cloud Platform (GCP):** Managed serverless infrastructure (Firebase).
    *   **Cloud Firestore Clusters:** Geographically distributed NoSQL nodes.
    *   **Perfect Corp Cloud:** Remote processing nodes for AI skin analysis (S3 for presigned image uploads).

---

## 3. System Architecture (UML Deployment Diagram Description)

### a) Nodes & Components

#### Node 1: Mobile Device (Android Smartphone)
*   **Operating System:** Android 7.0+
*   **Role:** Client interface, image capture, local state management, and API gateway.
*   **Components:**
    *   **UI Layer:** Composable screens (Home, History, Report, Profile, Scan Detail).
    *   **ViewModels:** Business logic and state holders (e.g., `HomeViewModel`, `HistoryScreenViewModel`, `ScanDetailViewModel`).
    *   **Repositories:** `AppRepositoryImpl`, `ScanRepositoryImpl`, `SkinAnalysisRepository` (orchestrating AI API workflow).
    *   **Local State:** `LoadingStateDelegate` (Global loading manager).
    *   **FHIR Mapping Layer:** `FhirMapper` (Converts internal models to HL7 FHIR resources).
    *   **Network Stack:** Retrofit Service for skin analysis.

#### Node 2: Firebase Backend (Cloud Infrastructure)
*   **Operating System:** Managed Google Linux environment.
*   **Role:** Centralized data storage, user authentication, and secure access.
*   **Components:**
    *   **Firebase Authentication Service:** Validates tokens and sessions.
    *   **Cloud Firestore Database:** Stores hierarchical NoSQL data.

#### Node 3: Perfect Corp AI Skin Analysis API
*   **Role:** External processing service for diagnostic analysis.
*   **Components:**
    *   **AI Models:** Specialized neural networks for skin condition detection.
    *   **S3 Presigned Storage:** Temporary landing zone for uploaded images.
    *   **API Interface:** RESTful endpoint for task management and result polling.

### b) Communication & Data Flow
*   **Protocols:** 
    *   **HTTPS/TLS:** Standard for all RESTful and SDK-based traffic.
    *   **PUT (S3):** Used for direct binary upload to presigned URLs.
    *   **Firestore Binary Protocol:** Used for real-time synchronization.
*   **Data Flow:**
    1.  **Auth Flow:** User authenticates via Google/Email -> Firebase Auth returns JWT.
    2.  **Scan Flow (Upload):** App requests upload URL -> Receives S3 presigned URL -> App performs binary `PUT` upload.
    3.  **Scan Flow (Analysis):** App sends `file_id` to **Perfect Corp API** -> Polls for `task_status == "success"`.
    4.  **Scan Flow (Storage):** App maps results to `ScanEntity` -> `ScanRepository` pushes to Firestore.
    5.  **Interoperability Flow:** App uses `FhirMapper` to transform `ScanEntity` into a FHIR `Observation`.
    6.  **Real-time Updates:** Firestore Snapshot Listeners push data updates to the `HistoryScreen` automatically.

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

#### Entity: Scan (Document)
*   **Path:** `/users/{userId}/scans/{scanId}`
*   **Fields:**
    *   `id` (String, PK): Auto-generated.
    *   `userId` (String, FK): Reference to parent user.
    *   `createdAt` (Number): Timestamp of scan.
    *   `scanArea` (String): Body part scanned (e.g., "Face").
    *   `overallScore` (Number): Health score (0-100).
    *   `skinAge` (Number): Estimated skin age.
    *   `skinType` (String): Detected skin type (e.g., "Oily").
    *   `imageUrl` (String): Reference to stored image.
    *   `conditions` (Array<Map>): List of `{ label, score, region, maskUrl }`.
    *   `metrics` (Array<Map>): List of `{ name, value, colorHex }`.
    *   `recommendations` (Array<Map>): List of `{ title, description, iconName, ... }`.

### b) Data Standards (HL7 FHIR R4)
DermAssist implements a dedicated **FHIR Mapping Layer** (`FhirMapper.kt`) to ensure compatibility with global health information systems.

*   **Patient Resource:** Maps internal `User` data to `Patient`.
*   **Observation Resource:** Maps `ScanEntity` to a structured `Observation` using **LOINC 86665-7** codes and **SNOMED CT** interpretations.

---

## 5. Integration Documentation

External systems can integrate with the DermAssist data layer via the following:

*   **API Structure:** Firebase REST API or Admin SDK.
*   **External API Interface:** The Perfect Corp AI Skin Analysis API is consumed via a two-step process: (1) Metadata registration to obtain a presigned URL, (2) Direct binary `PUT` to S3-compatible storage.

---

## 6. Security, Privacy & GDPR Compliance

### a) Authentication & Authorization
*   **Mechanism:** Firebase Authentication with **Credential Manager API** integration.
*   **Authorization:** Firestore Security Rules ensure that data is only accessible if `request.auth.uid == userId`.

### b) Data Encryption
*   **In Transit:** Forced HTTPS/TLS for all communication.
*   **At Rest:** Automatic AES-256 encryption provided by Cloud Firestore.

### c) Privacy & GDPR
*   **Data Collected:** Personal identity (Email, Name) and biometric-linked health data (Skin conditions).
*   **Local Validation:** App performs client-side validation of image dimensions (HD: Long side ≤ 4096px, Short side ≥ 1080px) and format (JPG/PNG) to minimize data exposure and optimize bandwidth.
*   **Right to Erase:** Users can invoke `clearUserData()`, which deletes both their Firestore profile and their Authentication record.

---

## 7. System Diagrams (Text Representation)

### UML Deployment Diagram
```text
+-------------------------------------------------------+
|                Node: Mobile Device (Android)          |
|  +-------------------------------------------------+  |
|  | Components: UI, ViewModels, Hilt, FhirMapper    |  |
|  | Networking: Retrofit, Coil, OkHttp (PUT/GET)    |  |
|  +------------+-----------------------+---------------+  |
|               |                       |               |
|               | HTTPS (gRPC)          | HTTPS (REST)  |
|               |                       | + PUT (S3)    |
+---------------+-----------------------+---------------+
                |                       |
+---------------v---------------+       |  +---------------------------+
|     Node: Firebase Cloud      |       |  |  Node: Perfect Corp API   |
|  +-------------------------+  |       |  |  +---------------------+  |
|  | - Auth Service          |  |       +-->  | - S3 Presigned URL  |  |
|  | - Firestore (NoSQL)     |  |          |  | - AI Skin analysis  |  |
|  +-------------------------+  |          +---------------------------+
+-------------------------------+
```

### Entity Relationship Diagram (ERD)
```text
[ USER ] ---< [ FHIR PATIENT ]
  |
  +---< [ SCAN ] ---< [ FHIR OBSERVATION ]
          |-- overallScore
          |-- skinAge
          |-- skinType
          |
          +---[ CONDITION MAP ] (label, score, region, maskUrl)
          |
          +---[ METRIC MAP ]
          |
          +---[ RECOMMENDATION MAP ]
```
