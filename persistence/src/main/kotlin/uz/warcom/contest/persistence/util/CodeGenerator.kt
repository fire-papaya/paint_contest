package uz.warcom.contest.persistence.util

object CodeGenerator {

    private const val symbolPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

    fun generate (length: Int = 6) : String {
        return buildString {
            repeat(length) {
                this.append(symbolPool[ (Math.random() * symbolPool.length).toInt()])
            }
        }
    }
}