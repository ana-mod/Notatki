package com.example.notes

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_paint_menu.view.*
import top.defaults.colorpicker.ColorPickerPopup


/**
 * A simple [Fragment] subclass.
 * Use the [PaintMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PaintMenuFragment : Fragment() {
    private var listener: PaintFragment? = null

    fun setPaintFragment(p: PaintFragment){
        listener = p
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thisView = inflater.inflate(R.layout.fragment_paint_menu, container, false)

        thisView.clearButton.setOnClickListener {
            listener?.clearCanvas()
        }
        thisView.colorButton.setOnClickListener {
            pickColor()
        }
        thisView.undoButton.setOnClickListener {
            listener?.undo()

        }
        thisView.eraseButton.setOnClickListener {
            listener?.switchToEraseMode()
        }

        thisView.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {listener?.switchToDrawingMode()}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener?.setBrushWidth(progress+2f)

            }

        })

        return thisView
    }



    private fun pickColor(){
        class ColorDialog : ColorPickerPopup.ColorPickerObserver() {
            override fun onColorPicked(color: Int) {
                listener?.setColor(color)
            }
        }
        val myColorDialog = ColorDialog()

        ColorPickerPopup.Builder(context)
            .initialColor(Color.RED)
            .enableBrightness(true)
            .okTitle("CONFIRM")
            .cancelTitle("CANCEL")
            .showValue(false)
            .build()
            .show(view, myColorDialog)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {

        private const val TAG = "PaintMenuFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PaintMenuFragment.
         */
        @JvmStatic
        fun newInstance() =
            PaintMenuFragment().apply {}
    }
}
