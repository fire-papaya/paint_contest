package uz.warcom.contest.bot.model.enums

class EmojiCmd {
    companion object {
        const val START = "start"
        const val SUBMISSION = "\uD83D\uDDBC\uFE0F"
        const val CONTEST = "\uD83D\uDE80"
        const val CODE = "code"
        const val PRIME = "prime"
        const val READY = "ready"
        const val COMMUNITY = "\uD83C\uDF07"
        const val SWITCH_COMMUNITY = "\uD83C\uDFD9\uFE0F"
        const val CONFIRM_COMMUNITY = "\uD83C\uDF06"
        const val HOME = "\uD83C\uDFE0"
        // Admin commands
        const val ENTRIES = "entries"
        const val ENTRY = "entry"
        const val CROPPED = "cropped"
        const val CREATE_CONTEST = "create"
        const val UPDATE_CONTEST = "update"
    }
}