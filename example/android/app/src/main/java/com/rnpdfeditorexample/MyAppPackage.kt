package com.rnpdfeditorexample

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager
import com.rnpdfeditorexample.thumbnail.ThumbnailModule

class MyAppPackage : ReactPackage {
    override fun createNativeModules(context: ReactApplicationContext) =
        listOf<NativeModule>(ThumbnailModule(context))

    override fun createViewManagers(context: ReactApplicationContext) =
        listOf<ViewManager<View, ReactShadowNode<*>>>()
}