package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.databinding.ItemRouteLayoutBinding

class AdapterRoutes(
    private val data: List<Route>,
    val itemClickListener: (Route)->Unit) : RecyclerView.Adapter<AdapterRoutes.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRoutes.ViewHolder {
        val binding = ItemRouteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterRoutes.ViewHolder, position: Int) {
        with(holder){
            with(data[position]){
                binding.txtNameRoute.text=name
                binding.txtRvAsistentes.text=noRealWorkers.toString()
                binding.txtRvCliente.text=client
                binding.txtRvObReclutados.text=noReclWorkers.toString()
                binding.txtRvObSolicitados.text=noRequWorkers.toString()
                holder.itemView.setOnClickListener {
                    itemClickListener(data[position])
                }
                binding.btnRvSeeRoute.setOnClickListener {
                    itemClickListener(data[position])
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: ItemRouteLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}