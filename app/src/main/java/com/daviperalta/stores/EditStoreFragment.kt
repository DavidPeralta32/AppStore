package com.daviperalta.stores

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daviperalta.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity : MainActivity? = null

    private var mIsEditMode : Boolean = false
    private var mStoreEntity: StoreEntity? =  null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container,false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val id = arguments?.getLong(getString(R.string.arg_id) , 0)
        if(id != null && id != 0L){
            mIsEditMode = true
            getStore(id)
        }else{
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")
        }

        setupActionBar()

        setupTextFields()
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.edit_store_title_add)
        else getString(R.string.edit_store_title_edit)

        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {
        //si el usuario agrega un cambio cunado este en require se pasa a okey
        with(mBinding){
            etName.addTextChangedListener { validateFields(tilName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreApplication.dataBase.storeDao().getStoreById(id)
            uiThread {
                if(mStoreEntity != null){
                    setUIStore(mStoreEntity!!)
                }
            }
        }
    }

    private fun setUIStore(storeEntity: StoreEntity) {
            with(mBinding){
                etName.setText(storeEntity.name)
                etPhone.setText(storeEntity.phone)
                etWebsite.setText(storeEntity.website)
                etPhotoUrl.setText(storeEntity.photoUrl)

            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save ->{
                if(mStoreEntity != null && validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone,mBinding.tilName)){
                    //                val store =  StoreEntity(name = sName, phone = sPhone,website = sWebSite, photoUrl = sPhotoUrl)
                    with(mStoreEntity!!){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }
                    doAsync {
                        if(mIsEditMode){
                            StoreApplication.dataBase.storeDao().updateStore(mStoreEntity!!)
                        }else{
                            mStoreEntity!!.id = StoreApplication.dataBase.storeDao().addStore(mStoreEntity!!)
                        }
                        uiThread {

                            hideKeyboard()

                            if(mIsEditMode){
                                mActivity?.updateStore(mStoreEntity!!)

                                Snackbar.make(mBinding.root,R.string.edit_store_message_update_succes, Snackbar.LENGTH_SHORT)
                            }else {
                                mActivity?.addStore(mStoreEntity!!)
                                Toast.makeText(
                                    mActivity,
                                    R.string.edit_store_message_save_succes,
                                    Toast.LENGTH_SHORT
                                ).show()

                                mActivity?.onBackPressed()
                            }
                        }
                    }
                }
                true
            }
            else ->return super.onOptionsItemSelected(item)
        }

    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true
        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_require)
                textField.editText?.requestFocus()
                isValid = false
            }else textField.error = null
        }
        if(!isValid) Snackbar.make(mBinding.root, R.string.edit_message_valid, Snackbar.LENGTH_SHORT).show()
        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if(mBinding.etPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_require)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }
        if(mBinding.etPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_require)
            mBinding.etPhone.requestFocus()
            isValid = false
        }
        if(mBinding.etName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_require)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun hideKeyboard(){
        val inn = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inn.hideSoftInputFromWindow(requireView().windowToken, 0)

    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }


    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }


}