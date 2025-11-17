package com.filkom.designimplementation.model.data.src

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.filkom.designimplementation.model.data.auth.User
import com.google.common.hash.Hasher
import kotlinx.coroutines.channels.awaitClose

class FirestoreClient {
    private val tag = "FirestoreClient: "

    private val db = FirebaseFirestore.getInstance()
    private val collection = "users"

    fun insertUser(
        user: User
    ): Flow<Boolean>{
        return callbackFlow {
            db.collection(collection)
                .document(user.id)
                .set(user.ToHashMap())
                .addOnSuccessListener {
                    trySend(true)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    trySend(false)
                }
            awaitClose {}
        }
    }

    fun updateUser(
        user: User
    ): Flow<Boolean>{
        return callbackFlow {
            db.collection(collection)
                .document(user.id)
                .set(user.ToHashMap())
                .addOnSuccessListener {
                    println(tag + "Updating user with id: ${user.id}")

                    trySend(true)
                }
                .addOnFailureListener {
                        e -> e.printStackTrace()
                    println(tag + "error updating user: ${e.message}")
                    trySend(false)
                }
            awaitClose {

            }
        }
    }

    fun getUser(
        email: String?,
    ): Flow<User?>{
        return callbackFlow {
            db.collection(collection)
                .get()
                .addOnSuccessListener {
                    result -> var user: User? = null

                    for(document in result) {
                        if (document.data["email"] == email) {
                            user = document.data.toUser()
                            println(tag + "user found: ${user.email}")
                            trySend(user)
                        }
                        if (user == null) {
                            println(tag + "user not found: $email")
                            trySend(null)
                        }
                    }


                }
                .addOnFailureListener {
                        e -> e.printStackTrace()
                    println(tag + "error updating user: ${e.message}")
                    trySend(null)
                }
            awaitClose {

            }
        }
    }

    private fun User.ToHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "name" to name,
            "email" to email,
        )
    }

    private fun Map<String, Any?>.toUser() : User{
        return User(
            id = this["id"] as String,
            name = this["name"] as String,
            email = this["email"] as String
        )
    }
}