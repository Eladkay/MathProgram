package eladkay.mathprogram

import java.awt.Dimension
import java.awt.Font
import java.awt.Font.BOLD
import java.awt.Toolkit
import java.awt.event.*
import javax.swing.*

class NumberChooser(val action: (Number)->Unit, val onClose: ()->Unit = {}) : JFrame(), ActionListener, WindowListener {
    override fun windowDeiconified(e: WindowEvent?) {}

    override fun windowClosing(e: WindowEvent?) {
        onClose()
    }

    override fun windowClosed(e: WindowEvent?) {}

    override fun windowActivated(e: WindowEvent?) {}

    override fun windowDeactivated(e: WindowEvent?) {}

    override fun windowOpened(e: WindowEvent?) {}

    override fun windowIconified(e: WindowEvent?) {}

    val spinner = JSpinner(SpinnerNumberModel(0, 0, 1000000, 0.1))
    val okButton = JButton("Ok")

    init {
        size = Dimension(Toolkit.getDefaultToolkit().screenSize.width / 5, Toolkit.getDefaultToolkit().screenSize.height / 3)
        spinner.setBounds(0, 0, size.width, size.height * 3/4)
        okButton.mnemonic = KeyEvent.VK_S
        okButton.actionCommand = "ok"
        okButton.setBounds(0, size.height * 3/4, size.width, size.height * 1/4)
        okButton.addActionListener(this)
        add(okButton)
        add(spinner)
        this.addWindowListener(this)
        isResizable = false
    }

    override fun actionPerformed(e: ActionEvent?) {
        when (e!!.actionCommand) {
            "ok" -> {
                this.isVisible = false
                action(spinner.value as Number)
                spinner.value = 0
            }
        }
    }



}
