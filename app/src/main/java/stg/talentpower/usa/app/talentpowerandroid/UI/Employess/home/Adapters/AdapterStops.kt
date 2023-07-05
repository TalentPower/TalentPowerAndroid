package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.databinding.ItemStopsLayoutBinding

class AdapterStops(
    private val data: List<Stop>,
    val itemClickListener: (View,Stop)->Unit,
):RecyclerView.Adapter<AdapterStops.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterStops.ViewHolder {
        val binding= ItemStopsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AdapterStops.ViewHolder, position: Int) {
        with(holder){
            with(data[position]){
                binding.txtNameStop.text=name
                binding.txtTimeStop.text=time
                binding.btnImgEditStop.setOnClickListener{ view->
                    itemClickListener(view,data[position])
                }
                binding.btnImgDeleteStop.setOnClickListener {view->
                    itemClickListener(view,data[position])
                }
            }
        }
    }
    inner class ViewHolder(val binding: ItemStopsLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}