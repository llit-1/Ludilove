package com.example.ludilove

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class ModalForMap(private val location: Location, context : Context) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        println(location)
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.modal_for_map, null)

        val redirectToMainPage : Button = dialogView.findViewById(R.id.redirectToMainPage)
        val address : TextView = dialogView.findViewById(R.id.address)
        address.setText("${location.address}")

        redirectToMainPage.setOnClickListener {
            val db = context?.let { it1 -> DbHelper(it1, null) }
            db?.deleteAndInsertLocation(location)
            val intent = Intent(requireContext(), ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        //builder.setCancelable(false).setView(dialogView)
        builder.setView(dialogView)
        val dialog = builder.create()

        //dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}
