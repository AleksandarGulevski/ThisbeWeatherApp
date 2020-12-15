package com.denofdevelopers.thisbeweatherapp.screen.home

import com.denofdevelopers.thisbeweatherapp.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [HomeModule::class]
)
interface HomeComponent {
    fun inject(homeActivity: HomeActivity): HomeActivity
}