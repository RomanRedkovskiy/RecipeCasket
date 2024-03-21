package com.example.recipecasket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipecasket.R
import com.example.recipecasket.model.Recipe
import com.example.recipecasket.model.UserAdditionalData
import com.example.recipecasket.util.RecipeAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AuthorisedActivity : AppCompatActivity() {

    private lateinit var settingsButton: ImageView
    private lateinit var recyclerView: RecyclerView

    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var starList: List<String>

    private lateinit var dbRecipeRef: DatabaseReference
    private lateinit var dbUserRef: DatabaseReference

    private lateinit var starFilterOff: ImageView
    private lateinit var starFilterOn: ImageView

    override fun onResume() {
        super.onResume()
        updateFilteredRecipeData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorised)

        settingsButton = findViewById(R.id.settings)

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(
                this@AuthorisedActivity,
                SettingsActivity::class.java
            )
            settingsIntent.putExtra("user_id", intent.getStringExtra("user_id"))
            startActivity(settingsIntent)
        }

        starFilterOff = findViewById(R.id.starFilterOff)
        starFilterOn = findViewById(R.id.starFilterOn)

        starFilterOff.isVisible = true
        starFilterOn.isVisible = false

        starFilterOff.setOnClickListener {
            starFilterOff.isVisible = false
            starFilterOn.isVisible = true
            updateFilteredRecipeData()
        }

        starFilterOn.setOnClickListener {
            starFilterOff.isVisible = true
            starFilterOn.isVisible = false
            updateRecipeData()
        }

        recyclerView = findViewById(R.id.rvRecipe)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dbUserRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRecipeRef = FirebaseDatabase.getInstance().getReference("Recipes")

        recipeList = arrayListOf()

        updateRecipeData()
    }

    private fun updateRecipeData() {

        dbRecipeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear()
                if (snapshot.exists()) {
                    if(starFilterOff.isVisible){
                        for (recipeSnapshot in snapshot.children) {
                            val recipeData = recipeSnapshot.getValue(Recipe::class.java)
                            recipeList.add(recipeData!!)
                        }
                    }
                    else if(starFilterOn.isVisible){
                        for (recipeSnapshot in snapshot.children) {
                            val recipeData = recipeSnapshot.getValue(Recipe::class.java)
                            if(starList.contains(recipeData!!.recipeId)){
                                recipeList.add(recipeData)
                            }
                        }
                    }
                    val mAdapter = RecipeAdapter(recipeList)
                    recyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : RecipeAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {

                            val descriptionIntent = Intent(
                                this@AuthorisedActivity,
                                RecipeDetailsActivity::class.java
                            )

                            //put extras
                            descriptionIntent.putExtra("recipe_id", recipeList[position].recipeId)
                            descriptionIntent.putExtra("name", recipeList[position].name)
                            descriptionIntent.putExtra("cuisine", recipeList[position].cuisine)
                            descriptionIntent.putExtra(
                                "ingredients",
                                recipeList[position].ingredients
                            )
                            descriptionIntent.putExtra("recipe", recipeList[position].recipe)
                            descriptionIntent.putExtra(
                                "cooking_time",
                                recipeList[position].cookingTime
                            )
                            descriptionIntent.putExtra("user_id", intent.getStringExtra("user_id"))

                            startActivity(descriptionIntent)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    /*    private fun processStarChange(recipeId: String) {
            val mutableList: MutableList<String> = mutableListOf()
            userData.starredRecipes?.let { mutableList.addAll(it) }

            if (mutableList.contains(recipeId)) {
                mutableList.remove(recipeId)
            } else {
                mutableList.add(recipeId)
            }
            userData.starredRecipes = mutableList

            dbRef.child(userData.userId!!).setValue(userData)
        }*/

    private fun updateFilteredRecipeData() {
        dbUserRef.child(intent.getStringExtra("user_id").toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle the retrieved data here
                    val userData = dataSnapshot.getValue(UserAdditionalData::class.java)
                    starList = if (dataSnapshot.exists() &&
                        userData?.starredRecipes != null) {
                        userData.starredRecipes!!
                    } else {
                        listOf()
                    }
                    updateRecipeData()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during the data retrieval
                }
            })
    }
}