package com.federico.di

import com.federico.auth.AuthViewModel
import com.federico.data.CustomerRepositoryImpl
import com.federico.data.domain.CustomerRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** @viewModelOf --> So this is one a special syntax in a coin dependency injection library that allows you to specify the
 * actual type in which you want to inject those other different types.
 * With this syntax you don't need to actually provide that repository within the ViewModel itself,
 * we just create the singleton for our repository.
 */
val sharedModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
    viewModelOf(::AuthViewModel)
}

/** We will pass the Android context as config parameter*/
fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule)
    }
}