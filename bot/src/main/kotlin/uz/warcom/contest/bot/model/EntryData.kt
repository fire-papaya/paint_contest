package uz.warcom.contest.bot.model


data class EntryData(
    var id: Int?,
    var code: String?,
    var images: List<ImageData>,
    var user: String?
)

data class EntrySummary (
    var user: String,
    var isPrimed: Boolean = false,
    var isReady: Boolean = false
)


data class EntriesSummary(
    var usersMap: HashMap<String, EntrySummary> = hashMapOf()
)
