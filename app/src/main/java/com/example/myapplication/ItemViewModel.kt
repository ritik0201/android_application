package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ItemViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(
        listOf(
            Item(1, "Buy Groceries", "Milk, eggs, bread, and fruits"),
            Item(2, "Kotlin Learning", "Study Jetpack Compose and StateFlow")
        )
    )
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private var nextId = 3

    fun addItem(title: String, description: String) {
        if (title.isBlank()) return
        val newItem = Item(id = nextId++, title = title, description = description)
        _items.update { currentList -> currentList + newItem }
    }

    fun updateItem(id: Int, title: String, description: String) {
        if (title.isBlank()) return
        _items.update { currentList ->
            currentList.map { item ->
                if (item.id == id) item.copy(title = title, description = description) else item
            }
        }
    }

    fun deleteItem(id: Int) {
        _items.update { currentList ->
            currentList.filter { it.id != id }
        }
    }
}
