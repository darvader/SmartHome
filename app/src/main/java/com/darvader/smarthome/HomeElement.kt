package com.darvader.smarthome

import java.net.InetAddress

interface HomeElement {
    abstract fun refresh(address: InetAddress, received: String)

}
