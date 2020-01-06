package com.example.ungdungngenhac.interfaces

interface onItemClickListener {
    interface online{
        fun setOnClickListener(position: Int)
        fun setOnLongClickListener(position: Int)

    }
    interface favorite{
        fun setOnClickListener(position: Int)
        fun setOnLongClickListener(position: Int)

    }

}