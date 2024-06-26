package dev.banger.hootkey.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import dev.banger.hootkey.data.datasource.SettingsManager

class NetworkManager(context: Context, private val settingsManager: SettingsManager) {

    private var _isNetworkAvailable = true

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _isNetworkAvailable = true
        }

        override fun onUnavailable() {
            super.onUnavailable()
            _isNetworkAvailable = false
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _isNetworkAvailable = false
        }
    }

    init {
        val connectivityManager =
            getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
        val currentNetwork = connectivityManager.activeNetwork
        _isNetworkAvailable =
            connectivityManager.getNetworkCapabilities(currentNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    val isNetworkAvailable: Boolean
        get() = _isNetworkAvailable && !settingsManager.isOffline()

}