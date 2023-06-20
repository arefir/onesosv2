package com.example.newonesos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newonesos.databinding.ContactBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


class ContactAdapter(val items:ArrayList<Contact>): RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(data: Contact)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding: ContactBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.outerlayout.setOnClickListener {
                itemClickListener?.OnItemClick(items[adapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(view)
    }

    fun removeItem(pos: Int, context: Context) {
        items.removeAt(pos)
        notifyItemRemoved(pos)
        delete(context)
    }

    fun delete(context: Context) {
        val firstOutput = PrintStream(context.openFileOutput("contacts.txt", Context.MODE_PRIVATE))

        firstOutput.println(items[0].name)
        firstOutput.println(items[0].number)
        firstOutput.close()

        val output = PrintStream(context.openFileOutput("contacts.txt", Context.MODE_APPEND))
        for (i in 1 until items.size ) {
            output.println(items[i].name)
            output.println(items[i].number)
        }

        output.close()

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.contactName.text = items[position].name
        holder.binding.contactNo.text = items[position].number
    }

    override fun getItemCount(): Int {
        return items.size
    }

//    fun notifyAdd(position: Int) {
//        notifyItemInserted(holder.get)
//    }

}