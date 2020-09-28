package eladkay.mathprogram

import java.awt.Toolkit
import java.awt.event.*
import java.io.File
import javax.swing.*

object MainScreen : JFrame() {

    const val NAME = "MathProgram"
    const val VERSION = "0.1"
    const val HEADER = "---This file was created by $NAME, version $VERSION---\n"
    internal const val DEBUG = true

    internal val menuBar = JMenuBar()
    internal val textBox = MathTextBox()
    val tempFile = File("_temp.mp")

    private val SMALL_METADATA_REGEX_1: Regex
    //"(.*?)(?:\\{sk(\\d)/(\\d)}(.*?)\\{sv(\\d)/(\\d)}(.*?))*(.*?)".toRegex()
    private val SMALL_METADATA_REGEX_2: Regex


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

        SMALL_METADATA_REGEX_1 = "(.*?)(\\{sk(?:\\d+)/(?:\\d+)}(?:.*?)\\{sv(?:\\d+)/(?:\\d+)})*(.*?)".toRegex()
        SMALL_METADATA_REGEX_2 = "\\{sk(\\d+)/(\\d+)}(.*?)\\{sv(\\d+)/(\\d+)}".toRegex()

        if(tempFile.exists()) {
            textBox.text = readFromMetadata(tempFile.readText())
        }
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
        tempFile.writeText(addMetadata(string))
    }

    // Format: Let ([a, b], [c, d]) be a member of smallMap with index i. Then, before index a in the string, we add
    // {sk$i/${b-a+1}} and before index c we add {sv$i/${c-d+1}}
    internal fun addMetadata(string: String): String {
        val strings = mutableMapOf<Int, String>()
        for ((index, entry) in textBox.smallMap.entries.withIndex()) {
            val (key, value) = entry
            val metaForKey = "{sk$index/${key.last - key.first + 1}}"
            val metaForValue = "{sv$index/${value.last - value.first + 1}}"
            strings[key.first] = metaForKey
            strings[value.first] = metaForValue
        }
        return appendAt(string, strings)
    }

    private fun appendAt(toAppend: String, strings: Map<Int, String>): String {
        if(strings.isEmpty() || toAppend.isEmpty()) return toAppend
        val builder = StringBuilder()
        val keysSorted = strings.keys.sorted()
        builder.append(toAppend.substring(0 until keysSorted.first()))
        for((index, key) in keysSorted.withIndex()) {
            val value = strings[key]!!
            builder.append(value)
            if(index != keysSorted.size - 1) builder.append(toAppend.substring(key until keysSorted[index + 1]))
        }
        builder.append(toAppend.substring(keysSorted.last() until toAppend.length))
        return builder.toString()
    }

    private val regexForTesting = "\\{sk(\\d+)/(\\d+)}(.*?)\\{sv(\\d+)/(\\d+)}".toRegex()
    private fun readFromMetadata(input: String): String {
        val matchResult = SMALL_METADATA_REGEX_1.matchEntire(input) ?: error(input)
        val builder = StringBuilder()
        builder.append(matchResult.groupValues[1])
        val groups = matchResult.groups.toMutableList()

        groups.removeFirst() // entire match
        groups.removeFirst() // prefix
        groups.removeLast() // suffix
        val newSmallMap = mutableMapOf<IntRange, IntRange>()
        for(group in groups) {
            if(group == null || group.value.isEmpty()) continue
            val newOffset = group.range.first
            val match = SMALL_METADATA_REGEX_2.matchEntire(group.value)
            val index1 = (match ?: error(group.value)).groupValues[1]
            val length1 = match.groupValues[2]
            val text = match.groupValues[3]
            builder.append(text)
            val index2 = match.groupValues[4]
            val length2 = match.groupValues[5]
            @Suppress("ControlFlowWithEmptyBody")
            if(index1 == index2) {
                // {sk($i1)/($l1)}($text)}
                // handle deserialization if needed, not needed here
            }
        }
        builder.append(matchResult.groupValues.last())
        return builder.toString()
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
            if(DEBUG && isAltDown) textBox.debug()
        }

        override fun keyPressed(e: KeyEvent) {
            isCtrlDown = e.isControlDown
            isAltDown = e.isAltDown
            if(e.extendedKeyCode == KeyEvent.VK_INSERT) isInsertOn = !isInsertOn
            if(DEBUG && isAltDown) textBox.debug()
        }

        override fun keyReleased(e: KeyEvent) {
            isCtrlDown = e.isControlDown
            isAltDown = e.isAltDown
            if(DEBUG && isAltDown) textBox.debug()
        }

    }




}