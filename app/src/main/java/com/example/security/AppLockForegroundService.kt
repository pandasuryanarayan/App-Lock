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
import com.example.MainActivity
import com.example.data.db.AppLockerDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

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

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                Log.d(TAG, "Screen off detected, relocking all apps")
                AppLockStateManager.relockAll()
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

        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)

        AppLockStateManager.setProtectionActive(true)
        observeDatabaseLockedApps()
        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "AppLockForegroundService onStartCommand")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "AppLockForegroundService onDestroy")
        AppLockStateManager.setProtectionActive(false)
        try {
            unregisterReceiver(screenReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun observeDatabaseLockedApps() {
        serviceScope.launch {
            try {
                val db = AppLockerDatabase.getInstance(applicationContext)
                db.appLockerDao().getAllLockedApps().collectLatest { lockedList ->
                    val lockedPackages = lockedList.filter { it.isLocked }.map { it.packageName }.toSet()
                    AppLockStateManager.updateLockedPackages(lockedPackages)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error observing locked apps from DB", e)
            }
        }
    }

    private fun startMonitoringLoop() {
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (isActive) {
                try {
                    if (PermissionHelper.hasUsageAccess(applicationContext)) {
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
        }
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
            .setContentText("Protected apps are secured in real time")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
