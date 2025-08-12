package com.nutrisportdemo.shared

object Constants {
    const val WEB_CLIENT_ID = "649626202936-nthfj6gr3bovdki5h3rlt6su4e5svidj.apps.googleusercontent.com"
    const val PAYPAL_GET_TOKEN_URL = "https://us-central1-nutrisportdemo-466412.cloudfunctions.net/paypalGetToken"

    const val PAYPAL_CHECKOUT_ENDPOINT = "https://api-m.sandbox.paypal.com/v2/checkout/orders"

    const val RETURN_URL = "com.federico.nutrisportdemo://paypalpay?success=true"
    const val CANCEL_URL = "com.federico.nutrisportdemo://paypalpay?cancel=true"
    const val MAX_QUANTITY = 10
    const val MIN_QUANTITY = 1
}