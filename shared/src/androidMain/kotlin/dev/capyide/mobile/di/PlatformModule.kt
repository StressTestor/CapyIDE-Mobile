package dev.capyide.mobile.di

import dev.capyide.mobile.core.config.EncryptedSettingsRepository
import dev.capyide.mobile.core.config.SettingsRepository
import dev.capyide.mobile.core.file.FileSystemProvider
import org.koin.dsl.module

val platformModule = module {
    single<SettingsRepository> { EncryptedSettingsRepository(get()) }
    single { FileSystemProvider(get()) }
}
