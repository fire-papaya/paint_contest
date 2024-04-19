package uz.warcom.contest.persistence.service

import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.Image
import uz.warcom.contest.persistence.dto.ImageDto
import uz.warcom.contest.persistence.exception.NoPaintedImageException
import uz.warcom.contest.persistence.exception.NoPrimedImageException
import uz.warcom.contest.persistence.repository.ImageRepository
import java.util.*
import java.util.stream.Collectors


@Service
class ImageService (
    private val imageRepository: ImageRepository
) {

    fun addEntryImage (entry: Entry, entryImage: ImageDto) {
        val image = Image().also {
            it.guid = UUID.randomUUID()
            it.entry = entry
            it.isReady = entryImage.isReady
            it.telegramFileId = entryImage.fileId
        }

        imageRepository.save(image)
    }

    fun getEntryImagesInfo (entry: Entry): List<Image> {
        val images = imageRepository.findAllByEntryOrderByDateCreatedDesc(entry)

        val primed = images.firstOrNull { !it.isReady } ?: throw RuntimeException()

        val painted = images.stream().filter { it.isReady }.limit(3).collect(Collectors.toList())

        painted.add(primed)

        return painted
    }

    fun getEntryImages (entry: Entry): List<Image> {
        val images = imageRepository.findAllByEntryOrderByDateCreatedDesc(entry)

        val primed = images.firstOrNull { !it.isReady } ?: throw NoPrimedImageException()

        val painted = images.filter { it.isReady }.take(3).takeIf { it.size == 3 }?.toMutableList()
            ?: throw NoPaintedImageException()

        painted.add(primed)

        return painted
    }
}