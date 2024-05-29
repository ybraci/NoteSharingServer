package com.example.data

import kotlinx.serialization.Serializable

@Serializable
class DatoDigitale (
    var id: String,
    var fileBytes: ByteArray,
    var fileName: String
){
}