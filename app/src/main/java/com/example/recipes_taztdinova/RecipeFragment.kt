package com.example.recipes_taztdinova

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class RecipeFragment : Fragment() {

    private var recipe: RecipeInfo? = null
    private var ingredientsText = ""
    private var stepsText = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe, container, false)

        val tvTitle: AppCompatTextView = view.findViewById(R.id.tv_title)
        val tvInfo: AppCompatTextView = view.findViewById(R.id.tv_info)
        val tvRecipe: AppCompatTextView = view.findViewById(R.id.tv_recipe)
        val btSave: AppCompatButton = view.findViewById(R.id.bt_save)
        val btBack: AppCompatButton = view.findViewById(R.id.bt_back)

        val recipeId = requireArguments().getInt("recipe_id")
        val apiKey = requireArguments().getString("api_key")
        val gson = Gson()

        tvTitle.text = "Загрузка..."

        val url = "https://api.spoonacular.com/recipes/$recipeId/information?apiKey=$apiKey"
        val queue = Volley.newRequestQueue(requireContext())
        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val info = gson.fromJson(response, RecipeInfo::class.java)
                recipe = info
                Log.d("MyLog", "Открыт рецепт: ${info.title}")

                ingredientsText = ""
                for (ingredient in info.extendedIngredients) {
                    ingredientsText += "- ${ingredient.original}\n"
                }

                stepsText = ""
                if (info.analyzedInstructions.isNotEmpty()) {
                    for (step in info.analyzedInstructions[0].steps) {
                        stepsText += "${step.number}. ${step.step}\n\n"
                    }
                } else {
                    stepsText = "Шаги приготовления не указаны"
                }

                tvTitle.text = info.title
                tvInfo.text = "Время: ${info.readyInMinutes} мин, порций: ${info.servings}"
                tvRecipe.text = "Ингредиенты:\n$ingredientsText\nПриготовление:\n$stepsText"
            },
            { error ->
                Log.d("MyLog", "Volley error: $error")
                tvTitle.text = "Не удалось загрузить рецепт"
            }
        )
        queue.add(request)

        btSave.setOnClickListener {
            if (recipe == null) {
                Toast.makeText(requireContext(), "Рецепт еще не загрузился", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val settings = requireActivity().getSharedPreferences("recipes_app", Context.MODE_PRIVATE)
            val account = gson.fromJson(settings.getString("account", ""), Account::class.java)

            val entity = RecipeEntity(
                userName = account.name,
                title = recipe!!.title,
                minutes = recipe!!.readyInMinutes,
                servings = recipe!!.servings,
                ingredients = ingredientsText,
                steps = stepsText,
                comment = ""
            )
            RecipeDatabase.create(requireContext()).recipeDao().addRecipe(entity)
            Toast.makeText(requireContext(), "Рецепт сохранен", Toast.LENGTH_SHORT).show()
        }

        btBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
