package jp.co.keyplanning.okinawaitfes

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.Executors

enum class Screen { COUNTER, SURVEY, RESULTS }

class CounterViewModel(application: Application) : AndroidViewModel(application) {
    private val store = SurveyStore(InternalSurveyFile(application.filesDir))
    private val worker = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())
    var count by mutableStateOf(0)
        private set
    var screen by mutableStateOf(Screen.COUNTER)
        private set
    var satisfaction by mutableStateOf<String?>(null)
        private set
    var discovery by mutableStateOf<String?>(null)
        private set
    var age by mutableStateOf<String?>(null)
        private set
    var responses by mutableStateOf<List<Response>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var saving by mutableStateOf(false)
        private set
    var loadError by mutableStateOf(false)
        private set
    var saveError by mutableStateOf(false)
        private set
    var saved by mutableStateOf(false)
        private set
    val canSave: Boolean
        get() = satisfaction != null && discovery != null && age != null &&
            !saving && !loading && !loadError

    init { reload() }

    fun increment() { if (count < Int.MAX_VALUE) count++ }
    fun reset() { count = 0 }

    fun navigate(destination: Screen) {
        if (saving) return
        clearForm()
        screen = destination
    }

    fun select(question: Int, value: String) {
        if (saving) return
        when (question) {
            0 -> satisfaction = value
            1 -> discovery = value
            2 -> age = value
        }
        saved = false
        saveError = false
    }

    private fun clearForm() {
        satisfaction = null
        discovery = null
        age = null
        saved = false
        saveError = false
    }

    fun reload() {
        if (loading || saving) return
        loading = true
        worker.execute {
            val result = runCatching { store.load() }
            main.post {
                result.onSuccess { responses = it }
                loadError = result.isFailure
                loading = false
            }
        }
    }

    fun save() {
        if (!canSave) return
        val answer = Answer(satisfaction!!.toInt(), discovery!!, age!!)
        saving = true
        saved = false
        saveError = false
        worker.execute {
            val result = runCatching { store.save(answer) }
            main.post {
                result.onSuccess {
                    responses = it
                    clearForm()
                    saved = true
                }.onFailure {
                    if (it is SurveyReadException) loadError = true else saveError = true
                }
                saving = false
            }
        }
    }

    override fun onCleared() {
        worker.shutdown()
    }
}
