package com.hasan.foraty.photogallery.data

import android.content.Context
import android.preference.Preference
import android.preference.PreferenceManager

private const val PREF_QUERY_SEARCH="searchQuery"
private const val PREF_LAST_ID = "lastId"

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

    /**
     * getStoredFirstId get First picture seen id
     * @param context context of program
     * @return id of First picture sean
     */
    fun getStoredFirstId(context: Context):String=
        context.getSharedPreferences(PREF_LAST_ID,Context.MODE_PRIVATE)
            .getString(PREF_LAST_ID,"")!!

    /**
     * setStoredFirstId store First picture id
     * @param context context of program4
     * @param id id of First picture
     */
    fun setStoredFirstId(context: Context, id:String){
        context.getSharedPreferences(PREF_LAST_ID,Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_LAST_ID,id)
            .apply()
    }
}