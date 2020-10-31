package se.torsteneriksson.recepihandler

import android.os.Parcel
import android.os.Parcelable

enum class STEPTYPE {
    TIMER, PREPARE
}

// This class describes a recepi step
open class RecepiStep (open val description: String = "", open val type: STEPTYPE) {

}

class RecepiStepWait (override val description: String, val time: Long = 0):
    RecepiStep(description=description, type=STEPTYPE.TIMER){

}


class RecepiStepPrepare (override val description: String):
    RecepiStep(description=description, type=STEPTYPE.PREPARE){

}

class Recepi(val uid: String?, val name: String?, val slogan: String?, val image: Int, val ingridients: String?,
             val recepiSteps: ArrayList<RecepiStep>): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        TODO("recepiSteps")
    ) {
    }
    var mCurrentStep: Int = -1

    fun getCurrentStep(): RecepiStep? {
        when {
            mCurrentStep == -1 -> return null
            mCurrentStep < recepiSteps.size -> return recepiSteps[mCurrentStep]
        }
        return recepiSteps[recepiSteps.size - 1]
    }

    fun nextStep() {
        if (mCurrentStep < recepiSteps.size - 1) {
            mCurrentStep++
        }
    }
    fun prevStep() {
        if (mCurrentStep > 0) {
            mCurrentStep--
        }
    }
    fun progress(): Float {
        return ((mCurrentStep.toFloat()+1)/recepiSteps.size * 100)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
    }

    companion object CREATOR : Parcelable.Creator<Recepi> {
        override fun createFromParcel(parcel: Parcel): Recepi {
            return Recepi(parcel)
        }

        override fun newArray(size: Int): Array<Recepi?> {
            return arrayOfNulls(size)
        }
    }
}