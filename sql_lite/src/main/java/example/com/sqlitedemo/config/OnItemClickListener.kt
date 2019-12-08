package example.com.sqlitedemo.config

import example.com.sqlitedemo.model.Person

interface OnItemClickListener {
    fun onEditClicked(pos: Int, student: Person)
    fun onDeleteClicked(adapterPosition: Int, studentBean: Person)
}