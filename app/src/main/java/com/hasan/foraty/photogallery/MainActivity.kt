package com.hasan.foraty.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

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
}