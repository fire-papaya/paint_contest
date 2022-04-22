package uz.warcom.contest.persistence.service

import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.Image
import uz.warcom.contest.persistence.domain.WarcomUser
import uz.warcom.contest.persistence.dto.ImageDto
import uz.warcom.contest.persistence.exception.ContestNotFoundException
import uz.warcom.contest.persistence.exception.EntryNotFoundException
import uz.warcom.contest.persistence.repository.EntryRepository
import uz.warcom.contest.persistence.repository.ImageRepository
import java.io.File
import java.util.*


@Service
class EntryService
constructor(
    private val entryRepository: EntryRepository,
    private val imageService: ImageService,
    private val contestService: ContestService
){

    fun createEntry (user: WarcomUser): Entry {
        val contest = contestService.currentContest() ?: throw ContestNotFoundException()

        val existing = getEntry(user, contest)
        if (existing != null)
            return existing

        val entry = Entry.getInstance()
            .withContest(contest)
            .withUser(user)

        return entryRepository.save(entry)
    }

    fun getCurrentEntry (user: WarcomUser): Entry {
        val contest = contestService.currentContest() ?: throw ContestNotFoundException()

        return getEntry(user, contest) ?: throw EntryNotFoundException()
    }

    private fun getEntry (user: WarcomUser, contest: Contest): Entry? {
        return entryRepository.findFirstByContestAndUser(contest, user)
    }

    fun addEntryImage (entryImage: ImageDto) {
        val entry = getCurrentEntry(entryImage.user)

        imageService.addEntryImage(entry, entryImage)
    }

    fun compileEntryImage (user: WarcomUser) {
        val entry = getCurrentEntry(user)

        imageService.compileEntryImage(entry)
    }
}