import java.util.*
import javax.swing.undo.UndoableEdit

object UndoRedoUtils2 {
    val undoStack = Stack<UndoableEdit>()
    val redoStack = Stack<UndoableEdit>()
    fun undoAction() {
        val last = try {
            undoStack.pop()
        } catch(e: EmptyStackException) {
            return
        }
        last.undo()
        redoStack.push(last)
    }
    fun redoAction() {
        try {
            val ret = redoStack.pop()
            ret.redo()
        } catch (e: EmptyStackException) {}
    }
}