package me.jacoblewis.dailyexpense.fragments.categories

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.CategoryItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.*
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import java.util.*
import javax.inject.Inject

class ChooseCategoryFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(ChooseCategoryFragment::class.java)

    init {
        MyApp.graph.inject(this)
    }

    @Inject
    lateinit var categoryAdapter: CategoryItemAdapter

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view_category)
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(CategoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewBound(view: View) {
        toolbar.title = "Payment Category"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.setCallback(this)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        viewModel.categories.observe(this, Observer {
            if (it != null) {
                categoryAdapter.updateItems(it)
            }
        })
        viewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
        }
        return true
    }

    override fun onItemClicked(item: Any) {
        if (item is Category) {
            val payment: Payment = arguments?.get(ARG_PAYMENT) as? Payment ?: Payment(0f)
            payment.categoryId = item.categoryId
            viewModel.savePayment(payment)

            navigationController.navigateTo(NavScreen.Main)
        }
    }

    @OnClick(R.id.fab_add_new)
    fun addNewCategory(v: View) {
        navigationController.navigateTo(NavScreen.EditCategory)
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        val revealSetting: RevealAnimationSetting = arguments?.get("reveal") as? RevealAnimationSetting
                ?: return false
        AnimationUtils.startCircularExitAnimation(context!!, view!!, revealSetting, ContextCompat.getColor(context!!, R.color.white), ContextCompat.getColor(context!!, R.color.colorAccent))
        return false
    }
}