package dev.capyide.mobile.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(sharedModule, platformModule)
    }
}
