package net.prismclient.document.pdf

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.prismclient.DocumentMarker
import net.prismclient.Logger
import net.prismclient.document.Document
import net.prismclient.util.localResource
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.image.BufferedImage
import java.io.File

/**
 * A class which represents a PDF, and allows for extraction of text.
 *
 * @author Winter
 */
class PDF(val pdfLocation: File) : Document() {
    val name: String get() = pdfLocation.nameWithoutExtension

    /**
     * Specifies the language to use for OCR based text extraction (tess4j).
     *
     * @see tesseractDatapath
     */
    var language: String = "eng"

    /**
     * Specifies the dataset for tess4j to use for text extraction.
     *
     *  [Tess4j Dataset @ Github](https://github.com/tesseract-ocr/tessdata)
     */
    var tesseractDatapath = "eng.traineddata".localResource.parent

    /**
     * Determines how many pages to parse at once. Processing too many pages at once with a high extraction quality
     * can lead to an [OutOfMemoryError].
     */
    var batchSize: Int = 10

    /**
     * Reads the text of the provided PDF through the provided [extractionMethod] and returns the document as a [String].
     */
    fun extractText(
        extractionMethod: PDFExtractionMethod = PDFExtractionMethod.Text,
        extractionQuality: PDFExtractionQuality = PDFExtractionQuality.Balanced
    ): String {
        Logger.debug(
            "Extracting text from PDF {} using method {} with quality {}",
            name,
            extractionMethod,
            extractionQuality
        )

        if (extractionMethod == PDFExtractionMethod.Text) {
            val extractedText = Loader.loadPDF(pdfLocation).use { document ->
                PDFTextStripper().getText(document)
            }
            if (extractedText.trim().isNotEmpty()) {
                Logger.debug(DocumentMarker, "Extracted text from PDF {} using Text method ", name)
                return extractedText
            }
        }

        // Fallback method or using Image method by default
        val extractedText = StringBuilder()
        val semaphore = Semaphore(2)

        runBlocking {
            val totalPages = Loader.loadPDF(pdfLocation).use { it.numberOfPages }
            for (batchStart in 0 until totalPages step batchSize) {
                val batchEnd = (batchStart + batchSize).coerceAtMost(totalPages)
                val images = extractImagesFromPDF(batchStart until batchEnd, pdfLocation, extractionQuality)
                images.mapIndexed { i, image ->
                    async(Dispatchers.Default) {
                        semaphore.withPermit {
                            Logger.debug(DocumentMarker, "Parsing page {}/{} of {}", batchStart + i, totalPages, name)
                            extractedText.append(Tesseract().performOCR(image))
                            Logger.debug(DocumentMarker, "Parsed page {}/{} of {}", batchStart + i, totalPages, name)
                        }
                    }
                }.awaitAll()
            }
        }

        Logger.debug(DocumentMarker, "Extracted text from PDF {} using Image method ", pdfLocation.nameWithoutExtension)

        return extractedText.toString()
    }

    private fun extractImagesFromPDF(
        pages: IntRange,
        pdfFile: File,
        extractionQuality: PDFExtractionQuality
    ): List<BufferedImage> = mutableListOf<BufferedImage>().also {
        Loader.loadPDF(pdfFile).use { document ->
            val pdfRenderer = PDFRenderer(document)
            for (page in pages) {
                if (page < document.numberOfPages) {
                    Logger.debug(DocumentMarker, "Rendering page {} for pdf {}", page, name)
                    it.add(pdfRenderer.renderImageWithDPI(page, extractionQuality.dpi.toFloat()))
                    Logger.debug(DocumentMarker, "Rendered page {} for pdf {}", page, name)
                }
            }
        }
    }

    private fun Tesseract.performOCR(image: BufferedImage): String {
        setDatapath(tesseractDatapath)
        setLanguage(language)
        return try {
            doOCR(image)
        } catch (e: TesseractException) {
            e.printStackTrace()
            // Return an empty String if OCR failed.
            ""
        }
    }

    /**
     * Specifies the method to extract the PDF. Generally [PDFExtractionMethod.Text] is recommended.
     *
     * @author Winter
     */
    enum class PDFExtractionMethod {
        /**
         * Extracts the text directly through the metadata of the PDF, will default to Image if no text-metadata is present.
         */
        Text,

        /**
         * Uses OCR to extract text from the Image.
         */
        Image
    }

    /**
     * Specifies the quality (which adjusts the model among other parameters) for image based extraction, where higher
     * quality generally takes longer.
     *
     * @author Winter
     */
    enum class PDFExtractionQuality(val dpi: Int) {
        /**
         * Optimal for high density documents e.g., research papers. (Will take up a lot of Memory add "-Xmx4g" to VM Options)
         */
        High(dpi = 450),

        /**
         * Good for general purpose documents e.g., resumes, general documents with easily legible text.
         */
        Balanced(dpi = 300),

        /**
         * If speed is important, and the document is easily legible.
         */
        Low(dpi = 150)
    }
}