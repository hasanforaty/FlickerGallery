package com.hasan.foraty.photogallery.data

import android.content.Context
import android.preference.Preference
import android.preference.PreferenceManager

private const val PREF_QUERY_SEARCH="searchQuery"

/**
 * Object to store and retrieve query from SharedPreferences
 */
object QueryPreference{
    /**
     * getStoredQuery  a method to retrieve value in sharedPreference
     * used for retrieve  search query string along the phone
     * @param context instance of any kind of context
     * @return query if exists String of last value saved
     */
    fun getStoredQuery(context: Context):String{
        return context.getSharedPreferences(PREF_QUERY_SEARCH,Context.MODE_PRIVATE).getString(PREF_QUERY_SEARCH,"")!!
    }
    /**
     * setStoreQuery  a method to save value in sharedPreference
     * used for save  search query string along the phone
     * @param context instance of any kind of context
     * @param query string of query needed to save
     */
    fun setStoreQuery(context: Context,query:String){
        context.getSharedPreferences(PREF_QUERY_SEARCH,Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_QUERY_SEARCH,query)
                .apply()
    }
}