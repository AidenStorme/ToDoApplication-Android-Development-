package be.aidenstorme.todoapplication

import android.app.Application
import be.aidenstorme.todoapplication.data.AppContainer
import be.aidenstorme.todoapplication.data.DefaultAppContainer

class ToDoApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
