package com.example.ipgeolocation

import java.util.regex.*;

class IPAddressValidation {
    // Use as companion object (= interface)
    companion object Factory {
        // Function to validate the IPs address.
        fun isValidIPAddress(ip: String): Boolean {
            // Regex for digit from 0 to 255.
            val zeroTo255: String = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])"

            // Regex for a digit from 0 to 255 and
            // followed by a dot, repeat 4 times.
            val fullIP: String =
                zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

            // Compile the ReGex
            val mypattern = Regex(fullIP)

            // If the IP address is empty
            // return false
            if (ip == null) {
                return false;
            }

            // Return if the IP address
            // matched the ReGex
            return (mypattern.matches(ip))
        }
    }
}