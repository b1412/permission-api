package com.github.b1412.cannon.controller

class DelegatingCollection3<T>(
        private val innerList: MutableCollection<T> = HashSet()
) : MutableCollection<T> by innerList {
    private var addedSum = 0

    override fun add(element: T): Boolean {
        addedSum++
        return innerList.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        addedSum += elements.size
        return innerList.addAll(elements)
    }
}

fun main() {
    val r = DelegatingCollection3<Int>()
    r.clear()
}