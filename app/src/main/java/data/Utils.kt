package data

import android.util.Log
import com.google.firebase.database.*
import java.util.*

fun encodeString(string:String) : String {
    return string.replace('.',',')
}

fun decodeString(string:String) : String {
    return string.replace(',','.')
}

//fun getEmailFromUid(uid:String,dbRef:DatabaseReference): String {
//    var toReturn:String = "pulamea"
//    val db = dbRef.child("users").child(uid)
//    db.addListenerForSingleValueEvent(object : ValueEventListener {
//        override fun onCancelled(p0: DatabaseError) {
//            return
//        }
//
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            toReturn = dataSnapshot.child("email").value.toString()
//            Log.d("MainActivity", toReturn)
//        }
//    })
//}