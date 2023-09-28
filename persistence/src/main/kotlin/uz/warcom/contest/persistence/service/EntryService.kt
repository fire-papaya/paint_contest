package uz.warcom.contest.persistence.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.Image
import uz.warcom.contest.persistence.domain.WarcomUser
import uz.warcom.contest.persistence.dto.ImageDto
import uz.warcom.contest.persistence.exception.ContestNotFoundException
import uz.warcom.contest.persistence.exception.EntryNotFoundException
import uz.warcom.contest.persistence.repository.EntryRepository


@Service
class EntryService
constructor(
    private val entryRepository: EntryRepository,
    private val imageService: ImageService,
    private val contestService: ContestService
){

    fun createEntry (user: WarcomUser): Entry {
        val contest = contestService.currentContest(user.community!!) ?: throw ContestNotFoundException()

        val existing = getEntry(user, contest)
        if (existing != null)
            return existing

        val entry = Entry.getInstance()
            .withContest(contest)
            .withUser(user)

        return entryRepository.save(entry)
    }

    fun getCurrentEntry (user: WarcomUser): Entry {
        val contest = contestService.currentContest(user.community!!) ?: throw ContestNotFoundException()

        return getEntry(user, contest) ?: throw EntryNotFoundException()
    }

    fun getCurrentEntries (): List<Entry> {
        val contest = contestService.currentContest() ?: throw ContestNotFoundException()

        return getEntries(contest)
    }

    fun addEntryImage (entryImage: ImageDto) {
        val entry = getCurrentEntry(entryImage.user)

        imageService.addEntryImage(entry, entryImage)
    }

    fun getEntryImages (user: WarcomUser): List<Image> {
        val entry = getCurrentEntry(user)

        return imageService.getEntryImages(entry)
    }

    fun getEntryImagesInfo (entryId: Int): List<Image> {
        val entry = getEntry(entryId) ?: throw EntryNotFoundException()

        return imageService.getEntryImagesInfo(entry)
    }

    private fun getEntry (user: WarcomUser, contest: Contest): Entry? {
        return entryRepository.findFirstByContestAndUser(contest, user)
    }

    private fun getEntry (entryId: Int): Entry? {
        return entryRepository.findByIdOrNull(entryId)
    }

    private fun getEntries (contest: Contest): List<Entry> {
        return entryRepository.findAllByContest(contest)
    }
}