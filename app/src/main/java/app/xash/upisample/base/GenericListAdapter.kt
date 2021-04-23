package app.xash.upisample.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.ListAdapter

abstract class GenericListAdapter<T : Any>(
    @IdRes val layoutId: Int,
    inline val bind: (item: T, holder: BaseViewHolder, itemCount: Int) -> Unit
) : ListAdapter<T, BaseViewHolder>(BaseItemCallback<T>()) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(getItem(position), holder, itemCount)
    }

    override fun getItemViewType(position: Int) = layoutId
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(
            viewType, parent, false
        )
        return BaseViewHolder(root as ViewGroup)
    }

    override fun getItemCount() = currentList.size

}