package be.aidenstorme.todoapplication.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userName: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var password: String = "",
    var isActive: Boolean = false,
) {
    @Ignore
    constructor() : this(
        id = 0,
        userName = "",
        firstName = "",
        lastName = "",
        password = "",
        isActive = false,
    )
}
