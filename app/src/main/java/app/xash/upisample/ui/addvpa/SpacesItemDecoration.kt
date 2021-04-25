package app.xash.upisample.ui.addvpa

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Int, private val orientation: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (orientation == RecyclerView.VERTICAL) {
            outRect.left = space
            outRect.right = space
        } else {
            val firstItem: Boolean = parent.getChildAdapterPosition(view) == 0
            val lastItem: Boolean = parent.getChildAdapterPosition(view) == parent.childCount - 1
            outRect.top = if (firstItem) 0 else space
            outRect.bottom = if (lastItem) 0 else space
        }
    }

}
