package se.torsteneriksson.recepihandler

import android.app.*
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.renderscript.ScriptGroup
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.TypedArrayUtils.getText
import se.torsteneriksson.recepihandler.service.RecepiHandlerService
import se.torsteneriksson.recepihandler.service.RecepiHandlerService.Companion.startService
import android.icu.util.Output as Output1


fun isMyServiceRunning(context: Context, serviceClass:String): Boolean {
    val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass == service.service.className) {
            return true
        }
    }
    return false
}

fun getHumanFriendlyTime(secs: Long): String {
    val hours: String = String.format("%02d", secs / 3600)
    var minutes: String = String.format("%02d", (secs % 3600) / 60)
    var seconds: String = String.format("%02d", secs % 60)

    return hours + ":" + minutes + ":" + seconds
}


fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(context.getString(R.string.channeld_id), name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun notify(context: Context, title: String, message: String) {
    val intent = Intent(context, RecepiHandlerMainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    val channelId: String = context.getString(R.string.channeld_id)
    val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    val mBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
        .setContentTitle(title)
        .setContentText(message)
        .setSound(soundUri) //This sets the sound to play
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
    val notification = mBuilder.build()
    notification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_AUTO_CANCEL
    notificationManager.notify(0, notification)
}

fun getRecepi(): Recepi {
    var  recepiSteps = ArrayList<RecepiStep>()
    recepiSteps.add(RecepiStepPrepare(
        """
        Blanda alla ingredienser utom valnötterna. 
        När allt är väl blandat, blanda in valnötterna.
        Låt det sedan jäsa i 30 min.
        """.trimIndent()))
    recepiSteps.add(RecepiStepWait("Jäsning i 30 min", 1800))
    recepiSteps.add(RecepiStepPrepare("Dags att vika 1:a gången och sedan 30 min jäsning"))
    recepiSteps.add(RecepiStepWait("Jäsning i 30 min", 1800))
    recepiSteps.add(RecepiStepPrepare("Dags att vika 2:a gången och sedan 30 min jäsning"))
    recepiSteps.add(RecepiStepWait("Jäsning i 30 min", 1800))
    recepiSteps.add(RecepiStepPrepare("Dags att vika, 3:e och sista gången och sedan 1 tim jäsning"))
    recepiSteps.add(RecepiStepWait("Jäsning i 1 timme", 3600))
    recepiSteps.add(RecepiStepPrepare("Baka ut och grädda i 15 min"))
    recepiSteps.add(RecepiStepWait("Gräddning i 15 min", 900))
    recepiSteps.add(RecepiStepPrepare("Vädra!"))
    recepiSteps.add(RecepiStepWait("Gräddning i 5 min", 300))
    recepiSteps.add(RecepiStepPrepare("Vädra"))
    recepiSteps.add(RecepiStepWait("Gräddning i 5 min", 300))
    recepiSteps.add(RecepiStepPrepare("Vädra"))
    recepiSteps.add(RecepiStepWait("Gräddning i 5 min", 300))
    recepiSteps.add(RecepiStepPrepare("Mät tempen och grädda möjligen i ytterligare 5 min"))
    recepiSteps.add(RecepiStepWait("Gräddning i 5 min", 300))
    recepiSteps.add(RecepiStepPrepare("Färdigt!!"))
    val overAllDescription: String =
        """
        Ingredienser:
        - Jäst 3g
        - Vetemjöl special 300g
        - Vatten 300g
        - Salt 1.5tsk
        - Rågmjöl 75g
        - Valnötter 150g
        """.trimIndent()
    var recepi: Recepi = Recepi("id1", "Valnötsbröd", overAllDescription, recepiSteps)
    return recepi
}

fun showAlertDialog(context: Context, title: String, message: String):
        AlertDialog {
    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.setPositiveButton(context.getString(R.string.yes)) {
            dialog, whichButton -> dialog.dismiss()
    }
    alertDialog.setNegativeButton(context.getString(R.string.no)) {
            dialog, whichButton -> dialog.dismiss()
    }
    val alert: AlertDialog = alertDialog.create()
    alert.setCanceledOnTouchOutside(false)
    return alert
}