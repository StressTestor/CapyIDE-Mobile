package dev.capyide.mobile.di

import dev.capyide.mobile.core.ai.AiProviderRegistry
import dev.capyide.mobile.core.ai.AiService
import dev.capyide.mobile.core.ai.StubAiProviderRegistry
import dev.capyide.mobile.core.update.GithubUpdateChecker
import dev.capyide.mobile.core.update.UpdateChecker
import dev.capyide.mobile.viewmodel.AiAssistViewModel
import dev.capyide.mobile.viewmodel.EditorViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val sharedModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single<UpdateChecker> { GithubUpdateChecker(get()) }
    single<AiProviderRegistry> { StubAiProviderRegistry() }
    single { AiService(get()) }
    factory { EditorViewModel() }
    factory { AiAssistViewModel(get(), get()) }
}
