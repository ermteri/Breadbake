package se.torsteneriksson.recepihandler.database

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
enum class STEPTYPE {
    TIMER, PREPARE
}


// This class describes a recepi step
@Serializable
sealed class RecepiStep () {
    abstract val description: String
    abstract val steptype: STEPTYPE

}

@Serializable
class RecepiStepWait (override val description: String, val time: Long = 0,
                      override val steptype: STEPTYPE = STEPTYPE.TIMER
):
    RecepiStep(){}

@Serializable
class RecepiStepPrepare (override val description: String,
                         override val steptype: STEPTYPE = STEPTYPE.PREPARE
):
    RecepiStep(){}

@Serializable
class Ingredient(val name: String, val amount: String) {}

@Serializable
class Recepi(val name: String?, val slogan: String?, val image: Int, val description: String?,
             val recepiSteps: ArrayList<RecepiStep>, val ingredients: ArrayList<Ingredient>):
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        TODO("recepiSteps"),
        TODO("ingredients")

    ) {}
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
@Serializable
class RecepiList(val recepies: Array<Recepi>){}