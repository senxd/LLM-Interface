package net.prismclient.document.type.text

import java.io.File
import java.io.StringReader
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.rtf.RTFEditorKit

/**
 * A class which represents standard text types such as .txt and .rtf. Formatting is ignored.
 *
 * @author Winter
 */
class GenericText(file: File) : TextDocument(file, "txt", "rtf") {
    override fun extract(): String = Builder {
        append(file.readText().let { if (extension == "rtf") unformatRTF(it) else it })
    }

    private fun unformatRTF(content: String): String {
        val document = HTMLEditorKit().createDefaultDocument()

        RTFEditorKit().read(StringReader(content), document, 0)

        return document.getText(0, document.length)
    }
}