package eladkay.mathprogram

import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyleConstants.ALIGN_CENTER
import javax.swing.text.TabSet
import javax.swing.text.TabStop


class MathTextBox : JTextPane() {

    private lateinit var listener: DocumentListener
    init {
        listener = Listener()
        this.document.addDocumentListener(listener)
        font = Font("monospaced", Font.PLAIN, FONT_SIZE)
        val scrollPane = JScrollPane(this.parent)
        scrollPane.preferredSize = Dimension(200, 200)
        add(scrollPane)
        setTabs(4)

        addKeyListener(MainScreen.Listener)
        inputMap.put(KeyStroke.getKeyStroke('C', InputEvent.ALT_DOWN_MASK), "center")
        actionMap.put("center", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                println("hi")
                if(selectedText == null) return
                val center = SimpleAttributeSet()
                StyleConstants.setAlignment(center, ALIGN_CENTER)
                styledDocument.setParagraphAttributes(selectionStart, selectionEnd - selectionStart, center, false)
            }

        })
    }

    override fun getToolTipText(event: MouseEvent): String {
        if (selectedText == null) return super.getToolTipText(event)
        val index = viewToModel(event.point)
        val startSelectionIndex = selectionStart
        val endSelectionIndex = selectionEnd
        val text = selectedText
        if (index < startSelectionIndex || index > endSelectionIndex)
            return super.getToolTipText(event)
        return try {
            println(Grapher.evaluateBase64(text, 160 to 160))
            "<html>" + (if("x" !in text)
                ExpressionUtils.evaluate(text, 0.0).toString()
            else "Graph:<br><img src=\"data:image/gif;base64,"+Grapher.evaluateBase64(text, 160 to 160)+"\" alt=\"Graph\" width=\"160\" height=\"160\">\n") + "</html>"//"Graph:<br><img src=\"${File("imagetest").readText()}\"")
            //else "Graph:<br><img src=\"data:image/gif;base64," + Grapher.evaluateBase64(text, 160 to 160) + "\">") + "</html>" //
        } catch (e: Exception) {
            super.getToolTipText(event)
        }
    }

    internal val smallMap = mutableMapOf<IntRange, IntRange>()
    fun debug() {
        println(MainScreen.addMetadata(text))
    }

    private inner class Listener : DocumentListener {
        override fun changedUpdate(e: DocumentEvent?) {
            change(e!!)
        }

        override fun insertUpdate(e: DocumentEvent?) {
            change(e!!)
        }

        private var lastChange: String = ""
        fun change(e: DocumentEvent) {
            var text = this@MathTextBox.text
            if (text == lastChange || e.type == DocumentEvent.EventType.REMOVE) return
            if (MenuBarHandler.greekLock.state && e.length == 1) {
                val letter = text[e.offset]
                if (letter.isLetter()) {
                    var replace = when (letter.toLowerCase()) {
                        'a' -> 'α';'b' -> 'β';'g' -> 'γ';'d' -> 'δ';'e' -> 'ε';'z' -> 'ζ';'h' -> 'η';'t' -> 'θ';'i' -> 'ι';'k' -> 'κ';'l' -> 'λ';'m' -> 'μ';'n' -> 'ν';'x' -> 'ξ';'o' -> 'ο';'p' -> 'π';'r' -> 'ρ';'s' -> 'σ';
                    //'t' -> 'τ'
                        'u' -> 'υ';'f' -> 'φ';'c' -> 'χ';
                    //'p' -> 'ψ'
                        'w' -> 'ω'; // because they look similar
                        else -> letter.toLowerCase()
                    }
                    replace = if (letter.isLowerCase()) replace.toLowerCase() else replace.toUpperCase()
                    val p1 = if (text.length > 1) text.substring(0, e.offset + e.length - 1) else ""
                    val p2 = replace
                    val p3 = if (text.length > e.offset + e.length) text.substring(e.offset + e.length) else ""
                    text = p1 + p2 + p3
                }
            }
            val changes = mutableMapOf(
                    "->" to "→",
                    "infinity" to "∞",
                    "<->" to "↔",
                    "iff" to "↔",
                    "for all" to "∀",
                    "exists" to "∃",
                    "alpha" to "\u03b1",
                    "beta" to "\u03b2",
                    "gamma" to "\u03b3",
                    "delta" to "\u03b4",
                    "epsilon" to "\u03b5",
                    "pi" to "π",
                    "<=" to "\u2264",
                    ">=" to "\u2265",
                    "not equal" to "\u2260",
                    "empty set" to "\u2205",
                    "member of" to "\u2208",
                    "gradient" to "∇",
                    "\\in" to "∈",
                    "(" to "()",
                    "\"" to "\"\"",
                    "\'" to "\'\'",
            )
            for(i in 0..9) changes["^$i"] = superscripts[i].toString()
            //val idxStart = e.offset
            val idxEnd = e.offset + e.length - 1
            var decrementCaret = false
            for (change in changes.keys) {
                //if (idxEnd + 1 >= text.length && text.length >= change.length) println("$change, $text, ${idxEnd + 1 - change.length}, ${idxEnd + 1}") //, ${text.substring(idxEnd - change.length + 1, idxEnd + 1).toLowerCase()}
                //else println("${e.offset + e.length > change.length} ${text.length > e.offset + e.length} ${text.length} ${e.offset + e.length}")
                if (idxEnd + 1 >= change.length && idxEnd - change.length + 1 <= text.length && idxEnd + 1 <= text.length && text.length >= change.length && text.substring(idxEnd - change.length + 1, idxEnd + 1).toLowerCase().trim() == change.trim()) {
                    val p1 = if (text.length > change.length) text.substring(0, e.offset + e.length - change.length) else ""
                    val p2 = changes[change]
                    val p3 = if (text.length > e.offset + e.length) text.substring(e.offset + e.length) else ""
                    text = p1 + p2 + p3
                    if("(" in change || "\"" in change) decrementCaret = true
                    break //!!!!!
                }
            }
            counter++
            checkForSmall(text)
            text = checkForSuperscript(text)
            lastChange = text
            //println("${MainScreen.isAltDown} ${MainScreen.isCtrlDown} ${MainScreen.isInsertOn}")
            MainScreen.saveText(text)
            if (this@MathTextBox.text == text || MainScreen.isInsertOn) return
            SwingUtilities.invokeLater {
                this@MathTextBox.text = text
                if(decrementCaret) this@MathTextBox.caretPosition--
            }
        }

        private var counter = 0
        private val SMALL_REGEX = "(.*)(?:\\((.+)\\)_\\((.+)\\)|((?:\\w|\\d))_((?:\\w|\\d)))(.*)".toRegex()
        fun checkForSmall(text: String) {
            if(caretPosition == this@MathTextBox.text.length - 2) return
            val matchResult = SMALL_REGEX.matchEntire(text) ?: return
            val group2 = matchResult.groups[2] ?: matchResult.groups[4]!!
            val group3 = matchResult.groups[3] ?: matchResult.groups[5]!!
            if(group2.range in smallMap.keys || group2.range in smallMap.values || group3.range in smallMap.keys || group3.range in smallMap.values)
                return
            smallMap[group2.range] = group3.range
        }

        private val SUPERSCRIPT_REGEX = "(.*)(.+)\\^\\((\\d+)\\)(.*)".toRegex()
        private val superscripts = listOf('\u2070', '\u00b9', '\u00b2', '\u00b3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079')
        fun checkForSuperscript(text: String): String {
            if(caretPosition == this@MathTextBox.text.length - 2) return text
            val matchResult = SUPERSCRIPT_REGEX.matchEntire(text) ?: return text
            val group1 = matchResult.groupValues[1]
            val group2 = matchResult.groupValues[2]
            val group3 = matchResult.groupValues[3].map { superscripts[it.toString().toInt()] }.joinToString("")
            val group4 = matchResult.groupValues[4]
            return group1 + group2 + group3 + group4
        }

        override fun removeUpdate(e: DocumentEvent) {
            if (smallMap.keys.any { e.offset in it } || smallMap.values.any { e.offset in it }) {
                    SwingUtilities.invokeLater {
                        println(e.offset)
                        for(pair in smallMap)
                            if(e.offset in pair.key.first..pair.value.last) smallMap.remove(pair.key)
                        var smallMapBak: MutableMap<IntRange, IntRange>
                        do {
                            smallMapBak = smallMap.toMutableMap()
                            checkForSmall(text)
                        } while (smallMapBak != smallMap)
                    }
            }
            MainScreen.saveText(text)
        }

    }

    companion object {
        private const val UNDERLINE = 1
        private const val THREE_HALVES_SPACE = 2
        private val locationsStyle = mutableMapOf<IntRange, Int>()
        const val FONT_SIZE = 20
    }

    // https://community.oracle.com/thread/1507037
    @Suppress("SameParameterValue")
    private fun setTabs(charactersPerTab: Int) {
        val fm = getFontMetrics(font)
        val charWidth = fm.charWidth('w')
        val tabWidth = charWidth * charactersPerTab

        val tabs = arrayOfNulls<TabStop>(10)

        for (j in tabs.indices) {
            val tab = j + 1
            tabs[j] = TabStop((tab * tabWidth).toFloat())
        }

        val tabSet = TabSet(tabs)
        val attributes = SimpleAttributeSet()
        StyleConstants.setTabSet(attributes, tabSet)
        StyleConstants.setFontSize(attributes, FONT_SIZE)
        val length = document.length
        styledDocument.setParagraphAttributes(0, length, attributes, true)
    }

}