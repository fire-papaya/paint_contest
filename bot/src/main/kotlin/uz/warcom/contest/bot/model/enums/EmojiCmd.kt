package uz.warcom.contest.bot.model.enums

class EmojiCmd {
    companion object {
        const val START = "start"
        const val SUBMISSION = "\uD83D\uDDBC\uFE0F"
        const val CONTEST = "\uD83D\uDE80"
        const val CODE = "\uD83C\uDFB0"
        const val PRIMED = "\uD83C\uDFA8"
        const val PAINTED = "\uD83C\uDFA8"
        const val COMMUNITY = "\uD83C\uDF07"
        const val SWITCH_COMMUNITY = "\uD83C\uDFD9\uFE0F"
        const val CONFIRM_COMMUNITY = "\uD83C\uDF06"
        const val HOME = "\uD83C\uDFE0"
        // Admin commands
        const val ENTRIES = "entries"
        const val ENTRY = "entry"
        // Admin contest-related commands
        const val CREATE_CONTEST = "\uD83D\uDEE0\uFE0F"
        const val DRAFT_CONTEST = "\uD83D\uDCDC"
        const val UPDATE_NAME = "\uD83D\uDD8A\uFE0F"
        const val UPDATE_DESCRIPTION = "\uD83D\uDCF0"
        const val UPDATE_DATES = "\uD83D\uDCC6"
        const val SUBMIT_CONTEST = "✅"
    }
}