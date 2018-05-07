@file:JvmName("MainKt")

package at.ac.tuwien.big

import at.ac.tuwien.big.rest.WebServiceController

fun main(args: Array<String>) {
    MessageController.start()
    WebServiceController.start()
    readLine()
}
