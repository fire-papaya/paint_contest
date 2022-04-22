package uz.warcom.contest.bot.model.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mappings
import uz.warcom.contest.bot.model.UserData
import uz.warcom.contest.persistence.domain.WarcomUser

@Mapper(componentModel = "spring")
interface UserMapStruct {

    fun toUserData (entity: WarcomUser): UserData
}