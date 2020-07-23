package eladkay.mathprogram

import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

object MainScreen : JFrame(), ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        if(!textBox.isEnabled) return
        var shouldCaps = textBox.text.endsWith("\n") || textBox.text.endsWith(". ") || textBox.text.isEmpty() || textBox.selectionStart == 0
        var text = ""

        when (e.actionCommand) {
            "Let" -> text = MathTextUtils.let()
            "Choose" -> text = MathTextUtils.choose()
            "Inductive hypothesis and base step" -> text = MathTextUtils.induction1()
            "Inductive step" -> text = MathTextUtils.induction2()
            "Induction complete" -> text = MathTextUtils.induction3()
            in symbols -> {
                text = e.actionCommand
                shouldCaps = false
                if(e.actionCommand !in lastUsedList) {
                    lastUsed.add(getMenuItem(e.actionCommand))
                    lastUsedList.add(e.actionCommand)
                }
            }
            in expressions -> {
                text = e.actionCommand
                shouldCaps = false
            }
            "Line break" -> text = "\n"
            "Space" -> text = " "
            "Undo" -> {
                UndoRedoUtils.undoAction()
            }
            "Redo" -> {
                UndoRedoUtils.redoAction()
            }
            "Clear" -> {
                textBox.text = ""
            }
            "Assumption for ex falso" -> text = MathTextUtils.assumeForContradiction1()
            "Contradiction from ex falso" -> text = MathTextUtils.assumeForContradiction2()
            "Evaluate expression" -> {
                if(textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    if("x" in textBox.selectedText) {
                        textBox.isEnabled = false
                        val text2 = textBox.selectedText
                        NumberChooser(action = {
                            text = ExpressionUtils.evaluate(text2, it.toDouble()).toString()
                            textBox.replaceSelection(text[0].toUpperCase() + text.substring(1))
                            textBox.isEnabled = true
                        }) { textBox.isEnabled = true }.isVisible = true
                    } else {
                        text = ExpressionUtils.evaluate(textBox.selectedText, 0.0).toString()
                        shouldCaps = false
                    }
                }
            }
            "Limit" -> {
                if(textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    if("x" in textBox.selectedText) {
                        textBox.isEnabled = false
                        val text2 = textBox.selectedText
                        NumberChooser(action = {
                            text = ExpressionUtils.limit(text2, it.toDouble()).toString()
                            textBox.replaceSelection(text[0].toUpperCase() + text.substring(1))
                            textBox.isEnabled = true
                        }) { textBox.isEnabled = true }.isVisible = true
                    } else {
                        text = ExpressionUtils.limit(textBox.selectedText, 0.0).toString()
                        shouldCaps = false
                    }
                }
            }
            "Approximate root" -> {
                if(textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    if("x" in textBox.selectedText) {
                        textBox.isEnabled = false
                        val text2 = textBox.selectedText
                        NumberChooser(action = {
                            text = ExpressionUtils.approximateRoot(text2, it.toDouble()).toString()
                            textBox.replaceSelection(text[0].toUpperCase() + text.substring(1))
                            textBox.isEnabled = true
                        }) { textBox.isEnabled = true }.isVisible = true
                    } else {
                        if(textBox.selectedText.toDoubleOrNull() == 0.0) text = "every real x"
                        else JOptionPane.showMessageDialog(null, "No roots of constant function!")
                    }
                }
            }
            "Negate" -> {
                if(textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    text = ExpressionUtils.negate(textBox.selectedText)
                }
            }
            else -> println(e.actionCommand)
        }
        if(text.isEmpty()) return
        if (shouldCaps) textBox.replaceSelection(text[0].toUpperCase() + text.substring(1)) else textBox.replaceSelection(text)

    }

    private val menuBar = JMenuBar()
    private val textBox = JTextArea()
    private val greekLock = JCheckBoxMenuItem("Greek lock")
    val lastUsed = JMenu("Last used")
    val lastUsedList = mutableListOf<String>()
    val uppercaseGreek = listOf("\u0391", "\u0392", "\u0393", "\u0394", "\u0395", "\u0396", "\u0397", "\u0398", "\u0399", "\u039a", "\u039b", "\u039c", "\u039d", "\u039e", "\u039f", "\u03a0", "\u03a1", "\u03a2", "\u03a3", "\u03a4", "\u03a5", "\u03a6", "\u03a7", "\u03a8", "\u03a9")
    val lowercaseGreek = listOf("\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8", "\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", "\u03c1", "\u03c2", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8", "\u03c9")
    val sets = listOf("\u2208", "\u2209", "\u2205", "\u220b", "\u220c", "\u2229", "\u222A", "\u2282", "\u2283", "\u2284", "\u2285", "\u2286", "\u2287", "\u2288", "\u2289", "\u228a", "\u228b", "\u2102", "\uD835\uDD3D", "\u2115", "\u211A", "\u211d", "\u2124")
    val quantifiers = listOf("\u2203", "\u2204", "\u2200")
    val logic = listOf("\u2227", "\u2228", "\u2234", "\u2235")
    val arrows = listOf("\u2190", "\u2191", "\u2192", "\u2193", "\u2194", "\u2195", "\u2196", "\u2197", "\u2198", "\u2199", "\u219a", "\u219b", "\u219c", "\u219d", "\u219e", "\u219f", "\u21a0", "\u21a1", "\u21a2", "\u21a3", "\u21a4", "\u21a5", "\u21a6", "\u21a7", "\u21a8", "\u21a9", "\u21aa", "\u21ab", "\u21ac", "\u21ad", "\u21ae", "\u21af", "\u21b0", "\u21b1", "\u21b2", "\u21b3", "\u21b4", "\u21b5", "\u21b6", "\u21b7", "\u21b8", "\u21b9", "\u21ba", "\u21bb", "\u21bc", "\u21bd", "\u21be", "\u21bf", "\u21c0", "\u21c1", "\u21c2", "\u21c3", "\u21c4", "\u21c5", "\u21c6", "\u21c7", "\u21c8", "\u21c9", "\u21ca", "\u21cb", "\u21cc", "\u21cd", "\u21ce", "\u21cf", "\u21d0", "\u21d1", "\u21d2", "\u21d3", "\u21d4")
    val calculus = listOf("\u2202", "\u221e", "\u222b", "\u222c", "\u222d", "\u222e", "\u222f", "\u2220", "\u2231", "\u2231", "\u2232", "\u2233", "\u2a1b", "\u2a1c")
    val misc = listOf("\u2206", "\u2207", "\u2213", "\u007c", "\u007e", "\u00ac", "\u00b1", "\u00d7", "\u00f7", "\u221b", "\u221c", "\u221d", "\u2220", "\u2223", "\u2224", "\u2225", "\u2226", "\u223c", "\u2243", "\u2244", "\u2245", "\u2248", "\u2249", "\u2261", "\u2264", "\u2265")
    val symbols = uppercaseGreek.union(lowercaseGreek).union(sets).union(quantifiers).union(logic).union(calculus).union(misc).union(arrows)
    val expressions = listOf("d/dx", ">0 ", ">=0 ", " large enough ", "dy/dx", "d{0}/dx", "2\u03c0", "lim {0}\u2192\u221e ", "/2", "/3", "f(x)", "g(x)")
    init {
        size = Toolkit.getDefaultToolkit().screenSize
        title = "MathProgram"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isResizable = true
        layout = null

        textBox.lineWrap = true
        textBox.size = size
        //textBox.isEditable = false
        add(textBox)
        textBox.document.addUndoableEditListener {
            UndoRedoUtils.undoStack.push(it.edit)
        }
        textBox.document.addDocumentListener(object: DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) {
                change(e!!)
            }

            override fun insertUpdate(e: DocumentEvent?) {
                change(e!!)
            }

            fun change(e: DocumentEvent) {
                if(greekLock.state && e.length == 1) {
                    val letter = textBox.text[e.offset]
                    if(letter.isLetter()) {
                        var replace = when(letter.toLowerCase()) {
                            'a' -> 'α'
                            'b' -> 'β'
                            'g' -> 'γ'
                            'd' -> 'ε'
                            'z' -> 'ζ'
                            'h' -> 'η'
                            't' -> 'θ'
                            'i' -> 'ι'
                            'k' -> 'κ'
                            'l' -> 'λ'
                            'm' -> 'μ'
                            'n' -> 'ν'
                            'x' -> 'ξ'
                            'o' -> 'ο'
                            'p' -> 'π'
                            'r' -> 'ρ'
                            's' -> 'σ'
                            //'t' -> 'τ'
                            'u' -> 'υ'
                            'f' -> 'φ'
                            'c' -> 'χ'
                            //'p' -> 'ψ'
                            //'o' -> 'ω'
                            else -> letter.toLowerCase()
                        }
                        replace = if(letter.isLowerCase()) replace.toLowerCase() else replace.toUpperCase()
                        val p1 = if(textBox.text.length > 1) textBox.text.substring(0, e.offset + e.length - 1) else ""
                        val p2 = replace
                        val p3 = if(textBox.text.length > e.offset + e.length) textBox.text.substring(e.offset + e.length) else ""
                        SwingUtilities.invokeLater { textBox.text = p1 + p2 + p3 }
                    }
                }
                val changes = mutableMapOf(
                        "->" to "→",
                        "infinity" to "∞",
                        "<->" to "↔",
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
                        "member of" to "\u2208"
                )
                for(change in changes.keys)
                    if(textBox.text.length >= change.length && textBox.text.substring(e.offset + e.length - change.length, e.offset + e.length).toLowerCase() == change) {
                        val p1 = if(textBox.text.length > change.length) textBox.text.substring(0, e.offset + e.length - change.length) else ""
                        val p2 = changes[change]
                        val p3 = if(textBox.text.length > e.offset + e.length) textBox.text.substring(e.offset + e.length) else ""
                        SwingUtilities.invokeLater { textBox.text = p1 + p2 + p3 }
                    }
            }

            override fun removeUpdate(e: DocumentEvent?) {
                //no-op
            }

        })
        textBox
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                textBox.size = this@MainScreen.size
            }
        })

        if (getFileMenu() != null) menuBar.add(getFileMenu())
        if (getEditMenu() != null) menuBar.add(getEditMenu())
        if (getMathMenu() != null) menuBar.add(getMathMenu())
        if (getOperationsMenu() != null) menuBar.add(getOperationsMenu())
        if (getSymbolsMenu() != null) menuBar.add(getSymbolsMenu())
        if (getCommonExpressionsMenu() != null) menuBar.add(getCommonExpressionsMenu())
        jMenuBar = menuBar
    }

    private fun getMenuItem(string: String): JMenuItem {
        val item = JMenuItem(string)
        item.addActionListener(this)
        item.actionCommand = string
        return item
    }

    fun getFileMenu(): JMenu? {
        val fileMenu = JMenu("File")
        return fileMenu
    }

    fun getEditMenu(): JMenu? {
        val editMenu = JMenu("Edit")
        val undo = getMenuItem("Undo")
        val redo = getMenuItem("Redo")
        val clear = getMenuItem("Clear")
        val lineBreak = getMenuItem("Line break")
        val space = getMenuItem("Space")
        editMenu.add(undo)
        editMenu.add(redo)
        editMenu.add(clear)
        editMenu.add(lineBreak)
        editMenu.add(space)
        editMenu.add(greekLock)
        return editMenu
    }

    fun getMathMenu(): JMenu? {
        val mathMenu = JMenu("Math")
        val evaluateExpression = getMenuItem("Evaluate expression")
        val limit = getMenuItem("Limit")
        val root = getMenuItem("Approximate root")
        val negate = getMenuItem("Negate")
        mathMenu.add(evaluateExpression)
        mathMenu.add(limit)
        mathMenu.add(root)
        mathMenu.add(negate)
        return mathMenu
    }

    fun getOperationsMenu(): JMenu? {
        val operationsMenu = JMenu("Operations")
        listOf("Let", "Choose", "Inductive hypothesis and base step", "Inductive step", "Induction complete", "Assumption for ex falso", "Contradiction from ex falso").map { getMenuItem(it) }.forEach { operationsMenu.add(it) }
        return operationsMenu
    }

    fun getSymbolsMenu(): JMenu? {
        val symbolsMenu = JMenu("Symbols")
        val greek = JMenu("Greek")
        val capital = JMenu("Uppercase")
        val lower = JMenu("Lowercase")
        val sets = JMenu("Sets")
        val quantifiers = JMenu("Quantifiers")
        val logic = JMenu("Logic")
        val calculus = JMenu("Calculus")
        val misc = JMenu("Misc.")
        val arrows = JMenu("Arrows")
        uppercaseGreek.map { getMenuItem(it) }.forEach { capital.add(it) }
        lowercaseGreek.map { getMenuItem(it) }.forEach { lower.add(it) }
        MainScreen.sets.map { getMenuItem(it) }.forEach { sets.add(it) }
        MainScreen.quantifiers.map { getMenuItem(it) }.forEach { quantifiers.add(it) }
        MainScreen.logic.map { getMenuItem(it) }.forEach { logic.add(it) }
        MainScreen.calculus.map { getMenuItem(it) }.forEach { calculus.add(it) }
        MainScreen.misc.map { getMenuItem(it) }.forEach { misc.add(it) }
        MainScreen.arrows.map { getMenuItem(it) }.forEach { arrows.add(it) }
        greek.add(capital)
        greek.add(lower)
        logic.add(arrows)
        symbolsMenu.add(greek)
        symbolsMenu.add(sets)
        symbolsMenu.add(quantifiers)
        symbolsMenu.add(logic)
        symbolsMenu.add(calculus)
        symbolsMenu.add(misc)
        symbolsMenu.add(lastUsed)
        return symbolsMenu
    }

    fun getCommonExpressionsMenu(): JMenu? {
        val expressionsMenu = JMenu("Common Expressions")
        expressions.map { getMenuItem(it) }.forEach { expressionsMenu.add(it) }
        return expressionsMenu
    }
}