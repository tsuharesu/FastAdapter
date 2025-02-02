package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.app.databinding.ActivitySampleBinding
import com.mikepenz.fastadapter.app.items.SimpleImageItem
import com.mikepenz.fastadapter.app.paged.DemoEntity
import com.mikepenz.fastadapter.app.paged.DemoEntityViewModel
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import com.mikepenz.fastadapter.select.getSelectExtension


class PagedActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySampleBinding

    //save our FastAdapter
    private lateinit var mFastAdapter: FastAdapter<SimpleImageItem>

    //save our FastAdapter
    private lateinit var mItemAdapter: PagedModelAdapter<DemoEntity, SimpleImageItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        //create the activity
        super.onCreate(savedInstanceState)
        binding = ActivitySampleBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        // Handle Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.sample_paged_list)

        val asyncDifferConfig = AsyncDifferConfig.Builder<DemoEntity>(object : DiffUtil.ItemCallback<DemoEntity>() {
            override fun areItemsTheSame(oldItem: DemoEntity, newItem: DemoEntity): Boolean {
                return oldItem.identifier == newItem.identifier
            }

            override fun areContentsTheSame(oldItem: DemoEntity, newItem: DemoEntity): Boolean {
                return oldItem == newItem
            }
        }).build()

        //create our ItemAdapter which will host our items
        mItemAdapter = PagedModelAdapter<DemoEntity, SimpleImageItem>(asyncDifferConfig, { arg: Int -> SimpleImageItem().setPlaceholder() }) {
            SimpleImageItem().withName(it.data1 ?: "").withDescription(it.data2 ?: "").apply {
                identifier = it.identifier.toLong()
                isSelectable = true
                withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yang_zhuo_yong_cuo,_tibet-china-63.jpg")
            }
        }

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(listOf(mItemAdapter))

        //
        val selectExtension = mFastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //configure our fastAdapter
        //rv.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = mFastAdapter

        val viewModel = ViewModelProvider(this, DemoEntityViewModel.DemoEntityViewModelFactory(this.application))
            .get(DemoEntityViewModel::class.java)

        //listen to data changes and pass it to adapter for displaying in recycler view
        viewModel.demoEntitiesList.observe(this, { t -> mItemAdapter.submitList(t!!) })

        //if we do this. the first added items will be animated :D
        Handler().postDelayed({
            //restore selections (this has to be done after the items were added
            mFastAdapter.withSavedInstanceState(savedInstanceState)
        }, 50)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }


    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
