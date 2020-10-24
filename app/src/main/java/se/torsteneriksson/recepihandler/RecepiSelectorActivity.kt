package se.torsteneriksson.recepihandler


import android.app.Activity
import android.content.Intent
import android.graphics.ColorSpace
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


interface CellClickListener {
    fun onCellClickListener(data: Model)
}

class Model(
    val recepiName: String,
    val recepiImage: Int
)

class RecepiSelectorActivity : AppCompatActivity(), CellClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recepi_selector)
        getSupportActionBar()?.setTitle(getString(R.string.recepi_selector_title))
        val extras = intent.getBundleExtra("myData")
        val myDataSet: Array<Model> = arrayOf(
            Model("Valnötsbröd", 0),
            Model( "Torsten Bröd", 1),
            Model( "Stinas skorpor", 2))

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(myDataSet, this)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    override fun onCellClickListener(data: Model) {
        val intent: Intent = Intent(data.recepiName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}

class MyAdapter(private val myDataset: Array<Model>, private val cellClickListener: CellClickListener) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val rowView: CardView) : RecyclerView.ViewHolder(rowView){

    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
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
        val tv = holder.rowView.findViewById<TextView>(R.id.id_recepi_row_tv)
        val data = myDataset[position]
        tv.setText(data.recepiName)
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(data)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}