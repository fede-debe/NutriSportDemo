package com.federico.di

import com.federico.admin_panel.AdminPanelViewModel
import com.federico.auth.AuthViewModel
import com.federico.cart.CartViewModel
import com.federico.category_search.CategorySearchViewModel
import com.federico.checkout.CheckoutViewModel
import com.federico.checkout.domain.PaypalApi
import com.federico.data.AdminRepositoryImpl
import com.federico.data.CustomerRepositoryImpl
import com.federico.data.OrderRepositoryImpl
import com.federico.data.domain.AdminRepository
import com.federico.data.domain.CustomerRepository
import com.federico.data.domain.ProductRepository
import com.federico.data.ProductRepositoryImpl
import com.federico.data.domain.OrderRepository
import com.federico.details.DetailsViewModel
import com.federico.home.HomeGraphViewModel
import com.federico.manage_product.ManageProductViewModel
import com.federico.payment_completed.PaymentViewModel
import com.federico.products_overview.ProductsOverviewViewModel
import com.federico.profile.ProfileViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** @viewModelOf --> So this is one a special syntax in a coin dependency injection library that allows you to specify the
 * actual type in which you want to inject those other different types.
 * With this syntax you don't need to actually provide that repository within the ViewModel itself,
 * we just create the singleton for our repository.
 */
val sharedModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
    single<AdminRepository> { AdminRepositoryImpl() }
    single<ProductRepository> { ProductRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<PaypalApi> { PaypalApi() }
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeGraphViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ManageProductViewModel)
    viewModelOf(::AdminPanelViewModel)
    viewModelOf(::ProductsOverviewViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::CategorySearchViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::PaymentViewModel)
}

/** the reason why I'm using a targetModule is because that I need to provide
 * the actual photo picker dependency separately for Android and iOS, because we have used
 * the actual and expected mechanism to define that photo picker functionality
 * separately for Android and iOS.
 * */
expect val targetModule: Module

/** We will pass the Android context as config parameter*/
fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, targetModule)
    }
}