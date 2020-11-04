package se.torsteneriksson.recepihandler

import android.app.*
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import se.torsteneriksson.recepihandler.database.*

const val PICK_RECEPI_REQUEST = 1

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

fun notify(context: Context, title: String, message: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
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


fun getRecepiList(): ArrayList<Recepi> {
    var recepiList: ArrayList<Recepi> = ArrayList()
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())
    recepiList.add(getTorstenBrod())
    recepiList.add(getValnotsbrod())

    return recepiList
}

fun getRecepi(recepi: String): Recepi? {
    when(recepi){
        "Valnötsbröd" -> return getValnotsbrod()
        "Torsten Bröd" -> return getTorstenBrod()
        else -> return null
    }
}

fun getValnotsbrod(): Recepi {
    var  recepiSteps = ArrayList<RecepiStep>()
    recepiSteps.add(
        RecepiStepPrepare(
        """
        Blanda alla ingredienser utom valnötterna. 
        När allt är väl blandat, blanda in valnötterna.
        Låt det sedan jäsa i 30 min.
        """.trimIndent())
    )
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
    var ingredients = ArrayList<Ingredient>()
    ingredients.add(Ingredient("Jäst", "3g"))
    ingredients.add(Ingredient("Vetemjöl special", "300g"))
    ingredients.add(Ingredient("Vatten", "300g"))
    ingredients.add(Ingredient("Salt", "1,5 tsk"))
    ingredients.add(Ingredient("Rågmjöl", "75g"))
    ingredients.add(Ingredient("Valnötter", "150g"))

    var recepi: Recepi = Recepi(
        "Valnötsbröd",
        "Dyrt, men kostar det så smakar det!",
        R.drawable.valnotsbrod,
        "Ingredienser:",
        recepiSteps,
        ingredients)
    return recepi
}

fun getTorstenBrod(): Recepi {
    var  recepiSteps = ArrayList<RecepiStep>()
    recepiSteps.add(
        RecepiStepPrepare(
        """
        Blanda alla ingredienser. 
        När allt är väl blandat låt jäsa i 8-10 tim (gärna över natten).
        """.trimIndent())
    )
    recepiSteps.add(RecepiStepWait("Jäsning i 8-10 timmar", 36000))
    recepiSteps.add(RecepiStepPrepare("Dags att vika 1:a gången och sedan 30 min jäsning"))
    recepiSteps.add(RecepiStepWait("Jäsning i 30 min", 1800))
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

    var ingredients = ArrayList<Ingredient>()
    ingredients.add(Ingredient("Jäst", "3g"))
    ingredients.add(Ingredient("Salt", "1,5 tsk"))
    ingredients.add(Ingredient("Vetemjöl special", "300g"))
    var recepi: Recepi = Recepi(
        "Torsten Bröd",
        "Ett enkelt gott bröd",
        R.drawable.torsten_brod,
        "Det här brödet kan man ta fram när man har gäster.\nIngredienser:",
        recepiSteps,
        ingredients)
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