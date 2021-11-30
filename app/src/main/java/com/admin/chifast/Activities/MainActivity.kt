package com.admin.chifast.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.admin.chifast.Menu
import com.admin.chifast.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_content.view.*


class MainActivity : AppCompatActivity() {

    private val database = Firebase.database
    private lateinit var messagesListener: ValueEventListener
    private val listMenus:MutableList<Menu> = ArrayList()
    val myRef = database.getReference("menu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newFloatingActionButton.setOnClickListener { v ->
            val intent = Intent(this, AddActivity::class.java)
            v.context.startActivity(intent)
        }

        val bundle = intent.extras
        val email = bundle?.getString("email")

        NameUser.text = email

        listMenus.clear()
        setupRecyclerView(menuRecyclerView)

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.commit()
        setup()

    }

    private fun setup() {
        title = "Inicio"


        btnLogOut.setOnClickListener {

            //BORRADO DE DATOS
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.commit()

            FirebaseAuth.getInstance().signOut()

            val loginIntent = Intent(this, AuthActivity::class.java)
            startActivity(loginIntent)
        }

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        messagesListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listMenus.clear()
                dataSnapshot.children.forEach { child ->
                    val menu: Menu? =
                            Menu(child.child("name").getValue<String>(),
                                    child.child("precio").getValue<String>(),
                                    child.child("description").getValue<String>(),
                                    child.child("url").getValue<String>(),
                                    child.key)
                    menu?.let { listMenus.add(it) }
                }
                recyclerView.adapter = MenuViewAdapter(listMenus)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)

        deleteSwipe(recyclerView)
    }

    class MenuViewAdapter(private val values: List<Menu>) :
        RecyclerView.Adapter<MenuViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.menu_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val menu = values[position]
            holder.mNameTextView.text = menu.name
            holder.mPrecioTextView.text = menu.precio
            holder.mPosterImgeView?.let {
                Glide.with(holder.itemView.context)
                    .load(menu.url)
                    .into(it)
            }

            holder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, MenuDetalleActivity::class.java).apply {
                    putExtra("key", menu.key)
                }
                v.context.startActivity(intent)
            }

            holder.itemView.setOnLongClickListener{ v ->
                val intent = Intent(v.context, EditActivity::class.java).apply {
                    putExtra("key", menu.key)
                }
                v.context.startActivity(intent)
                true
            }

        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mNameTextView: TextView = view.nameTextView
            val mPrecioTextView: TextView = view.precioTextView
            val mPosterImgeView: ImageView? = view.posterImgeView
        }
    }

    private fun deleteSwipe(recyclerView: RecyclerView){
        val touchHelperCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listMenus.get(viewHolder.adapterPosition).key?.let { myRef.child(it).setValue(null) }
                listMenus.removeAt(viewHolder.adapterPosition)
                recyclerView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}

