package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.extensions.asColorInt
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

class CategoryEditViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Category, ItemDelegate<Category>>(viewGroup, LAYOUT_TYPE) {
    @BindView(R.id.txt_category)
    lateinit var categoryTextView: TextView
    @BindView(R.id.txt_category_payments)
    lateinit var categoryPaymentsTextView: TextView
    @BindView(R.id.view_color)
    lateinit var colorView: View

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
        categoryTextView.text = item.name.toUpperCase()
        categoryPaymentsTextView.text = itemView.context.getString(R.string.label_x_linked_payments, item.payments.size)
        colorView.setBackgroundColor(item.color.asColorInt)
    }

    override fun onClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>?) {
        delegate?.onItemClicked(item)
    }

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.viewholder_category_edit
    }
}