package eladkay.mathprogram

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.JCheckBoxMenuItem
import javax.swing.JFileChooser
import javax.swing.JFileChooser.APPROVE_OPTION
import javax.swing.JMenu
import javax.swing.JOptionPane

object MenuBarHandler : ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        if(!MainScreen.textBox.isEnabled) return
        var shouldCaps = MainScreen.textBox.text.endsWith("\n") || MainScreen.textBox.text.endsWith(". ") || MainScreen.textBox.text.isEmpty() || MainScreen.textBox.selectionStart == 0
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
                    lastUsed.add(MainScreen.getMenuItem(e.actionCommand))
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
                MainScreen.textBox.text = ""
            }
            "Assumption for ex falso" -> text = MathTextUtils.assumeForContradiction1()
            "Contradiction from ex falso" -> text = MathTextUtils.assumeForContradiction2()
            "Evaluate expression" -> {
                if(MainScreen.textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    if("x" in MainScreen.textBox.selectedText) {
                        MainScreen.textBox.isEnabled = false
                        val text2 = MainScreen.textBox.selectedText
                        NumberChooser(action = {
                            text = ExpressionUtils.evaluate(text2, it.toDouble()).toString()
                            MainScreen.textBox.replaceSelection(text[0].toUpperCase() + text.substring(1))
                            MainScreen.textBox.isEnabled = true
                        }) { MainScreen.textBox.isEnabled = true }.isVisible = true
                    } else {
                        text = ExpressionUtils.evaluate(MainScreen.textBox.selectedText, 0.0).toString()
                        shouldCaps = false
                    }
                }
            }
            "Limit" -> {
                if(MainScreen.textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    if("x" in MainScreen.textBox.selectedText) {
                        MainScreen.textBox.isEnabled = false
                        val text2 = MainScreen.textBox.selectedText
                        NumberChooser(action = {
                            text = ExpressionUtils.limit(text2, it.toDouble()).toString()
                            MainScreen.textBox.replaceSelection(text[0].toUpperCase() + text.substring(1))
                            MainScreen.textBox.isEnabled = true
                        }) { MainScreen.textBox.isEnabled = true }.isVisible = true
                    } else {
                        text = ExpressionUtils.limit(MainScreen.textBox.selectedText, 0.0).toString()
                        shouldCaps = false
                    }
                }
            }
            "Approximate root" -> {
                if(MainScreen.textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    if("x" in MainScreen.textBox.selectedText) {
                        MainScreen.textBox.isEnabled = false
                        val text2 = MainScreen.textBox.selectedText
                        NumberChooser(action = {
                            text = ExpressionUtils.approximateRoot(text2, it.toDouble()).toString()
                            MainScreen.textBox.replaceSelection(text[0].toUpperCase() + text.substring(1))
                            MainScreen.textBox.isEnabled = true
                        }) { MainScreen.textBox.isEnabled = true }.isVisible = true
                    } else {
                        if(MainScreen.textBox.selectedText.toDoubleOrNull() == 0.0) text = "every real x"
                        else JOptionPane.showMessageDialog(null, "No roots of constant function!")
                    }
                }
            }
            "Negate" -> {
                if(MainScreen.textBox.selectedText == null) JOptionPane.showMessageDialog(null, "No text is selected!")
                else {
                    text = ExpressionUtils.negate(MainScreen.textBox.selectedText)
                }
            }
            "Save..." -> {
                val result = fileChooser.showSaveDialog(MainScreen)
                if(result == APPROVE_OPTION) {
                    val fileSelected = fileChooser.selectedFile
                    val file = File(fileSelected.absolutePath + ".txt")
                    file.createNewFile()
                    file.writeText(MainScreen.HEADER + MainScreen.textBox.text)
                }
            }
            "Export..." -> {
                val result = fileChooser.showSaveDialog(MainScreen)
                if(result == APPROVE_OPTION) {
                    val fileSelected = fileChooser.selectedFile.absolutePath + ".pdf"
                    PdfUtils.export(fileSelected)
                }
            }
            else -> println(e.actionCommand)
        }
        if(text.isEmpty()) return
        if (shouldCaps) MainScreen.textBox.replaceSelection(text[0].toUpperCase() + text.substring(1)) else MainScreen.textBox.replaceSelection(text)

    }
    val greekLock = JCheckBoxMenuItem("Greek lock")
    val lastUsed = JMenu("Last used")
    val fileChooser = JFileChooser()
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

    fun getFileMenu(): JMenu? {
        val fileMenu = JMenu("File")
        val save = MainScreen.getMenuItem("Save...")
        val export = MainScreen.getMenuItem("Export...")
        fileMenu.add(save)
        fileMenu.add(export)
        return fileMenu
    }

    fun getEditMenu(): JMenu? {
        val editMenu = JMenu("Edit")
        val undo = MainScreen.getMenuItem("Undo")
        val redo = MainScreen.getMenuItem("Redo")
        val clear = MainScreen.getMenuItem("Clear")
        val lineBreak = MainScreen.getMenuItem("Line break")
        val space = MainScreen.getMenuItem("Space")
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
        val evaluateExpression = MainScreen.getMenuItem("Evaluate expression")
//        val limit = getMenuItem("Limit")
//        val root = getMenuItem("Approximate root")
//        val negate = getMenuItem("Negate")
        mathMenu.add(evaluateExpression)
//        mathMenu.add(limit)
//        mathMenu.add(root)
//        mathMenu.add(negate)
        return mathMenu
    }

    fun getOperationsMenu(): JMenu? {
        val operationsMenu = JMenu("Operations")
        listOf("Let", "Choose", "Inductive hypothesis and base step", "Inductive step", "Induction complete", "Assumption for ex falso", "Contradiction from ex falso").map { MainScreen.getMenuItem(it) }.forEach { operationsMenu.add(it) }
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
        uppercaseGreek.map { MainScreen.getMenuItem(it) }.forEach { capital.add(it) }
        lowercaseGreek.map { MainScreen.getMenuItem(it) }.forEach { lower.add(it) }
        this.sets.map { MainScreen.getMenuItem(it) }.forEach { sets.add(it) }
        this.quantifiers.map { MainScreen.getMenuItem(it) }.forEach { quantifiers.add(it) }
        this.logic.map { MainScreen.getMenuItem(it) }.forEach { logic.add(it) }
        this.calculus.map { MainScreen.getMenuItem(it) }.forEach { calculus.add(it) }
        this.misc.map { MainScreen.getMenuItem(it) }.forEach { misc.add(it) }
        this.arrows.map { MainScreen.getMenuItem(it) }.forEach { arrows.add(it) }
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
        expressions.map { MainScreen.getMenuItem(it) }.forEach { expressionsMenu.add(it) }
        return expressionsMenu
    }

}