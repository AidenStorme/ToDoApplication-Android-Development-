package be.aidenstorme.todoapplication.models

data class User(
    var id: Int,
    var userName: String,
    var firstName: String,
    var lastName: String,
    var password: String,
    var isActive: Boolean,
) {
    constructor() : this(
        id = 0,
        userName = "",
        firstName = "",
        lastName = "",
        password = "",
        isActive = false,
    )
}
