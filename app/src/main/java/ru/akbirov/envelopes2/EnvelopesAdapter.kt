package ru.akbirov.envelopes2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.akbirov.envelopes2.databinding.ItemBinding
import ru.akbirov.envelopes2.model.Envelope

interface EnvelopActionListener {
    fun onEnvelopeChange(envelope: Envelope)
    fun onEnvelopeDelete(position: Int)
}

class EnvelopeDiffCallback(
    private val oldList: List<Envelope>,
    private val newList: List<Envelope>,
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEnvelope = oldList[oldItemPosition]
        val newEnvelope = newList[newItemPosition]
        return oldEnvelope.id == newEnvelope.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEnvelope = oldList[oldItemPosition]
        val newEnvelope = newList[newItemPosition]
        return oldEnvelope == newEnvelope
    }

}

class EnvelopesAdapter(
    private val actionListener: EnvelopActionListener
) : RecyclerView.Adapter<EnvelopesAdapter.EnvelopesViewHolder>(), View.OnClickListener {
    class EnvelopesViewHolder(
        val binding: ItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    var envelopes: List<Envelope> = emptyList()
        set(newValue) {
            val diffCallback = EnvelopeDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            field = newValue
            diffResult.dispatchUpdatesTo(this)
//            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvelopesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return EnvelopesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EnvelopesViewHolder, position: Int) {
        val envelope = envelopes[position]

        with(holder.binding) {
            holder.itemView.tag = envelope

            val itemNumber = position + 1

            itemNumberTextView.text = itemNumber.toString()
            itemNameTextView.text = envelope.name
            itemPercentageTextView.text = "${envelope.percentage}%"
            itemSumTextView.text = envelope.piece.toString()
        }

    }

    override fun getItemCount(): Int = envelopes.size

    override fun onClick(v: View) {
        val envelope = v.tag as Envelope
        actionListener.onEnvelopeChange(envelope)
    }

    fun onSwipe(position: Int) {
        actionListener.onEnvelopeDelete(position)
    }
}