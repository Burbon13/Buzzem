package data

fun encodeString(string:String) : String {
    return string.replace('.',',')
}

fun decodeString(string:String) : String {
    return string.replace(',','.')
}