package se.torsteneriksson.recepihandler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import se.torsteneriksson.recepihandler.database.RecepiStepPrepare
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [RecepiStepsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecepiStepsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var mRecepiHandler: IRecepiHandlerService? = null
    private var mActivity: MainActivity? = null
    private var mIActivity: IMainActivity? = null

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val textView = activity?.findViewById<TextView>(R.id.id_timer)
            textView?.text = intent?.getStringExtra("Message")
            updateGui()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        activity?.registerReceiver(
            mBroadcastReceiver,
            IntentFilter("se.torsteneriksson.recepihandler.countdown_br")
        )
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
        mIActivity = activity as IMainActivity
        mRecepiHandler = mIActivity?.getRecepiHandlerService()
        val next_step = mActivity?.findViewById<ImageButton>(R.id.id_next_step)
        val prev_step = mActivity?.findViewById<ImageButton>(R.id.id_prev_step)
        next_step?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mRecepiHandler?.nextStep()
                updateGui()
            }
        })
        prev_step?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mRecepiHandler?.prevStep()
                updateGui()
            }
        })
        updateGui()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RecepiStepsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            RecepiStepsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    // Private methods
    private fun updateGui() {
        val stepinstruction = mActivity?.findViewById<TextView>(R.id.id_stepinstruction)
        val progressbar = mActivity?.findViewById<ProgressBar>(R.id.id_step_progress)
        val timer = mActivity?.findViewById<TextView>(R.id.id_timer)
        val recepi = mRecepiHandler?.recepi
        if (recepi != null) {
            if (recepi.getCurrentStep() == null)
                stepinstruction?.text = getString(R.string.stepinstruction)
             else
                stepinstruction?.text = ((recepi.getCurrenStepId() + 1).toString() + ". "
                        + recepi.getCurrentStep()?.description)
            if (recepi.getCurrentStep() is RecepiStepPrepare)
                timer?.text = getString(R.string.timer)
            progressbar?.progress = recepi.progress().roundToInt()
        }
    }
}