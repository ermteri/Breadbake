package se.torsteneriksson.recepihandler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class IngrediantListModel(
    val name: String?,
    val amount: String?
)

/**
 * A simple [Fragment] subclass.
 * Use the [RecepiDescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecepiDescriptionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var mRecepiHandler: IRecepiHandlerService? = null
    var mIActivity: IMainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recepi_description, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        val title_tv = itemView.findViewById<TextView>(R.id.recepi_title_tv)
        val description_tv = itemView.findViewById<TextView>(R.id.recepi_description_tv)
        val image_img = itemView.findViewById<ImageView>(R.id.recepi_picture_img)
        mIActivity = activity as IMainActivity
        mRecepiHandler = mIActivity?.getRecepiHandlerService()
        val recepi = mRecepiHandler?.getRecepi()
        title_tv.setText(recepi?.name)
        description_tv.setText(recepi?.description)
        image_img.setImageDrawable(itemView.getResources().getDrawable(recepi?.image as Int))
        // Ingredients
        var myDataSet: ArrayList<IngrediantListModel> = arrayListOf()
        for (ingredient in recepi.ingredients) {
            myDataSet.add(IngrediantListModel(ingredient.name, ingredient.amount))
        }

        viewManager = LinearLayoutManager(activity)
        viewAdapter = IngrediantAdapter(myDataSet)

        recyclerView = requireActivity().findViewById<RecyclerView>(R.id.id_ingredients_recycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RecepiDescriptionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(title: String, description: String, img_id: Int) =
            RecepiDescriptionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

class IngrediantAdapter(private val myDataset: ArrayList<IngrediantListModel>) :
    RecyclerView.Adapter<IngrediantAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val rowView: CardView) : RecyclerView.ViewHolder(rowView){

    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): IngrediantAdapter.MyViewHolder {
        // create a new view
        val rowView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_layout, parent, false) as CardView
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(rowView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val name = holder.rowView.findViewById<TextView>(R.id.id_ingredient_name)
        val amount = holder.rowView.findViewById<TextView>(R.id.id_ingredient_amount)

        val data = myDataset[position]
        name.setText(data.name)
        amount.setText(data.amount)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
