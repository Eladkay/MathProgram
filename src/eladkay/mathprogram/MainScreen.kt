package eladkay.mathprogram

import java.awt.Toolkit
import java.awt.event.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

object MainScreen : JFrame() {

    internal val menuBar = JMenuBar()
    internal val textBox = MathTextBox()

    init {
        size = Toolkit.getDefaultToolkit().screenSize
        title = "MathProgram"
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isResizable = true
        layout = null

        //textBox.lineWrap = true
        textBox.size = size
        //textBox.tabSize = 4
        textBox.toolTipText = ""
        add(textBox)

        textBox.document.addUndoableEditListener {
            UndoRedoUtils.undoStack.push(it.edit)
        }
        textBox
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                textBox.size = this@MainScreen.size
            }
        })

        if (MenuBarHandler.getFileMenu() != null) menuBar.add(MenuBarHandler.getFileMenu())
        if (MenuBarHandler.getEditMenu() != null) menuBar.add(MenuBarHandler.getEditMenu())
        if (MenuBarHandler.getMathMenu() != null) menuBar.add(MenuBarHandler.getMathMenu())
        //if (getOperationsMenu() != null) menuBar.add(getOperationsMenu())
        if (MenuBarHandler.getSymbolsMenu() != null) menuBar.add(MenuBarHandler.getSymbolsMenu())
        if (MenuBarHandler.getCommonExpressionsMenu() != null) menuBar.add(MenuBarHandler.getCommonExpressionsMenu())
        jMenuBar = menuBar
    }

    internal fun getMenuItem(string: String): JMenuItem {
        val item = JMenuItem(string)
        item.addActionListener(MenuBarHandler)
        item.actionCommand = string
        return item
    }

}