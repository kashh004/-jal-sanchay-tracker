# 🔥 Firebase Integration Guide

## Setup Instructions

### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project"
3. Enter project name: **JalSanchay**
4. Enable Google Analytics (optional but recommended)
5. Create project

### Step 2: Register Android App

1. In Firebase console, click "Android" icon
2. Package name: `com.jalsanchay`
3. App nickname: `JalSanchay (Debug)`
4. SHA-1 Certificate fingerprint:
   ```bash
   # Get your debug SHA-1:
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
5. Register app

### Step 3: Download google-services.json

1. Click "Download google-services.json"
2. Place file in: `app/google-services.json`
3. **Important:** Never commit to git (add to .gitignore)

### Step 4: Enable Firebase Services

#### Analytics
- ✅ Automatic event tracking
- ✅ Custom events
- ✅ User properties
- ✅ Crash reporting

#### Crashlytics
1. Go to Firebase Console
2. Navigation > Crashlytics
3. Enable Crashlytics
4. First crash reports appear within minutes

#### Cloud Messaging (FCM)
1. Go to Firebase Console
2. Navigation > Cloud Messaging
3. Enable FCM
4. Copy Server API Key (for backend)

#### Firestore (Optional)
1. Go to Firebase Console
2. Navigation > Firestore Database
3. Create database (Start in test mode)
4. Choose region (closest to users)

#### Realtime Database (Optional)
1. Go to Firebase Console
2. Navigation > Realtime Database
3. Create database (Start in test mode)

### Step 5: Update Android Manifest

Add these permissions:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Add Firebase service for cloud messaging:
```xml
<service
    android:name="com.google.firebase.messaging.FirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

### Step 6: Initialize Firebase in Application Class

Create or update `Application.kt`:

```kotlin
import android.app.Application
import com.jalsanchay.utils.FirebaseManager
import com.jalsanchay.utils.AppLogger

class JalSanchayApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseManager.initialize(this)
        
        // Initialize app logger
        AppLogger.initialize(this)
        
        AppLogger.d("Application initialized")
    }
}
```

Update `AndroidManifest.xml`:
```xml
<application
    android:name=".JalSanchayApplication"
    ...>
```

### Step 7: Usage Examples

#### Log Custom Events
```kotlin
// Log rainfall entry
FirebaseManager.logRainfallEntry(rainfallMm = 25f, litersHarvested = 250.0)

// Log achievement
FirebaseManager.logAchievementUnlocked("Water Warrior")

// Log data export
FirebaseManager.logDataExport("CSV", entriesCount = 45)

// Log feature usage
FirebaseManager.logFeatureUsage("tips_search")
```

#### Record Exceptions
```kotlin
try {
    // Your code
} catch (e: Exception) {
    FirebaseManager.recordException(e)
}
```

#### Get FCM Token
```kotlin
FirebaseManager.getFCMToken { token ->
    // Save token to backend for targeted notifications
    PreferencesHelper.setFCMToken(token)
}
```

#### Subscribe to Topics
```kotlin
// Subscribe to rainfall alerts
FirebaseManager.subscribeTopic("rainfall_alerts")

// Subscribe to achievements
FirebaseManager.subscribeTopic("achievements")

// Custom topics
FirebaseManager.subscribeTopic("region_${userRegion}")
```

### Step 8: Create Cloud Messaging Handler

Create `FirebaseMessagingService.kt`:

```kotlin
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jalsanchay.utils.AppLogger
import com.jalsanchay.utils.NotificationHelper

class JalSanchayMessagingService : FirebaseMessagingService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        AppLogger.d("Message received: ${remoteMessage.messageId}")
        
        remoteMessage.notification?.let {
            NotificationHelper.showCustomNotification(
                title = it.title ?: "JalSanchay",
                message = it.body ?: "",
                channelId = "rainfall_alerts"
            )
        }
    }
    
    override fun onNewToken(token: String) {
        AppLogger.d("FCM Token refreshed: $token")
        // Send token to backend/save locally
        PreferencesHelper.setFCMToken(token)
    }
}
```

Register in `AndroidManifest.xml`:
```xml
<service
    android:name=".services.JalSanchayMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

## Features Enabled

### 1. Analytics Dashboard
- Event tracking
- User engagement
- Retention analysis
- Crash patterns

### 2. Crash Reporting
- Automatic crash detection
- Stack trace analysis
- ANR (Not Responding) tracking
- User impact assessment

### 3. Cloud Messaging
- Push notifications
- Topic-based messaging
- Scheduled messages
- Rich notifications

### 4. Remote Configuration (Optional)
- Feature flags
- A/B testing
- Configuration updates

### 5. Performance Monitoring (Optional)
- App startup time
- Screen rendering
- Network performance
- Custom traces

## Build.gradle.kts Configuration

Already configured in `build.gradle.kts`:
```kotlin
// Firebase BOM ensures compatible versions
implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

// Individual Firebase products (auto-versioned via BOM)
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-crashlytics-ktx")
implementation("com.google.firebase:firebase-messaging-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-database-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
```

Add Google Play Services gradle plugin in `build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
```

## Testing Firebase Locally

### Test Crashlytics
```kotlin
// In any Activity/Fragment
FirebaseManager.setCrashCustomKey("test_key", "test_value")

// Manually trigger crash to test
throw RuntimeException("Test crash")
```

### Test Cloud Messaging
1. Go to Firebase Console > Cloud Messaging
2. Click "Send your first message"
3. Enter notification title and body
4. Select "App in development" or specific test device
5. Send message
6. Check device notifications

## Troubleshooting

### Crash Report Not Appearing
- Wait 5-15 minutes for Firebase to process
- Ensure Firebase Crashlytics is enabled
- Check crash filters in Firebase console

### FCM Token Not Received
- Ensure Google Play Services installed on device
- Check notifications are enabled in app settings
- Verify topic subscriptions in code

### Analytics Events Not Tracking
- Check event names are valid (lowercase, underscore, max 40 chars)
- Ensure Firebase Analytics is enabled
- Events appear in console after 24 hours

## Security & Privacy

### Data Privacy
- Firebase data encrypted in transit
- GDPR compliant with proper configuration
- Users can delete data via Google Account

### API Key Security
- `google-services.json` contains restricted API keys
- Keys are domain-restricted in Firebase console
- Never commit `google-services.json` to git

### User Privacy
- Users can opt-out of analytics
- Implement privacy toggle in settings
- Respect user preferences

## Next Steps

1. ✅ Create Firebase project
2. ✅ Download google-services.json
3. ✅ Enable desired services
4. ✅ Initialize FirebaseManager
5. ✅ Test on emulator/device
6. ✅ Monitor Firebase console

## Useful Links

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Best Practices](https://firebase.google.com/docs/best-practices)
- [Google Play Services](https://developers.google.com/android/guides/setup)

---

Your Firebase integration is now ready! Monitor your app's performance, crashes, and user engagement from the Firebase Console. 🚀
