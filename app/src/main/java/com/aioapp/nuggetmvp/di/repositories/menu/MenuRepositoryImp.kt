package com.aioapp.nuggetmvp.di.repositories.menu

import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MenuRepositoryImp @Inject constructor() : MenuRepository {
    override fun allMenuData(): Flow<Result<List<Food>>> {
        return flow {
            val foodList = getFoodList()
            val drinkList = getDrinksList()
            val allMenuList = foodList + drinkList
            emit(Result.Success(allMenuList))
        }
    }

    private fun getFoodList(): List<Food> {
        return listOf(
            Food(R.drawable.caesar, R.drawable.ceasar_full_img, "Caesar Salad", "12", "Caesar"),
            Food(R.drawable.wedge, R.drawable.wedge_full_img, "Wedge Salad", "14", "Wedge"),
            Food(R.drawable.caprese, R.drawable.caprese_full_img, "Caprese Salad", "14", "Caprese"),
            Food(R.drawable.pork, R.drawable.pork_full_img, "Pork Burger", "18", "Pork"),
            Food(R.drawable.fish, R.drawable.fish_full_img, "Fish Burger", "18", "Fish"),
            Food(R.drawable.beef, R.drawable.beef_full_img, "Beef Burger", "18", "Beef"),
            Food(R.drawable.salmon, R.drawable.salmon_full_img, "Salmon", "28", "Salmon"),
            Food(R.drawable.steak, R.drawable.steak_full_img, "Steak", "35", "Steak"),
            Food(
                R.drawable.chicken,
                R.drawable.chicken_full_img,
                "Chicken",
                "25",
                displayName = "Chicken"
            )
        )
    }

    private fun getDrinksList(): List<Food> {
        return listOf(
            Food(
                R.drawable.pina_colada,
                R.drawable.pina_colada_full_img,
                "Pina Colada",
                "24",
                displayName = "Pina Colada"
            ),
            Food(R.drawable.mojito, R.drawable.mojito_full_img, "Mojito", "24", "Mojito"),
            Food(
                R.drawable.margarita, R.drawable.margaritta_full_img, "Margarita", "24", "Margarita"
            ),
            Food(
                R.drawable.mile_high, R.drawable.mile_high_full_img, "Mile High", "14", "Mile High"
            ),
            Food(R.drawable.coke, R.drawable.coke_full_img, "Coke", "14", "Coke"),
            Food(R.drawable.maverick, R.drawable.mavrick_full_img, "Maverick", "14", "Maverick"),
            Food(R.drawable.wingman, R.drawable.wingman_full_img, "Wingman", "14", "Wingman"),
            Food(R.drawable.martini, R.drawable.martini_full_img, "Martini", "14", "Martini"),
            Food(R.drawable.iceman, R.drawable.iceman_full_img, "Iceman", "14", "Iceman")
        )
    }

}
