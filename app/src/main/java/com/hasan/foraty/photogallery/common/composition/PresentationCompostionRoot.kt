package com.hasan.foraty.photogallery.common.composition

import com.hasan.foraty.photogallery.screens.common.viewsmvc.ViewMvcFactory

class PresentationCompostionRoot(activityCompostionRoot: ActivityCompositionRoot) {

    private val layoutInflater = activityCompostionRoot.layoutInflater


    val viewMvc = ViewMvcFactory(layoutInflater)

}