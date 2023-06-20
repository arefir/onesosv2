package com.example.newonesos

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.*
import com.example.newonesos.databinding.ContactListBinding
import java.io.FileNotFoundException
import java.io.Serializable
import java.util.Scanner

class ContactListActivity: ComponentActivity() {
    lateinit var binding: ContactListBinding
    var contactList:ArrayList<Contact> = ArrayList()
    lateinit var adapter: ContactAdapter


    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val intent = result.data
            @Suppress("DEPRECATION")
            val contact = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent?.getSerializableExtra("contact", Contact::class.java)
            else
                intent?.getSerializableExtra("contact") as Contact
            Toast.makeText(this, contact?.name + " added", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addButton.setOnClickListener { launchAddActivity() }
        binding.backButton.setOnClickListener { finish() }

        initData()
        initRecyclerView()
    }

    fun refreshList() {
        contactList.clear()
        initData()
        initRecyclerView()
    }

    fun launchAddActivity() {
        val intent = Intent(this, AddContactActivity::class.java)
        launcher.launch(intent)
    }

    private fun readFileScan(scan:Scanner) {
        while(scan.hasNextLine()){
            val name = scan.nextLine()
            val number = scan.nextLine()
            contactList.add(Contact(name, number))
        }
    }

    fun initData(){
        try {
            val scan2 = Scanner(openFileInput("contacts.txt"))
            readFileScan(scan2)
        } catch (e: FileNotFoundException) {

        }
//        val scan = Scanner(resources.openRawResource(R.raw.contacts))
//        readFileScan(scan)
    }

    fun initRecyclerView(){

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
            false)
//        if (contactList != null)
        adapter = ContactAdapter(contactList)
//        adapter.itemClickListener = object : MyDataAdapter.OnItemClickListener{
//            override fun OnItemClick(data: MyData) {
//                if(isTtsReady)
//                    tts.speak(data.word, TextToSpeech.QUEUE_ADD, null, null)
//                Toast.makeText(this@MainActivity, data.meaning, Toast.LENGTH_SHORT).show()
//            }
//        }
        binding.recyclerView.adapter = adapter
        val simpleCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.adapterPosition, this@ContactListActivity)
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }


}