package com.jdamcd.sudoku.app

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
internal class AppModule {

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources
}
