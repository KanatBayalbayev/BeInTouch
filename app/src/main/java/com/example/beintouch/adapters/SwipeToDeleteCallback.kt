//package com.example.beintouch.adapters
//
//import android.app.AlertDialog
//import android.content.Context
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.RecyclerView
//
//class SwipeToDeleteCallback(
//    private val adapter: ChatAdapter,
//    private val context: Context
//): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
//    override fun onMove(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ): Boolean {
//        return false
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        val position = viewHolder.adapterPosition
//        showDeleteConfirmationDialog(context) {
//            adapter.removeItem(position)
//        }
//
//    }
//
//    fun showDeleteConfirmationDialog(context: Context, onDeleteConfirmed: () -> Unit) {
//        AlertDialog.Builder(context)
//            .setTitle("Удаление элемента")
//            .setMessage("Вы уверены, что хотите удалить этот элемент?")
//            .setPositiveButton("Удалить") { _, _ ->
//                onDeleteConfirmed()
//            }
//            .setNegativeButton("Отмена") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//}