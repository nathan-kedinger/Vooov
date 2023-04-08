package com.example.vooov.adapters

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.vooov.R
import com.example.vooov.data.model.RecordModel
import com.example.vooov.fragments.RecordListFragment
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordAdapter(
    val context: RecordListFragment,
    val recordList: List<RecordModel>,
    val layoutId: Int,
    val lifeCycleOwner: LifecycleOwner,
    val navController: NavController,
    val userViewModel: UserViewModel,
    var listener: RecordAdapterListener
) : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    interface RecordAdapterListener{
        fun onRecordItemSelected(recordId: Int)
    }
    fun setOnItemSelectedListener(listener: RecordAdapterListener) {
        this.listener = listener
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.record_item_record_title)
        val artistName = view.findViewById<TextView>(R.id.record_item_artist_name)
        val number_of_plays = view.findViewById<TextView>(R.id.record_item_number_of_play)
        val number_of_moons = view.findViewById<TextView>(R.id.record_item_moon_number)
        val voice_style = view.findViewById<TextView>(R.id.record_item_voice_style)
        val kind = view.findViewById<TextView>(R.id.record_item_kind)
        val addButton = view.findViewById<ImageButton>(R.id.record_item_add_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRecord: RecordModel = recordList[position]

        CoroutineScope(Dispatchers.Main).launch {
            userViewModel.fetchOneUser(currentRecord.artist_id)
        }
        userViewModel.user.observe(lifeCycleOwner, Observer { user ->
            if (user != null) {
                holder.artistName.text = user.pseudo
            }
        })
        holder.title.text = currentRecord.title
        holder.number_of_plays.text = currentRecord.number_of_plays.toString()
        holder.number_of_moons.text = currentRecord.number_of_moons.toString()
        holder.voice_style.text = currentRecord.voice_style_id
        holder.kind.text = currentRecord.categories_id

        val currentRecordId = currentRecord.id

            holder.addButton.setOnClickListener {
                val toRecordFromRecycler = Bundle()
                if (currentRecordId != null) {
                    toRecordFromRecycler.putInt("toRecordPageFragment", currentRecordId)
                    Log.i(ContentValues.TAG, currentRecordId.toString())
                }
                navController.navigate(R.id.recordPageFragment, toRecordFromRecycler)
            }

            holder.itemView.setOnClickListener {
                if (currentRecord.id != null) {
                    listener.onRecordItemSelected(currentRecord.id)
                /*val selectedRecordFromRecycler = Bundle()
                    selectedRecordFromRecycler.putInt("selectedRecord", currentRecord.id)
                    navController.navigate(R.id.play_bloc_fragment_container, selectedRecordFromRecycler)*/
                }
            }
    }

    override fun getItemCount(): Int {
        return recordList.size
    }
}