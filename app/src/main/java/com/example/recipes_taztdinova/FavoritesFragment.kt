package com.example.recipes_taztdinova

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson

class FavoritesFragment : Fragment() {

    private lateinit var lvFavorites: ListView
    private lateinit var dao: RecipeDao
    private lateinit var userName: String
    private var saved: List<RecipeEntity> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        lvFavorites = view.findViewById(R.id.lv_favorites)
        val btBack: AppCompatButton = view.findViewById(R.id.bt_back)

        val settings = requireActivity().getSharedPreferences("recipes_app", Context.MODE_PRIVATE)
        userName = Gson().fromJson(settings.getString("account", ""), Account::class.java).name
        dao = RecipeDatabase.create(requireContext()).recipeDao()

        loadList()

        lvFavorites.setOnItemClickListener { _, _, position, _ ->
            val recipe = saved[position]
            val dialogView = layoutInflater.inflate(R.layout.dialog_recipe, null)
            val tvRecipe: AppCompatTextView = dialogView.findViewById(R.id.tv_dialog_recipe)
            val etComment: AppCompatEditText = dialogView.findViewById(R.id.et_comment)

            tvRecipe.text = "Время: ${recipe.minutes} мин, порций: ${recipe.servings}\n\n" +
                    "Ингредиенты:\n${recipe.ingredients}\nПриготовление:\n${recipe.steps}"
            etComment.setText(recipe.comment)

            AlertDialog.Builder(requireContext())
                .setTitle(recipe.title)
                .setView(dialogView)
                .setPositiveButton("Сохранить") { _, _ ->
                    dao.updateRecipe(recipe.copy(comment = etComment.text.toString()))
                    Toast.makeText(requireContext(), "Изменено", Toast.LENGTH_SHORT).show()
                    loadList()
                }
                .setNegativeButton("Удалить") { _, _ ->
                    dao.deleteRecipe(recipe)
                    Toast.makeText(requireContext(), "Удалено", Toast.LENGTH_SHORT).show()
                    loadList()
                }
                .setNeutralButton("Отмена", null)
                .show()
        }

        btBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadList() {
        saved = dao.getRecipes(userName)
        val items = mutableListOf<String>()
        for (r in saved) {
            var text = "${r.title} (${r.minutes} мин)"
            if (r.comment.isNotEmpty()) {
                text += "\nКомментарий: ${r.comment}"
            }
            items.add(text)
        }
        lvFavorites.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
    }
}
