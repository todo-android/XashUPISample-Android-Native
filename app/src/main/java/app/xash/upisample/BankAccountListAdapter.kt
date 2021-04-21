package app.xash.upisample


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.xash.upisample.databinding.BankListItemBinding
import com.icici.ultrasdk.Models.Accounts

class BankAccountListAdapter :
    ListAdapter<Accounts, BankAccountListAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        val binding =
            BankListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewholder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewholder(itemView: BankListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item: Accounts) = with(itemView) {

            setOnClickListener {
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Accounts>() {
    override fun areItemsTheSame(oldItem: Accounts, newItem: Accounts): Boolean {
        return oldItem.account == newItem.account
    }

    override fun areContentsTheSame(oldItem: Accounts, newItem: Accounts): Boolean {
        return oldItem.equals(newItem)
    }
}