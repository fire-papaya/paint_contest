package uz.warcom.contest.bot.model.mapper

import org.mapstruct.Mapper
import uz.warcom.contest.bot.model.ContestData
import uz.warcom.contest.bot.model.EntryData
import uz.warcom.contest.bot.model.ImageData
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.Image


@Mapper(componentModel = "spring")
interface EntryMapStruct {

    fun toEntryData (entity: Entry): EntryData

    fun toImageData (entity: Image): ImageData

    fun toContestData (entity: Contest): ContestData
}