package com.daviperalta.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.daviperalta.stores.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
    private lateinit var mBinding:ActivityMainBinding

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

//        mBinding.btnSave.setOnClickListener {
//            val store = StoreEntity(name = mBinding.etName.text.toString().trim())
//
//            Thread{
//                StoreApplication.dataBase.storeDao().addStore(store)
//            }.start()
//
//
//            mAdapter.add(store)
//        }

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }

        setupRecyclerView()

    }

    private fun launchEditFragment(args : Bundle? = null) {
        val fragment = EditStoreFragment()

        if(args != null) fragment.arguments = args

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab(false)
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this,2)
        getStores()

        mBinding.rvStored.apply{
           setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores(){
        doAsync {
            val stores = StoreApplication.dataBase.storeDao().getAllStores()
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }

    /*
    * OnClickListener
    * */
    override fun onClick(storeId : Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreApplication.dataBase.storeDao().updateStore(storeEntity)
            uiThread {
                mAdapter.update(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        doAsync {
            StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
            uiThread {
                mAdapter.delete(storeEntity)
            }
        }

    }

    /*
    * MainAux
    */
    override fun hideFab(isVisible: Boolean) {
        if(isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {

    }
}