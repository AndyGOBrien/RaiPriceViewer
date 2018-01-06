package com.llamalabb.com.raipriceviewer

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView


/**
 * Created by andy on 1/5/18.
 */
class SelectGridItemDialogFragment : DialogFragment() {

    private lateinit var gridView: RecyclerView
    private lateinit var callBack: ItemSelectCallBack

    companion object {
        fun newInstance(title: String, list: Array<String>) : SelectGridItemDialogFragment {
            val frag = SelectGridItemDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putStringArray("list", list)
            frag.arguments = args
            return frag
        }
    }

    interface ItemSelectCallBack{
        fun itemClicked(text: String)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_grid_item_selection, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments.getString("title", "Item Selection")
        dialog.setTitle(title)
        gridView = view?.findViewById(R.id.item_selection_recycler_view) as RecyclerView
        gridView.adapter = ItemSelectGridAdapter(arguments.getStringArray("list"), callBack)
        gridView.layoutManager = GridLayoutManager(activity, 5)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callBack = context as ItemSelectCallBack
    }

    class ItemSelectGridAdapter(val itemArr: Array<String>, val callBack: ItemSelectCallBack) : RecyclerView.Adapter<ItemSelectGridAdapter.ItemViewHolder>(){

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.txtView.text = itemArr[position]
            holder.txtView.setOnClickListener { callBack.itemClicked(itemArr[position]) }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            var itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_grid_selection, parent, false)
            return ItemViewHolder(itemView)
        }

        override fun getItemCount() = itemArr.size

        class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val txtView = view.findViewById<TextView>(R.id.sel_cur_tv)
        }
    }
}