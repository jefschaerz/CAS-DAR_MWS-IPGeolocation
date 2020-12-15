package com.example.ipgeolocation

class IpListComment {
    var ipAddress: String? = null
    var comments: String? = null

    constructor(ipAddress: String, comments: String) {
        this.ipAddress = ipAddress
        this.comments = comments
    }
}