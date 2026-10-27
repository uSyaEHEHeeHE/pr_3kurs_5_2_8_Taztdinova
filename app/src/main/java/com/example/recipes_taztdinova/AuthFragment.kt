package com.example.recipes_taztdinova

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.gson.Gson

class AuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        val etName: AppCompatEditText = view.findViewById(R.id.et_name)
        val etPass: AppCompatEditText = view.findViewById(R.id.et_pass)
        val btEnter: AppCompatButton = view.findViewById(R.id.bt_enter)

        val settings = requireActivity().getSharedPreferences("recipes_app", Context.MODE_PRIVATE)
        val gson = Gson()

        btEnter.setOnClickListener {
            val name = etName.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните имя и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedJson = settings.getString("account", null)
            if (savedJson != null) {
                val saved = gson.fromJson(savedJson, Account::class.java)
                if (saved.name == name && saved.password != pass) {
                    Toast.makeText(requireContext(), "Неверный пароль", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            settings.edit().putString("account", gson.toJson(Account(name, pass))).apply()

            parentFragmentManager.beginTransaction()
                .replace(R.id.container, SearchFragment())
                .commit()
        }

        return view
    }
}
