package com.denofdevelopers.thisbeweatherapp.screen.home

import com.denofdevelopers.thisbeweatherapp.di.ActivityScope
import com.denofdevelopers.thisbeweatherapp.network.ApiService
import dagger.Module
import dagger.Provides

@Module
class HomeModule(private var activity: HomeActivity) {

    @Provides
    @ActivityScope
    fun provideHomeActivity(): HomeActivity {
        return activity
    }

    @Provides
    @ActivityScope
    fun provideHomePresenter(apiService: ApiService): HomePresenter {
        return HomePresenter(activity, apiService)
    }

}