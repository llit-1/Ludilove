package com.example.ludilove

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment


class ModalForLogout : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.modal_for_logout, null)

        val infoText = dialogView.findViewById<TextView>(R.id.infoText)
        val buttonClose : Button = dialogView.findViewById(R.id.buttonClose)
        val buttonExit : Button = dialogView.findViewById(R.id.buttonExit)

        buttonClose.setOnClickListener {
            dialog?.hide()
        }

        val db = context?.let { it1 -> DbHelper(it1, null) }
        val login = db?.get_last_user()?.login

        buttonExit.setOnClickListener {
            if (login != null) {
                db.change_last_user(login, 0, id.toString())
            }
            val intent = Intent(context, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        builder.setView(dialogView)

        val dialog = builder.create()
        return dialog
    }
}
