package com.mvvmkotlinbinding.app_common_components.listeners

interface AuthenticationListener {
    fun onTokenReceived(auth_token: String?)
}