package uz.warcom.contest.persistence.service

import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.Image
import uz.warcom.contest.persistence.dto.ImageDto
import uz.warcom.contest.persistence.repository.ImageRepository
import uz.warcom.contest.persistence.util.ImageCropper
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO


@Service
class ImageService (
    private val imageRepository: ImageRepository
) {
    private val baseDir = "pics"

    fun addEntryImage (entry: Entry, entryImage: ImageDto) {
        val image = Image().also {
            it.guid = UUID.randomUUID()
            it.entry = entry
            it.isReady = entryImage.isReady
        }

        savePhoto(entryImage.file, image)

        imageRepository.save(image)
    }

    fun compileEntryImage (entry: Entry): List<BufferedImage> {
        val images = imageRepository.findAllByEntryOrderByDateCreatedDesc(entry)

        // Todo proper exception
        val primed = images.lastOrNull { !it.isReady } ?: throw RuntimeException()

        // Todo proper exception
//        val painted = images.asReversed().filter { it.isReady }.take(3).takeIf { it.size == 3 } ?: throw RuntimeException()

        val primedFile = File("${baseDir}/${entry.contest!!.id}/${entry.id!!}/${primed.guid}.jpg")

        val imagePrimed = ImageIO.read(primedFile)

        val croppedPrimed = listOf(ImageCropper.cropSquare(imagePrimed))
        return croppedPrimed
    }

    private fun savePhoto (photo: ByteArray, image: Image) {
        val entry = image.entry!!
        val contest = entry.contest!!

        val targetFile = File("${baseDir}/${contest.id}/${entry.id!!}/${image.guid}.jpg")

        FileUtils.writeByteArrayToFile(targetFile, photo)
    }
}