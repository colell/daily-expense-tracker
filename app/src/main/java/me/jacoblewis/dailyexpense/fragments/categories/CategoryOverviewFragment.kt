package me.jacoblewis.dailyexpense.fragments.categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.StatsType
import me.jacoblewis.dailyexpense.commons.observeBoth
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import java.util.*
import javax.inject.Inject

class CategoryOverviewFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(CategoryEditFragment::class.java, drawerNavId = R.id.menu_item_categories)

    init {
        MyApp.graph.inject(this)
    }

    @Inject
    lateinit var categoryAdapter: GeneralItemAdapter

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(CategoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewBound(view: View) {
        toolbar.title = "Categories"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.callback = this
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        navigationController.linkToolBarToDrawer(toolbar)

        viewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))

        observeBoth(viewModel.categories, viewModel.remainingBudget, this) { cats, remainingBudget ->
            val items: MutableList<IdItem<*>> = mutableListOf()
            items.add(Stats(remainingBudget, cats, StatsType.PieChart))
            items.addAll(cats)
            categoryAdapter.submitList(items)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_category_overview, menu)
        val menuItem = menu.findItem(R.id.spinner)
        val monthSpinner = menuItem.actionView as AppCompatSpinner

        val data = viewModel.getPreviousMonths(3)
        val mapped = data.map { mapOf(Pair("title", it.display)) }

        val simpleAdapter = SimpleAdapter(toolbar.context, mapped, android.R.layout.simple_dropdown_item_1line, arrayOf("title"), intArrayOf(android.R.id.text1))
        monthSpinner.adapter = simpleAdapter
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val cal = data[position].calendar
                viewModel.updateCategoryDate(cal, DateHelper.endOfMonth(cal))
            }

        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onItemClicked(item: Any) {

    }


    @OnClick(R.id.fab_add_new)
    fun addNewCategory(v: View) {
        navigationController.navigateTo(NavScreen.EditCategories)
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}