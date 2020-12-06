package no.sigurof.gravity.demo

class Collision(
    val idOne: Int,
    val idOther: Int
) {

    data class Builder(
        var idOne: Int? = null,
        var idOther: Int? = null
    ) {
        fun build(): Collision {
            return Collision(
                idOne = idOne?: error("Expected idOne to be set."),
                idOther = idOther?: error("Expected idOther to be set.")
            )
        }
    }
}