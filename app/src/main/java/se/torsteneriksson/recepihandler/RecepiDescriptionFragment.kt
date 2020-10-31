package se.torsteneriksson.recepihandler

import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


private const val ARG_TITLE = "title"
private const val ARG_DESCRIPTION = "description"
private const val ARG_IMG_ID = "img_id"

/**
 * A simple [Fragment] subclass.
 * Use the [RecepiDescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecepiDescriptionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var title: String = ""
    private var description: String = ""
    private var img_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE).toString()
            description = it.getString(ARG_DESCRIPTION).toString()
            img_id = it.getInt(ARG_IMG_ID)
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
        title_tv.setText(title)
        description_tv.setText(description)
        image_img.setImageDrawable(itemView.getResources().getDrawable(img_id))
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
                    putString(ARG_TITLE, title)
                    putString(ARG_DESCRIPTION, description)
                    putInt(ARG_IMG_ID, img_id)
                }
            }
    }
}