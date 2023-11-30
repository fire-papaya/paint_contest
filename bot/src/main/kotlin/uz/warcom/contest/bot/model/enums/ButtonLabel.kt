package uz.warcom.contest.bot.model.enums

object ButtonLabel {
    const val START = "start"
    const val SUBMISSION = "${EmojiCmd.SUBMISSION} Моя заявка"
    const val CONTEST = "${EmojiCmd.CONTEST} Текущий конкурс"
    const val CODE = "${EmojiCmd.CODE} Получить код"
    const val PRIMED = "${EmojiCmd.PRIMED} Отправить некрашенную модель"
    const val PAINTED = "${EmojiCmd.PAINTED} Отправить покрашенная модель"
    const val COMMUNITY = "${EmojiCmd.COMMUNITY} Мой комьюнити"
    const val SWITCH_COMMUNITY = "${EmojiCmd.SWITCH_COMMUNITY} Поменять комьюнити"
    const val CONFIRM_COMMUNITY = "${EmojiCmd.CONFIRM_COMMUNITY} fs"
    const val HOME = "${EmojiCmd.HOME} Домой"
    // Admin commands
    const val ENTRIES = "entries"
    const val ENTRY = "entry"
    const val CREATE_CONTEST = "${EmojiCmd.CREATE_CONTEST} Создать конкурс"
    const val DRAFT_CONTEST = "${EmojiCmd.DRAFT_CONTEST} Предпросмотр"
    const val UPDATE_NAME = "${EmojiCmd.UPDATE_NAME} Редактировать имя"
    const val UPDATE_DESCRIPTION = "${EmojiCmd.UPDATE_NAME} Редактировать описание"
    const val UPDATE_DATES = "${EmojiCmd.UPDATE_NAME} Редактировать даты проведения"
    const val SUBMIT_CONTEST = "${EmojiCmd.SUBMIT_CONTEST} Подтвердить создание"
}