package com.brightnest.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Task(val id: String, val title: String, val date: String, val done: Boolean)
data class Note(val id: String, val title: String, val body: String, val updatedAt: String)
data class Expense(
    val id: String,
    val type: String,
    val category: String,
    val amount: Double,
    val date: String,
    val note: String
)
data class Habit(val id: String, val name: String, val color: String, val history: List<String>)
data class Goal(val id: String, val name: String, val targetDate: String, val progress: Int)

class AdultStore(context: Context) {
    private val sp = context.getSharedPreferences("brightnest_prefs", Context.MODE_PRIVATE)

    private fun getStr(key: String): String = sp.getString("bn_adult_$key", "") ?: ""
    private fun putStr(key: String, value: String) = sp.edit().putString("bn_adult_$key", value).apply()
    private fun arr(key: String): JSONArray =
        try { JSONArray(getStr(key).ifEmpty { "[]" }) } catch (e: Exception) { JSONArray() }

    // ---- Tasks ----
    var tasks: List<Task>
        get() {
            val a = arr("tasks")
            return (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                Task(o.optString("id"), o.optString("title"), o.optString("date"), o.optBoolean("done"))
            }
        }
        set(value) {
            val a = JSONArray()
            value.forEach { t ->
                a.put(JSONObject().put("id", t.id).put("title", t.title).put("date", t.date).put("done", t.done))
            }
            putStr("tasks", a.toString())
        }

    fun addTask(t: Task) { tasks = tasks + t }
    fun toggleTask(id: String) { tasks = tasks.map { if (it.id == id) it.copy(done = !it.done) else it } }
    fun deleteTask(id: String) { tasks = tasks.filter { it.id != id } }

    // ---- Notes ----
    var notes: List<Note>
        get() {
            val a = arr("notes")
            return (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                Note(o.optString("id"), o.optString("title"), o.optString("body"), o.optString("updatedAt"))
            }
        }
        set(value) {
            val a = JSONArray()
            value.forEach { n ->
                a.put(JSONObject().put("id", n.id).put("title", n.title).put("body", n.body).put("updatedAt", n.updatedAt))
            }
            putStr("notes", a.toString())
        }

    fun addNote(n: Note) { notes = notes + n }
    fun updateNote(id: String, title: String, body: String, updatedAt: String) {
        notes = notes.map { if (it.id == id) it.copy(title = title, body = body, updatedAt = updatedAt) else it }
    }
    fun deleteNote(id: String) { notes = notes.filter { it.id != id } }

    // ---- Expenses ----
    var expenses: List<Expense>
        get() {
            val a = arr("expenses")
            return (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                Expense(o.optString("id"), o.optString("type"), o.optString("category"), o.optDouble("amount", 0.0), o.optString("date"), o.optString("note"))
            }
        }
        set(value) {
            val a = JSONArray()
            value.forEach { e ->
                a.put(JSONObject().put("id", e.id).put("type", e.type).put("category", e.category).put("amount", e.amount).put("date", e.date).put("note", e.note))
            }
            putStr("expenses", a.toString())
        }

    fun addExpense(e: Expense) { expenses = expenses + e }
    fun deleteExpense(id: String) { expenses = expenses.filter { it.id != id } }

    // ---- Habits ----
    var habits: List<Habit>
        get() {
            val a = arr("habits")
            return (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                val h = o.optJSONArray("history") ?: JSONArray()
                Habit(o.optString("id"), o.optString("name"), o.optString("color"), (0 until h.length()).map { h.getString(it) })
            }
        }
        set(value) {
            val a = JSONArray()
            value.forEach { hb ->
                val h = JSONArray(); hb.history.forEach { h.put(it) }
                a.put(JSONObject().put("id", hb.id).put("name", hb.name).put("color", hb.color).put("history", h))
            }
            putStr("habits", a.toString())
        }

    fun addHabit(h: Habit) { habits = habits + h }
    fun toggleHabit(id: String, date: String) {
        habits = habits.map {
            if (it.id == id) {
                val hist = if (it.history.contains(date)) it.history - date else it.history + date
                it.copy(history = hist)
            } else it
        }
    }
    fun deleteHabit(id: String) { habits = habits.filter { it.id != id } }

    // ---- Goals ----
    var goals: List<Goal>
        get() {
            val a = arr("goals")
            return (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                Goal(o.optString("id"), o.optString("name"), o.optString("targetDate"), o.optInt("progress", 0))
            }
        }
        set(value) {
            val a = JSONArray()
            value.forEach { g ->
                a.put(JSONObject().put("id", g.id).put("name", g.name).put("targetDate", g.targetDate).put("progress", g.progress))
            }
            putStr("goals", a.toString())
        }

    fun addGoal(g: Goal) { goals = goals + g }
    fun updateGoal(id: String, progress: Int) { goals = goals.map { if (it.id == id) it.copy(progress = progress) else it } }
    fun deleteGoal(id: String) { goals = goals.filter { it.id != id } }

    // ---- Tasbeeh ----
    var tasbeehCount: Int
        get() = sp.getInt("bn_adult_tasbeehCount", 0)
        set(v) = sp.edit().putInt("bn_adult_tasbeehCount", v).apply()
    var tasbeehTarget: Int
        get() = sp.getInt("bn_adult_tasbeehTarget", 33)
        set(v) = sp.edit().putInt("bn_adult_tasbeehTarget", v).apply()

    fun incrementTasbeeh() { tasbeehCount += 1 }
    fun resetTasbeeh() { tasbeehCount = 0 }

    // ---- Water ----
    var waterGlasses: Int
        get() = sp.getInt("bn_adult_waterGlasses", 0)
        set(v) = sp.edit().putInt("bn_adult_waterGlasses", v).apply()
    var waterTarget: Int
        get() = sp.getInt("bn_adult_waterTarget", 8)
        set(v) = sp.edit().putInt("bn_adult_waterTarget", v).apply()

    fun addWater() { waterGlasses += 1 }
    fun removeWater() { waterGlasses = maxOf(0, waterGlasses - 1) }
}
