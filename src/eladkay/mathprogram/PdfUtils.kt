package eladkay.mathprogram

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

object PdfUtils {

    fun export(file: String) {
        val doc = makePdf(File(file).nameWithoutExtension, MainScreen.NAME)
        // todo
        val everything = MainScreen.textBox.smallMap.map { it.key.first..it.value.last }
        var text = MainScreen.textBox.text
        everything.forEach { text = text.replaceRange(it, "\t") }
        doc.addPage(text)
        doc.finishPdf(file)
    }

    fun makePdf(title: String, author: String = "", subject: String = ""): PDDocument {
        val doc = PDDocument()
        val pdd = doc.documentInformation
        pdd.title = title
        if(author.isNotBlank()) pdd.author = author
        if(subject.isNotBlank()) pdd.subject = subject
        pdd.creationDate = Calendar.getInstance()
        pdd.modificationDate = Calendar.getInstance()
        pdd.creator = MainScreen.NAME

        return doc
    }

    private val offset = 30f

    fun PDDocument.addPage(vararg content: Any): PDDocument {
        val page = PDPage()
        val stream = PDPageContentStream(this, page)
        var flag = false
        var counter = 1
        var width = offset
        var lines = PDType1Font.HELVETICA.getHeight('h'.toInt())
        for(thing in content) {
            if(thing is String) {
                if(flag) stream.newLine()
                stream.beginText()
                stream.setFont(PDType1Font.HELVETICA, 16f)
                stream.newLineAtOffset(width, lines)
                stream.showText(thing)
                stream.endText()
                width += PDType1Font.HELVETICA.getStringWidth(thing)
                if(width > 100) {
                    lines += PDType1Font.HELVETICA.getHeight('h'.toInt())
                    width = 0f
                }
                flag = true
            } else if(thing is BufferedImage) {
                val byteStream = ByteArrayOutputStream()
                ImageIO.write(thing, "jpg", byteStream)
                val image = PDImageXObject.createFromByteArray(this, byteStream.toByteArray(), "image_${counter++}")
                stream.drawImage(image, width, lines)
                lines += PDType1Font.HELVETICA.getHeight('h'.toInt())
                width = 0f
                byteStream.close()
            }
        }
        stream.close()
        this.addPage(page)

        return this
    }

    fun PDDocument.finishPdf(fileName: String) {
        save(fileName)
        close()
    }

    fun PDDocument.finishPdf(file: File) {
        save(file)
        close()
    }
}