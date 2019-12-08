package example.com.sqlitedemo.model

class Person(
    id: Int,
    var name: String?,
    var phoneNumber: String?,
    var email: String?,
    var occupation: String?
) {
    var id: Long = 0

    init {
        this.id = id.toLong()
    }
}
