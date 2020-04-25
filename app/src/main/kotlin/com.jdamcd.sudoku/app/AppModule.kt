package com.jdamcd.sudoku.app

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides

@Module
internal class AppModule {

    @Provides
    fun provideApplicationContext(application: App): Context = application.applicationContext

    @Provides
    fun provideResources(context: Context): Resources = context.resources
}
