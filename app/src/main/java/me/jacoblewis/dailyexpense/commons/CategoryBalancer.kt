package me.jacoblewis.dailyexpense.commons

import me.jacoblewis.dailyexpense.data.models.Category

object CategoryBalancer {
    private val BIAS = 0.001f

    /**
     * Return true iff categories were rebalanced
     */
    fun balanceCategories(categories: List<Category>, pinned: List<Int> = listOf()): Boolean {
        if (!isBalanced(categories)) {
            balanceNow(pinned, categories)
            return true
        }
        return false
    }

    fun mapToExpo(original: Float): Float = Math.sqrt(original.toDouble()).toFloat()

    fun mapFromExpo(expo: Float): Float = expo * expo

    private fun balanceNow(pinned: List<Int>, categories: List<Category>) {
        val pinnedOffset = 1f - pinned.map { categories[it].budget }.sum()
        assert(pinnedOffset in 0.0..1.0, lazyMessage = { "Invalid Pinned Category" })
        val sum = categories.mapIndexed { i, cat ->
            if (pinned.contains(i)) 0f else cat.budget
        }.sum()
        if (sum != 0f) {
            categories.forEachIndexed { i, cat ->
                if (!pinned.contains(i)) {
                    cat.budget *= pinnedOffset / sum
                }
            }
        } else {
            categories.forEachIndexed { i, cat ->
                if (!pinned.contains(i)) {
                    cat.budget = pinnedOffset / categories.size
                }
            }
        }
    }

    private fun isBalanced(categories: List<Category>): Boolean {
        return Math.abs(categories.sumByDouble { it.budget.toDouble() } - 1f) < BIAS
    }


}