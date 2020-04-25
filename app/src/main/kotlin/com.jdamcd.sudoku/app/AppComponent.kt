package com.jdamcd.sudoku.app

import com.jdamcd.sudoku.repository.database.DatabaseModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    BinderModule::class,
    AppModule::class,
    DatabaseModule::class])
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<App>
}
