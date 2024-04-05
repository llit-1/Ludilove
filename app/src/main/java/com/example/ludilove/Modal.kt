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


class Modal : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.modal, null)

        val successIcon = dialogView.findViewById<ImageView>(R.id.successIcon)
        val successText = dialogView.findViewById<TextView>(R.id.successText)
        val redirectToOrdersActivity : Button = dialogView.findViewById(R.id.redirectToOrdersActivity)

        redirectToOrdersActivity.setOnClickListener {
            val intent = Intent(requireContext(), OrdersActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Настройка иконки галочки
        val state = arguments?.getBoolean("State") ?: false
        if(state) {
            successIcon.setImageResource(R.drawable.green_check)
            val color = ContextCompat.getColor(requireContext(), R.color.green)
            successIcon.setColorFilter(color)

            successText.text = "Заказ успешно оформлен"
        } else {
            successIcon.setImageResource(R.drawable.bad_check)
            val color = ContextCompat.getColor(requireContext(), R.color.red)
            successIcon.setColorFilter(color)

            successText.text = "Произошла ошибка при выполнении запроса"
        }
        builder.setCancelable(false).setView(dialogView)

        val dialog = builder.create()

        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}
