package uz.warcom.contest.bot.model.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uz.warcom.contest.bot.model.ContestData
import uz.warcom.contest.bot.model.EntryData
import uz.warcom.contest.bot.model.ImageData
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.Image


@Mapper(componentModel = "spring")
interface EntryMapStruct {

    @Mapping(source = "user.username", target = "user")
    fun toEntryData (entity: Entry): EntryData

    fun toImageData (entity: Image): ImageData

    @Mapping(source = "draft", target = "draft")
    fun toContestData (entity: Contest): ContestData

    @Mapping(source = "draft", target = "draft")
    fun toContest (contestData: ContestData): Contest
}