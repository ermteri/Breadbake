package se.torsteneriksson.recepihandler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "recepihandler"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecepiStepsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecepiStepsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mRecepiHandler: IRecepiHandlerService? = null
    var mActivity: MainActivity? = null

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val textView = activity?.findViewById<TextView>(R.id.id_timer)
            textView?.setText(intent?.getStringExtra("Message"))
            updateGui()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        activity?.registerReceiver(
            mBroadcastReceiver,
            IntentFilter("se.torsteneriksson.recepihandler.countdown_br")
        );
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_recepi_steps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as MainActivity
        mRecepiHandler = mActivity?.getRecepiHanlderService()
        val button: ImageButton = mActivity?.findViewById(R.id.id_next) as ImageButton
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                next(v)
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecepiStepsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecepiStepsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // User interface
    fun next(view: View) {
        val textView = mActivity?.findViewById<TextView>(R.id.id_stepinstruction)
        // Test of step
        mRecepiHandler?.nextStep()
        var recepi = mRecepiHandler?.getRecepi()
        val step = recepi?.getCurrentStep()
        updateGui()
    }

    // Private methods
    private fun updateGui() {
        val title = mActivity?.findViewById<TextView>(R.id.id_recepi_title)
        val overallDescription = mActivity?.findViewById<TextView>(R.id.id_overall_description)
        val stepinstruction = mActivity?.findViewById<TextView>(R.id.id_stepinstruction)
        val progressbar = mActivity?.findViewById<ProgressBar>(R.id.id_step_progress)
        val timer = mActivity?.findViewById<TextView>(R.id.id_timer)
        var recepi = mRecepiHandler?.getRecepi()
        if (recepi != null) {
            stepinstruction?.setText(recepi?.getCurrentStep()?.description)
            if (recepi?.getCurrentStep() is RecepiStepPrepare)
                timer?.setText(getString(R.string.timer))
            val p = recepi.progress()
            progressbar?.setProgress(recepi.progress().roundToInt())
        }
    }

}