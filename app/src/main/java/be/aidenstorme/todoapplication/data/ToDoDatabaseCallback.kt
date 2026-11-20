package be.aidenstorme.todoapplication.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import be.aidenstorme.todoapplication.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Vult de database met voorbeelddata wanneer ze voor het eerst wordt aangemaakt.
 * De database wordt via een [Provider] opgevraagd, anders zou de database van zichzelf afhangen.
 */
class ToDoDatabaseCallback @Inject constructor(
    private val database: Provider<ToDoDatabase>,
    @param:ApplicationScope private val applicationScope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch {
            database.get().userDao().insertUsers(Datasource.getUsers())
            database.get().toDoDao().insertToDos(Datasource.getToDos())
        }
    }
}
