package uz.warcom.contest.bot.model.enums

enum class EmojiCmd(val code: String) {
    START("/"),
    SUBMISSION("\uD83D\uDDBC\uFE0F"),
    CONTEST("\uD83D\uDE80"),
    CODE("\uD83C\uDFB0"),
    PRIMED("✏\uFE0F"),
    PAINTED("\uD83C\uDFA8"),
    COMMUNITY("\uD83C\uDF07"),
    SWITCH_COMMUNITY("\uD83C\uDFD9\uFE0F"),
    CONFIRM_COMMUNITY("\uD83C\uDF06"),
    HOME("\uD83C\uDFE0"),

    ENTRIES("entries"),
    ENTRY("entry"),

    CREATE_CONTEST("\uD83D\uDEE0\uFE0F"),
    PREVIEW_CONTEST("\uD83D\uDCDC"),
    UPDATE_NAME("\uD83D\uDD8A\uFE0F"),
    UPDATE_DESCRIPTION("\uD83D\uDCF0"),
    UPDATE_DATES("\uD83D\uDCC6"),
    SUBMIT_CONTEST("✅"),
    ;

    override fun toString(): String {
        return code
    }
}