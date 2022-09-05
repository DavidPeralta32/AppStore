package com.daviperalta.stores

interface OnClickListener {
    fun onClick(storeEntity:StoreEntity)

    fun onFavoriteStore(storeEntity: StoreEntity)

}