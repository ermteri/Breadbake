package se.torsteneriksson.recepihandler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max


interface CellClickListener {
    fun onCellClickListener(data: RecepiListModel)
}

class RecepiListModel(
    val recepiName: String?,
    val recepiSlogan: String?,
    val recepiImage: Int
)

/**
 * A simple [Fragment] subclass.
 * Use the [RecepiSelectorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecepiSelectorFragment : Fragment(), CellClickListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mViewAdapter: RecyclerView.Adapter<*>
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private lateinit var mActivity: MainActivity
    private var mRecepiLoadPending: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        mActivity = activity as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_recepi_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //getSupportActionBar()?.setTitle(getString(R.string.recepi_selector_title))
        val recepiListDataSet = getRecepieListData()
        mViewManager = LinearLayoutManager(activity)
        mViewAdapter = RecepiSelectorAdapter(recepiListDataSet, this)
        mRecyclerView = requireActivity().findViewById<RecyclerView>(R.id.selector_recycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = mViewManager

            // specify an viewAdapter (see also next example)
            adapter = mViewAdapter
        }
        val fetch = activity?.findViewById<ImageButton>(R.id.id_action_fetch)
        fetch?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                updateRecepiList()
            }
        })
    }

    override fun onCellClickListener(data: RecepiListModel) {
        mActivity.setCurrentRecepi(data.recepiName as String)
    }

    fun getRecepieListData(): ArrayList<RecepiListModel> {
        var myDataSet: ArrayList<RecepiListModel> = arrayListOf()
        for (recepi in mActivity.mRecepiList.getRecepies()) {
                myDataSet.add(RecepiListModel(recepi.name, recepi.slogan, recepi.image))
        }
        return myDataSet
    }

    fun updateRecepiList() {
        mActivity.mRecepiList.loadRecepies(true)
        val myDataSet = getRecepieListData()
        (mViewAdapter as RecepiSelectorAdapter).updateDataSet(myDataSet)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RecepiSelectorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecepiSelectorFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

class RecepiSelectorAdapter(
    private var myDataset: ArrayList<RecepiListModel>,
    private val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<RecepiSelectorAdapter.MyViewHolder>() {

    fun updateDataSet(data: ArrayList<RecepiListModel>) {
        myDataset = data
        notifyDataSetChanged()
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val rowView: CardView) : RecyclerView.ViewHolder(rowView) {

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecepiSelectorAdapter.MyViewHolder {
        // create a new view
        val rowView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recepirow_layout, parent, false) as CardView
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(rowView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val title = holder.rowView.findViewById<TextView>(R.id.id_recepi_title)
        val slogan = holder.rowView.findViewById<TextView>(R.id.id_recepi_slogan_tv)
        val image = holder.rowView.findViewById<ImageView>(R.id.recepi_picture_img)

        val data = myDataset[position]
        title.setText(data.recepiName)
        slogan.setText(data.recepiSlogan)
        image.setImageResource(data.recepiImage)
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(data)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

}
