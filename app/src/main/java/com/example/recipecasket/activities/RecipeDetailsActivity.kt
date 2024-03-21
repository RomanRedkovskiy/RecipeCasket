package com.example.recipecasket.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.recipecasket.R
import com.example.recipecasket.model.ImageItem
import com.example.recipecasket.model.UserAdditionalData
import com.example.recipecasket.util.ImageAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvCuisine: TextView
    private lateinit var tvIngredients: TextView
    private lateinit var tvRecipe: TextView
    private lateinit var tvCookingTime: TextView

    private lateinit var imgNotStarred: ImageView
    private lateinit var imgStarred: ImageView

    private lateinit var dbRef: DatabaseReference
    private lateinit var userData: UserAdditionalData

    private lateinit var viewpager2: ViewPager2
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback

    private val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(8, 0, 8, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        setupStarButton()

        tvName = findViewById(R.id.recipe_name)
        tvCuisine = findViewById(R.id.recipe_cuisine)
        tvIngredients = findViewById(R.id.recipe_ingredients)
        tvRecipe = findViewById(R.id.recipe_description)
        tvCookingTime = findViewById(R.id.recipe_time)

        tvName.text = intent.getStringExtra("name")
        tvCuisine.text = intent.getStringExtra("cuisine")
        tvIngredients.text = intent.getStringExtra("ingredients")
        tvRecipe.text = intent.getStringExtra("recipe")

        val cookingTime = intent.getIntExtra("cooking_time", 0)
        tvCookingTime.text = resources.getString(R.string.cooking_time_placeholder, cookingTime)
        val recipeId = intent.getStringExtra("recipe_id")!!

        imgStarred = findViewById(R.id.starred)
        imgNotStarred = findViewById(R.id.not_starred)

        imgStarred.isVisible = false
        imgNotStarred.isVisible = true

        imgStarred.setOnClickListener {
            imgStarred.isVisible = false
            imgNotStarred.isVisible = true
            processStarChange(recipeId)
        }

        imgNotStarred.setOnClickListener {
            imgStarred.isVisible = true
            imgNotStarred.isVisible = false
            processStarChange(recipeId)
        }

        viewpager2 = findViewById(R.id.viewpager2)

        val storageReference = FirebaseStorage.getInstance().reference
            .child("Image/$recipeId")
        val imageList = mutableListOf<ImageItem>()

        storageReference.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    item.downloadUrl.addOnSuccessListener { uri ->
                        imageList.add(ImageItem(UUID.randomUUID().toString(), uri.toString()))

                        // Check if all images have been loaded
                        if (imageList.size == listResult.items.size) {
                            imageList.sortBy {it.url}
                            setupViewPager(imageList)
                        }
                    }
                }
            }
    }

    private fun setupViewPager(imageList: List<ImageItem>) {
        val imageAdapter = ImageAdapter()
        viewpager2.adapter = imageAdapter
        imageAdapter.submitList(imageList)

        val slideDotLL = findViewById<LinearLayout>(R.id.slider_container)
        val dotsImage = Array(imageList.size) { ImageView(this) }

        dotsImage.forEach {
            it.setImageResource(
                R.drawable.non_active_dot
            )
            slideDotLL.addView(it, params)
        }

        // default first dot selected
        dotsImage[0].setImageResource(R.drawable.active_dot)

        pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dotsImage.mapIndexed { index, imageView ->
                    if (position == index) {
                        imageView.setImageResource(
                            R.drawable.active_dot
                        )
                    } else {
                        imageView.setImageResource(R.drawable.non_active_dot)
                    }
                }
                super.onPageSelected(position)
            }
        }
        viewpager2.registerOnPageChangeCallback(pageChangeListener)
    }

    private fun setupStarButton() {
        dbRef.child(intent.getStringExtra("user_id").toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle the retrieved data here
                    if (dataSnapshot.exists()) {
                        userData = dataSnapshot.getValue(UserAdditionalData::class.java)!!
                        if (userData.starredRecipes?.contains(intent.getStringExtra("recipe_id")!!) == true) {
                            imgStarred.isVisible = true
                            imgNotStarred.isVisible = false
                        }
                    } else {
                        // Handle the case when the key does not exist in the database
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during the data retrieval
                }
            })
    }

    private fun processStarChange(recipeId: String) {
        val mutableList: MutableList<String> = mutableListOf()
        userData.starredRecipes?.let { mutableList.addAll(it) }

        if (mutableList.contains(recipeId)) {
            mutableList.remove(recipeId)
        } else {
            mutableList.add(recipeId)
        }
        userData.starredRecipes = mutableList

        dbRef.child(userData.userId!!).setValue(userData)
    }
}