package eladkay.mathprogram

import java.awt.Toolkit
import java.awt.event.*
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

object MainScreen : JFrame() {

    const val NAME = "MathProgram"
    const val VERSION = "0.1"
    const val HEADER = "---This file was created by $NAME, version $VERSION---\n"


    internal val menuBar = JMenuBar()
    internal val textBox = MathTextBox()
    val tempFile = File("_temp.mp")
    init {
        size = Toolkit.getDefaultToolkit().screenSize
        title = "$NAME $VERSION"
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isResizable = true
        layout = null
        addKeyListener(Listener)
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

        if(tempFile.exists()) textBox.text = tempFile.readText()
        else tempFile.createNewFile()
        
    }

    internal fun getMenuItem(string: String): JMenuItem {
        val item = JMenuItem(string)
        item.addActionListener(MenuBarHandler)
        item.actionCommand = string
        return item
    }

    internal fun saveText(string: String) {
        if(string.isEmpty()) return
        if(!tempFile.exists()) tempFile.createNewFile()
        tempFile.writeText(string)
    }

    var isCtrlDown = false
        private set
    var isAltDown = false
        private set
    var isInsertOn = false
        private set

    internal object Listener : KeyListener {
        override fun keyTyped(e: KeyEvent) {
            isCtrlDown = e.isControlDown
            isAltDown = e.isAltDown
            if(e.extendedKeyCode == KeyEvent.VK_INSERT) isInsertOn = !isInsertOn
        }

        override fun keyPressed(e: KeyEvent) {
            isCtrlDown = e.isControlDown
            isAltDown = e.isAltDown
            if(e.extendedKeyCode == KeyEvent.VK_INSERT) isInsertOn = !isInsertOn
        }

        override fun keyReleased(e: KeyEvent) {
            isCtrlDown = e.isControlDown
            isAltDown = e.isAltDown
        }

    }




}