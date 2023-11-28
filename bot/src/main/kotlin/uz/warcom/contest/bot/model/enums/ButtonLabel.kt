package uz.warcom.contest.bot.model.enums

object ButtonLabel {
    const val START = "start"
    const val SUBMISSION = "${EmojiCmd.SUBMISSION} Моя заявка"
    const val CONTEST = "${EmojiCmd.CONTEST} Текущий конкурс"
    const val CODE = "${EmojiCmd.CODE} Получить код"
    const val PRIMED = "${EmojiCmd.PRIMED} Некрашенная модель"
    const val PAINTED = "${EmojiCmd.PAINTED} Крашенная модель"
    const val COMMUNITY = "${EmojiCmd.COMMUNITY} Мой комьюнити"
    const val SWITCH_COMMUNITY = "${EmojiCmd.SWITCH_COMMUNITY} Поменять комьюнити"
    const val CONFIRM_COMMUNITY = "${EmojiCmd.CONFIRM_COMMUNITY} fs"
    const val HOME = "${EmojiCmd.HOME} Домой"
    // Admin commands
    const val ENTRIES = "entries"
    const val ENTRY = "entry"
    const val CROPPED = "cropped"
    const val CREATE_CONTEST = "create"
    const val UPDATE_CONTEST = "update"
}