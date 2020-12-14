package com.denofdevelopers.thisbeweatherapp.application

import com.denofdevelopers.thisbeweatherapp.di.modules.ApiModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class
    ]
)
interface AppComponent {
}