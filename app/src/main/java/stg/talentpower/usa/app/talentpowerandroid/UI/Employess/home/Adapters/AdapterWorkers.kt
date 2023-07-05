package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Worker
import stg.talentpower.usa.app.talentpowerandroid.databinding.ItemWorkerLayoutBinding

class AdapterWorkers (
    val list:List<Worker>,
    val itemClickListener: (Worker)->Unit
    ): RecyclerView.Adapter<AdapterWorkers.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterWorkers.ViewHolder {
        val binding=ItemWorkerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterWorkers.ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.txtNameWorker.text=name
                binding.txtDaysWorker.text="$workedDays"
                binding.txtFacturedWorker.text="$availableFacture"
                holder.itemView.setOnClickListener {
                    itemClickListener(list[position])
                }
            }
        }
    }

    override fun getItemCount(): Int =list.size

    inner class ViewHolder(val binding: ItemWorkerLayoutBinding) : RecyclerView.ViewHolder(binding.root)


}