package org.deafsapps.mobile.kmpflagship

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform