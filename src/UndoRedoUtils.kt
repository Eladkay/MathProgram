
import java.util.*

object UndoRedoUtils {
    enum class ActionType { INSERT, REMOVE }
    data class Action(val text: String, val start: Int, val end: Int, val type: ActionType)
    val undoStack = Stack<Action>()
    val redoStack = Stack<Action>()
    fun action(text: String, start: Int, end: Int, type: ActionType) = undoStack.push(Action(text, start, end, type))
    fun action(action: Action) = undoStack.push(action)
    fun undoAction(): Action? {
        val last = try {
            undoStack.pop()
        } catch(e: EmptyStackException) {
            return null
        }
        val ret = if(last.type == ActionType.REMOVE) last.copy(type = ActionType.INSERT) else last.copy(type = ActionType.REMOVE)
        redoStack.push(last)
        return ret
    }
    fun redoAction(): Action? {
        return try {
            val ret = redoStack.pop()
            action(ret)
            ret
        } catch (e: EmptyStackException) {
            null
        }
    }
}