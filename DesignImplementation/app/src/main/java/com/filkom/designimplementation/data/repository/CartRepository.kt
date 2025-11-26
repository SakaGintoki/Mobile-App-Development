package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.product.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCartCollection() = auth.currentUser?.uid?.let { userId ->
        firestore.collection("users").document(userId).collection("cart")
    }

    fun getCartItemsFlow(): Flow<List<CartItem>> = callbackFlow {
        val collection = getCartCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(CartItem::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addToCart(item: CartItem) {
        val collection = getCartCollection() ?: return

        val query = collection.whereEqualTo("productId", item.productId).get().await()

        if (!query.isEmpty) {
            val document = query.documents.first()
            val existingQty = document.getLong("quantity")?.toInt() ?: 0
            document.reference.update("quantity", existingQty + item.quantity).await()
        } else {
            collection.add(item).await()
        }
    }

    suspend fun deleteItems(cartIds: List<String>) {
        val collection = getCartCollection() ?: return
        val batch = firestore.batch()

        cartIds.forEach { id ->
            val docRef = collection.document(id)
            batch.delete(docRef)
        }

        batch.commit().await()
    }
    suspend fun updateQuantity(cartId: String, newQty: Int) {
        if (newQty < 1) return // Minimal 1
        getCartCollection()?.document(cartId)?.update("quantity", newQty)?.await()
    }

    suspend fun removeItem(cartId: String) {
        getCartCollection()?.document(cartId)?.delete()?.await()
    }

    suspend fun toggleSelection(cartId: String, isSelected: Boolean) {
        getCartCollection()?.document(cartId)?.update("isSelected", isSelected)?.await()
    }

    suspend fun toggleSelectAll(isSelected: Boolean) {
        val collection = getCartCollection() ?: return
        val batch = firestore.batch()

        val snapshot = collection.get().await()
        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "isSelected", isSelected)
        }
        batch.commit().await()
    }
}