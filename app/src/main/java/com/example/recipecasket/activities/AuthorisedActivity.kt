package com.example.recipecasket.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipecasket.R
import com.example.recipecasket.model.Recipe
import com.example.recipecasket.util.RecipeAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthorisedActivity : AppCompatActivity() {

    private lateinit var empRecyclerView: RecyclerView
    private lateinit var empList: ArrayList<Recipe>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorised)

        empRecyclerView = findViewById(R.id.rvEmp)
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)

        empList = arrayListOf()

        getEmployeesData()

    }

    private fun getEmployeesData() {

        dbRef = FirebaseDatabase.getInstance().getReference("Recipes")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()) {
                    for (empSnap in snapshot.children) {
                        val empData = empSnap.getValue(Recipe::class.java)
                        empList.add(empData!!)
                    }
                    val mAdapter = RecipeAdapter(empList)
                    empRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : RecipeAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(
                                this@AuthorisedActivity,
                                RecipeDetailsActivity::class.java
                            )

                            //put extras
                            intent.putExtra("recipeId", empList[position].recipeId)
                            intent.putExtra("name", empList[position].name)
                            intent.putExtra("cuisine", empList[position].cuisine)
                            intent.putExtra("ingredients", empList[position].ingredients)
                            intent.putExtra("recipe", empList[position].recipe)
                            intent.putExtra("cookingTime", empList[position].cookingTime)

                            startActivity(intent)
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}