// IMPROVE: Progress reports on large file extraction
// IMPROVE: Memory Management (with High DPI -> 1.5gb for 1 image????)
// IDEA: Validation of processed text -> with LLMs? (Emending)
// FUTURE: Use different OCR Library for GPU based processing (massive documents).
// TODO: Debug markers
package net.prismclient.document.type

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
class PDF(pdfLocation: File) : Document(pdfLocation, "pdf") {
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
    var tesseractDatapath: String = "eng.traineddata".localResource.parent

    /**
     * Determines how many pages to parse at once. Processing too many pages at once with a high extraction quality
     * can lead to an [OutOfMemoryError].
     */
    var batchSize: Int = 10

    var extractionMethod: ExtractionMethod = ExtractionMethod.Text
    var extractionQuality: ExtractionQuality = ExtractionQuality.Balanced

    /**
     * Reads the text of the provided PDF through the provided [extractionMethod] and returns the document as a [String].
     */
    override fun extract(): String = Builder {
        if (extractionMethod == ExtractionMethod.Text) {
            val extractedText = Loader.loadPDF(file).use { document ->
                PDFTextStripper().getText(document)
            }
            if (extractedText.trim().isNotEmpty()) {
                return extractedText
            }
        }

        runBlocking {
            val totalPages = Loader.loadPDF(file).use { it.numberOfPages }
            for (batchStart in 0 until totalPages step batchSize) {
                val batchEnd = (batchStart + batchSize).coerceAtMost(totalPages)
                val images = extractImagesFromPDF(batchStart until batchEnd, file, extractionQuality)
                images.map { image ->
                    async(Dispatchers.Default) {
                        this@Builder.append(Tesseract().performOCR(image))
                    }
                }.awaitAll()
            }
        }
    }

    private fun extractImagesFromPDF(
        pages: IntRange,
        pdfFile: File,
        extractionQuality: ExtractionQuality
    ): List<BufferedImage> = mutableListOf<BufferedImage>().also {
        Loader.loadPDF(pdfFile).use { document ->
            val pdfRenderer = PDFRenderer(document)
            for (page in pages) {
                if (page < document.numberOfPages) {
                    it.add(pdfRenderer.renderImageWithDPI(page, extractionQuality.dpi.toFloat()))
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
     * Specifies the method to extract the PDF. Generally [ExtractionMethod.Text] is recommended.
     *
     * @author Winter
     */
    enum class ExtractionMethod {
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
    enum class ExtractionQuality(val dpi: Int) {
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