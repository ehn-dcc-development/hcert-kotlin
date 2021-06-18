package ehn.techiop.hcert.kotlin.data

//This is actually a platform type
interface CborObject{
    fun toJsonString():String
    fun getVersionString():String?
}