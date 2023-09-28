package uz.warcom.contest.bot.model.mapper

import org.mapstruct.Mapper
import uz.warcom.contest.bot.model.CommunityData
import uz.warcom.contest.persistence.domain.Community

@Mapper(componentModel = "spring")
interface CommunityMapStruct {
    fun toCommunityData (entity: Community): CommunityData
}