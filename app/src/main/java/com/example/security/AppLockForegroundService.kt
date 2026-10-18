package com.example.security

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import kotlinx.coroutines.*

/**
 * Compatibility fallback engine for users who do not want to grant Accessibility
 * access. It polls UsageStats - but ONLY while the screen is ON:
 *
 *  - SCREEN_OFF -> clear temporary unlocks and cancel the polling loop (no CPU,
 *    no wakeups while the phone is in a pocket).
 *  - SCREEN_ON  -> resume polling.
 *
 * When the accessibility engine is available this service stops itself and never
 * shows its permanent notification.
 */
class AppLockForegroundService : Service() {

    companion object {
        private const val TAG = "AppLockForegroundService"
        private const val CHANNEL_ID = "app_locker_protection_channel"
        private const val NOTIFICATION_ID = 1001
        private const val POLL_INTERVAL_MS = 300L
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var monitoringJob: Job? = null
    private var usageStatsManager: UsageStatsManager? = null

    private var receiverRegistered = false
    private var pollingActive = false

    @Volatile
    private var isScreenOn = true

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d(TAG, "Screen off -> relocking apps and pausing the polling loop")
                    isScreenOn = false
                    AppLockStateManager.relockAll()
                    stopMonitoringLoop()
                }
                Intent.ACTION_SCREEN_ON -> {
                    Log.d(TAG, "Screen on -> resuming the polling loop")
                    isScreenOn = true
                    startMonitoringLoop()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AppLockForegroundService onCreate")
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

        createNotificationChannel()
        val notification = buildForegroundNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Low-battery architecture: when Accessibility access is granted the
        // event-driven engine does all the work, and when the user paused
        // protection nothing should run at all. In both cases we never keep the
        // polling fallback (or its permanent notification) alive.
        if (PermissionHelper.isAccessibilityServiceEnabled(this) || !ProtectionPreferences.isProtectionEnabled(this)) {
            Log.d(TAG, "Event-driven engine owns interception -> stopping polling fallback")
            stopSelf()
            return
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        ContextCompat.registerReceiver(this, screenReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        receiverRegistered = true

        // ACTION_SCREEN_ON is a sticky broadcast, so this tells us the current state.
        isScreenOn = registerReceiver(null, IntentFilter(Intent.ACTION_SCREEN_ON)) != null

        pollingActive = true
        AppLockStateManager.isMasterEnabled = true
        AppLockStateManager.setPollingEngineActive(true)

        // Cache the locked list + re-lock policy in memory once, then keep them hot.
        ProtectionBootstrapper.attach(this, serviceScope)

        if (isScreenOn) {
            startMonitoringLoop()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "AppLockForegroundService onStartCommand")
        // Only ask the system to restart us when this engine is really the active one.
        return if (pollingActive) START_STICKY else START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "AppLockForegroundService onDestroy")
        AppLockStateManager.setPollingEngineActive(false)
        if (receiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            receiverRegistered = false
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startMonitoringLoop() {
        if (monitoringJob?.isActive == true) return
        monitoringJob = serviceScope.launch {
            while (isActive) {
                try {
                    if (!AppLockStateManager.isMasterEnabled) {
                        // Safety net: the master toggle normally stops this service.
                        break
                    }
                    if (isScreenOn && PermissionHelper.hasUsageAccess(applicationContext)) {
                        val topPackage = getForegroundPackage()
                        if (topPackage != null && topPackage.isNotEmpty()) {
                            AppLockStateManager.checkAndIntercept(applicationContext, topPackage)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during app monitoring cycle", e)
                }
                delay(POLL_INTERVAL_MS)
            }
            if (!AppLockStateManager.isMasterEnabled) {
                stopSelf()
            }
        }
    }

    private fun stopMonitoringLoop() {
        monitoringJob?.cancel()
        monitoringJob = null
    }

    private fun getForegroundPackage(): String? {
        val usm = usageStatsManager ?: return null
        val time = System.currentTimeMillis()
        val events = usm.queryEvents(time - 5000, time)
        var topPackage: String? = null
        val event = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                topPackage = event.packageName
            }
        }
        return topPackage
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Locker Protection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows status of real-time application security lock"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Locker Protection Active")
            .setContentText("Compatibility mode: monitoring while the screen is on")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
