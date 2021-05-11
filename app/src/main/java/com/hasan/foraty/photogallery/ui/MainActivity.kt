package com.hasan.foraty.photogallery.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hasan.foraty.photogallery.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigate(PhotoGalleryFragment.newInstance(),false)

    }

    /**
     * navigate to chosen Fragment
     * @param fragment Destination Fragment
     * @param rollBack ability to go back to previous Fragment
     */
    fun navigate(fragment:Fragment,rollBack:Boolean){
        val currentFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val supprot=supportFragmentManager.beginTransaction()
        if (rollBack){
            supprot.addToBackStack("")
        }
        if (currentFragment==null){
            supprot
                    .add(R.id.fragmentContainer,fragment)
        }else{
            supprot
                    .replace(R.id.fragmentContainer,fragment)
        }
        supprot.commit()
    }
    companion object{
        fun newIntent(context: Context) = Intent(context,MainActivity::class.java)
    }
}