package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.product.Product
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")

    suspend fun getAllProducts(): List<Product> {
        return try {
            val snapshot = productsCollection.get().await()
            snapshot.toObjects(Product::class.java).mapIndexed { index, product ->
                product.copy(id = snapshot.documents[index].id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProductById(id: String): Product? {
        return try {
            val document = productsCollection.document(id).get().await()
            document.toObject(Product::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    fun getAllProductsFlow(): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val products = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(products)
        }
        awaitClose { listener.remove() }
    }

    fun getProductDetailFlow(productId: String): Flow<Product?> = callbackFlow {
        val listener = productsCollection.document(productId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val product = snapshot?.toObject(Product::class.java)?.copy(id = snapshot.id)
            trySend(product)
        }
        awaitClose { listener.remove() }
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return try {
            val snapshot = productsCollection.whereEqualTo("category", category).get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addProduct(product: Product): Boolean {
        return try {
            productsCollection.add(product).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun incrementSold(productId: String, quantity: Int) {
        try {
            productsCollection.document(productId)
                .update("sold", FieldValue.increment(quantity.toLong()))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun submitRating(productId: String, userRating: Int) {
        try {
            val docRef = productsCollection.document(productId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                val currentRating = snapshot.getDouble("rating") ?: 0.0
                val currentCount = snapshot.getLong("reviewCount") ?: 0

                val newCount = currentCount + 1

                val totalStars = currentRating * currentCount
                val newAverage = (totalStars + userRating) / newCount

                transaction.update(docRef, "rating", newAverage)

                transaction.update(docRef, "reviewCount", newCount)
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}