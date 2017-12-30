package com.stasbar.taxledger

import com.stasbar.taxledger.exceptions.ApiNotSetException
import com.stasbar.taxledger.exceptions.CredentialsException
import com.stasbar.taxledger.exceptions.TooManyCredentialsException
import com.stasbar.taxledger.exchanges.abucoins.AbuApi
import com.stasbar.taxledger.exchanges.bitbay.BitBayApi
import com.stasbar.taxledger.exchanges.bitmarket.BitmarketApi
import org.fusesource.jansi.Ansi
import java.io.PrintWriter

object BitBay : Exchange<BitBayApi>("BitBay", "bb", listOf("publicKey", "privateKey"), Ansi.Color.BLUE) {


    private const val PUBLIC_KEY_LENGTH = 36
    private const val PRIVATE_KEY_LENGTH = 36

    var publicKey: String = ""
    var privateKey: String = ""

    @Throws(CredentialsException::class, TooManyCredentialsException::class)
    override fun addCredential(credential: String) {
        if (publicKey.isBlank()) {
            if (credential.length != PUBLIC_KEY_LENGTH)
                throw CredentialsException("publicKey", name, PUBLIC_KEY_LENGTH)
            else publicKey = credential
        } else if (privateKey.isBlank()) {
            if (credential.length != PRIVATE_KEY_LENGTH)
                throw CredentialsException("privateKey", name, PRIVATE_KEY_LENGTH)
            else privateKey = credential
        } else throw TooManyCredentialsException(name)

    }

    private var api : BitBayApi? = null

    @Throws(ApiNotSetException::class)
    override fun getApi() : BitBayApi{
        return if(api == null){
            if (isSet().not())
                throw ApiNotSetException(name)
            else {
                api = BitBayApi(publicKey, privateKey)
                api!!
            }

        } else api!!
    }

    override fun isSet() = publicKey.isNotBlank() && privateKey.isNotBlank()

    override fun printCredentials(writer: PrintWriter) {
        writer.println("bitbay")
        writer.println(publicKey)
        writer.println(privateKey)
        writer.flush()
    }

}

object Abucoins : Exchange<AbuApi>("Abucoins", "abu", listOf("key", "secret", "passphrase"), Ansi.Color.GREEN) {
    private const val KEY_LENGTH = 41
    private const val SECRET_LENGTH = 64
    private const val PASSPHRASE_LENGTH = 9

    var key: String = ""
    var secret: String = ""
    var passphrase: String = ""


    @Throws(CredentialsException::class, TooManyCredentialsException::class)
    override fun addCredential(credential: String) {
        if (key.isBlank()) {
            if (credential.length != KEY_LENGTH)
                throw CredentialsException("key", name, KEY_LENGTH)
            else key = credential
        } else if (secret.isBlank()) {
            if (credential.length != SECRET_LENGTH)
                throw CredentialsException("secret",name, SECRET_LENGTH)
            else secret = credential
        } else if (passphrase.isBlank()) {
            if (credential.length != PASSPHRASE_LENGTH)
                throw CredentialsException("passphrase", name, PASSPHRASE_LENGTH)
            else passphrase = credential
        } else throw TooManyCredentialsException(name)

    }

    private var api : AbuApi? = null

    @Throws(ApiNotSetException::class)
    override fun getApi(): AbuApi {
        return if(api == null){
            if (isSet().not())
                throw ApiNotSetException(name)
            else {
                api = AbuApi(key, secret, passphrase)
                api!!
            }

        } else api!!
    }

    override fun isSet() = key.isNotBlank() && secret.isNotBlank() && passphrase.isNotBlank()

    override fun printCredentials(writer: PrintWriter) {
        writer.println("abu")
        writer.println(key)
        writer.println(secret)
        writer.println(passphrase)
        writer.flush()
    }

}

object BitMarket : Exchange<BitmarketApi>("Bitmarket", "bm", listOf("publicKey", "privateKey"), Ansi.Color.BLUE) {


    private const val PUBLIC_KEY_LENGTH = 32
    private const val PRIVATE_KEY_LENGTH = 32

    var publicKey: String = ""
    var privateKey: String = ""

    @Throws(CredentialsException::class, TooManyCredentialsException::class)
    override fun addCredential(credential: String) {
        if (publicKey.isBlank()) {
            if (credential.length != PUBLIC_KEY_LENGTH)
                throw CredentialsException("publicKey", name, PUBLIC_KEY_LENGTH)
            else publicKey = credential
        } else if (privateKey.isBlank()) {
            if (credential.length != PRIVATE_KEY_LENGTH)
                throw CredentialsException("privateKey", name, PRIVATE_KEY_LENGTH)
            else privateKey = credential
        } else throw TooManyCredentialsException(name)

    }

    private var api : BitmarketApi? = null

    @Throws(ApiNotSetException::class)
    override fun getApi() : BitmarketApi{
        return if(api == null){
            if (isSet().not())
                throw ApiNotSetException(name)
            else {
                api = BitmarketApi(publicKey, privateKey)
                api!!
            }

        } else api!!
    }

    override fun isSet() = publicKey.isNotBlank() && privateKey.isNotBlank()

    override fun printCredentials(writer: PrintWriter) {
        writer.println("bitmarket")
        writer.println(publicKey)
        writer.println(privateKey)
        writer.flush()
    }

}

abstract class Exchange<out ApiType : ExchangeApi>(val name: String, val shortcut: String, val credentialsSteps: List<String>, val color: Ansi.Color) {
    @Throws(ApiNotSetException::class)
    abstract fun getApi(): ApiType

    @Throws(CredentialsException::class, TooManyCredentialsException::class)
    abstract fun addCredential(credential: String)

    fun isNameOf(candidate: String) = candidate.toLowerCase() in arrayOf(name.toLowerCase(), shortcut.toLowerCase())
    abstract fun isSet(): Boolean
    abstract fun printCredentials(writer: PrintWriter)
}
