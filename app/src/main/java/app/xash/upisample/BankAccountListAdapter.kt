package app.xash.upisample


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.xash.upisample.databinding.BankListItemBinding
import com.icici.ultrasdk.Models.Providers

class BankAccountListAdapter(val itemClick: ((bank: Providers) -> Unit) = {}) :
    ListAdapter<Providers, BankAccountListAdapter.ItemViewHolder>(object :
        DiffUtil.ItemCallback<Providers>() {
        override fun areItemsTheSame(oldItem: Providers, newItem: Providers): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Providers, newItem: Providers): Boolean {
            return oldItem.equals(newItem)
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            BankListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: BankListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Providers) {
            binding.bankListNameTextView.text = item.accountProvider
            binding.root.setOnClickListener {
                itemClick.invoke(item)
            }
        }
    }
}
