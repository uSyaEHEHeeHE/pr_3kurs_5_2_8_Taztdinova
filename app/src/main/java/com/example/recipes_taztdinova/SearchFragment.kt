package com.example.recipes_taztdinova

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class SearchFragment : Fragment() {

    private val apiKey = "ee64563f74b34a729b79cc43bed95bc1"

    private lateinit var lvResults: ListView
    private var recipes: List<Recipe> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val etQuery: AppCompatEditText = view.findViewById(R.id.et_query)
        val btFind: AppCompatButton = view.findViewById(R.id.bt_find)
        val btMy: AppCompatButton = view.findViewById(R.id.bt_my)
        lvResults = view.findViewById(R.id.lv_results)

        val settings = requireActivity().getSharedPreferences("recipes_app", Context.MODE_PRIVATE)
        val gson = Gson()

        val lastJson = settings.getString("last_search", null)
        if (lastJson != null) {
            recipes = gson.fromJson(lastJson, SearchResult::class.java).results
            showRecipes()
        }

        btFind.setOnClickListener {
            val query = etQuery.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Введите название блюда", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "https://api.spoonacular.com/recipes/complexSearch?query=" + Uri.encode(query) +
                    "&number=10&addRecipeInformation=true&apiKey=" + apiKey

            val queue = Volley.newRequestQueue(requireContext())
            val request = StringRequest(
                Request.Method.GET,
                url,
                { response ->
                    val result = gson.fromJson(response, SearchResult::class.java)
                    settings.edit().putString("last_search", gson.toJson(result)).apply()
                    Log.d("MyLog", "Найдено рецептов: ${result.results.size}")
                    recipes = result.results
                    showRecipes()
                    if (recipes.isEmpty()) {
                        Toast.makeText(requireContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Log.d("MyLog", "Volley error: $error")
                    Toast.makeText(requireContext(), "Ошибка запроса", Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(request)
        }

        lvResults.setOnItemClickListener { _, _, position, _ ->
            val fragment = RecipeFragment()
            val args = Bundle()
            args.putInt("recipe_id", recipes[position].id)
            args.putString("api_key", apiKey)
            fragment.arguments = args

            parentFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        btMy.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, FavoritesFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun showRecipes() {
        val items = mutableListOf<String>()
        for (r in recipes) {
            items.add("${r.title}\n${r.readyInMinutes} мин, порций: ${r.servings}")
        }
        lvResults.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
    }
}
