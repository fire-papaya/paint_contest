package uz.warcom.contest.bot.model.enums

object ButtonLabel {
    val SUBMISSION = "${EmojiCmd.SUBMISSION} Моя заявка"
    val CONTEST = "${EmojiCmd.CONTEST} Текущий конкурс"
    val CODE = "${EmojiCmd.CODE} Получить код"
    val PRIMED = "${EmojiCmd.PRIMED} Отправить некрашенную модель"
    val PAINTED = "${EmojiCmd.PAINTED} Отправить покрашенная модель"
    val COMMUNITY = "${EmojiCmd.COMMUNITY} Мой комьюнити"
    val HOME = "${EmojiCmd.HOME} Домой"
    // admin commands
    val ENTRIES = "entries"
    val ENTRY = "entry"
    val CREATE_CONTEST = "${EmojiCmd.CREATE_CONTEST} Создать конкурс"
    val DRAFT_CONTEST = "${EmojiCmd.DRAFT_CONTEST} Предпросмотр"
    val UPDATE_NAME = "${EmojiCmd.UPDATE_NAME} Редактировать имя"
    val UPDATE_DESCRIPTION = "${EmojiCmd.UPDATE_DESCRIPTION} Редактировать описание"
    val UPDATE_DATES = "${EmojiCmd.UPDATE_DATES} Редактировать даты проведения"
    val SUBMIT_CONTEST = "${EmojiCmd.SUBMIT_CONTEST} Подтвердить создание"
}