package com.example.retrofit_taller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit_taller.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView
import java.util.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticleAdapter
    private val articleList = mutableListOf<Articles>()
    lateinit var BtnBrasil : Button
    lateinit var BtnFrancia : Button
    lateinit var BtnRusia : Button
    var Pais : String = "fr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchNews.setOnQueryTextListener(this)

        initRecyclerView()

        BtnBrasil = findViewById(R.id.BtnBrasil)
        BtnFrancia = findViewById(R.id.BtnFran)
        BtnRusia = findViewById(R.id.BtnRusia)

        BtnBrasil.setOnClickListener{
            Pais = "br"
            Toast.makeText(this, "Brasil seleccionado", Toast.LENGTH_LONG).show()
        }
        BtnFrancia.setOnClickListener{
            Pais = "fr"
            Toast.makeText(this, "Francia seleccionado", Toast.LENGTH_LONG).show()
        }
        BtnRusia.setOnClickListener{
            Pais = "ru"
            Toast.makeText(this, "Rusia seleccionado", Toast.LENGTH_LONG).show()
        }
        searchNew(Pais,"business")
    }

    private fun initRecyclerView(){

        adapter = ArticleAdapter(articleList)
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter

    }

    private fun searchNew(country: String, category:String){

        val api = Retrofit2()

        CoroutineScope(Dispatchers.IO).launch {

            val call = api.getService()?.getNewsByCategory(country, category, "4b94054dbc6b4b3b9e50d8f62cde4f6c")
            val news: NewsResponse? = call?.body()
            runOnUiThread {
                if (call!!.isSuccessful) {
                    if (news?.status.equals("ok")) {
                        val articles = news?.articles ?: emptyList()
                        articleList.clear()
                        articleList.addAll(articles)
                        adapter.notifyDataSetChanged()
                    } else {
                        showMessage("Error en webservices")
                    }
                } else {
                    showMessage("Error en retrofit")
                }
            }
            hideKeyBoard()
        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            searchNew(Pais,query.toLowerCase(Locale.ROOT))
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}