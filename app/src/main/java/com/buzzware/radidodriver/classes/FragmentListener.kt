package com.buzzware.radidodriver.classes

interface FragmentListener {
    fun loadFragment(value: String)
    fun loadMapFragment(lng:Double,lat:Double,orderID: String)
}