package com.denofdevelopers.thisbeweatherapp.application

import com.denofdevelopers.thisbeweatherapp.di.modules.ApiModule
import com.denofdevelopers.thisbeweatherapp.screen.home.HomeComponent
import com.denofdevelopers.thisbeweatherapp.screen.home.HomeModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class
    ]
)
interface AppComponent {
    fun plus(homeModule: HomeModule): HomeComponent
}