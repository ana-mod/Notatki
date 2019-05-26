package com.example.notes


import android.content.Context

import android.graphics.*

import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.Json


class PaintView(context: Context?, attrs: AttributeSet?) : View(context, attrs){
    /*
    Things to save:

    brushColor:Int
    brushWidth:Float
    lowestY:Int
    Paths : Text (zapis w formacie JSON)
     */

    companion object {
        private const val TAG = "PaintView"
    }

    private var drawingMode=true



    private var scale = 1f
    private var drawPath = SerializablePath()

    private var lowestY = 0f
    private var paths: ArrayList<Pair<SerializablePath, Paint>> = ArrayList()
    private val background = Paint(Paint.ANTI_ALIAS_FLAG)
    private val brush = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        background.setARGB(255, 255,255,255)
        background.strokeWidth = 50f
        background.style = Paint.Style.STROKE


        brush.setARGB(255, 0,0,255)
        brush.strokeWidth = 2f
        brush.textSize = 40f
        brush.style = Paint.Style.STROKE

    }

    @Serializable
    data class Data(val a: Int, val b: Int)



    @ImplicitReflectionSerializer
    fun getPathsJSON():String{
        //val p = SerializablePath.MyPair(1f,2f)
        val data = Data(1, 2)
        val t = Json.stringify(data)


        Log.d(TAG, t)
        return ""
    }

    fun setPathsJSON(s:String){


        paths = ArrayList()
    }

    fun getLowestY():Float{
        return lowestY
    }


    fun setLowestY(y:Float){
        lowestY = y
    }

    fun getBrushWidth():Float{
        return brush.strokeWidth
    }

    fun setBrushWidth(w:Float){
        brush.strokeWidth = w
        invalidate()
    }

    fun setColor(color:Int){
        brush.color = color
        invalidate()
    }

    fun getColor():Int{
        return brush.color
    }




    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, width+lowestY.toInt())
        scale = width/720f
        Log.d(TAG, " No dobra  w: $width;h:$height")
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null) return
        canvas.save()
        canvas.translate(0f, 0f)
        canvas.scale(scale, scale)
        canvas.drawPaint(background)

        paths.forEach{
            canvas.drawPath(it.first, it.second)
        }

        if(drawingMode)
            canvas.drawPath(drawPath, brush)
        else
            canvas.drawPath(drawPath, background)

        canvas.restore()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchedX = event.x/scale
        val touchedY = event.y/scale

        when(event.action){
            MotionEvent.ACTION_DOWN -> drawPath.moveTo(touchedX, touchedY)
            MotionEvent.ACTION_MOVE -> drawPath.lineTo(touchedX, touchedY)
            MotionEvent.ACTION_UP -> {
                drawPath.setLastPoint(touchedX, touchedY)
                val c:Paint
                if(drawingMode){
                    c = Paint()
                    c.set(brush)
                }
                else{
                    c = background
                }

                paths.add(Pair(drawPath, c))
                drawPath = SerializablePath()
            }
            MotionEvent.ACTION_CANCEL -> drawPath.reset()
            MotionEvent.ACTION_OUTSIDE -> drawPath.reset()
        }

        if(lowestY<touchedY)
        {
            lowestY=touchedY
            requestLayout()
        }

        invalidate()

        return true
    }


    fun clearCanvas() {
        paths.clear()
        lowestY=0f
        requestLayout()
    }



    fun switchToEraseMode(){
        drawingMode=false
    }

    fun switchToDrawingMode(){
        drawingMode=true
    }

    fun undo(){
        if(paths.size>0) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    @Serializable
    private class SerializablePath : Path() {
        @Serializable
        class MyPair(val first:Float, val second:Float)
        {
            @Serializer(forClass = MyPair::class)
            companion object : KSerializer<MyPair>{
                override val descriptor: SerialDescriptor = StringDescriptor.withName("MyPair")

                override fun deserialize(decoder: Decoder): MyPair {
                    val f = decoder.decodeFloat()
                    val s = decoder.decodeFloat()
                    return MyPair(f,s)
                }

                override fun serialize(encoder: Encoder, obj: MyPair) {
                    encoder.encodeFloat(obj.first)
                    encoder.encodeFloat(obj.second)
                }

            }
        }

        var movesTypes = ArrayList<Int>()
        var xyArray = ArrayList<MyPair>()

        override fun lineTo(x: Float, y: Float) {
            movesTypes.add(0)
            xyArray.add(MyPair(x,y))
            super.lineTo(x, y)
        }

        override fun moveTo(x: Float, y: Float) {
            movesTypes.add(1)
            xyArray.add(MyPair(x,y))
            super.moveTo(x, y)
        }

        override fun setLastPoint(dx: Float, dy: Float) {
            movesTypes.add(2)
            xyArray.add(MyPair(dx,dy))
            super.setLastPoint(dx, dy)
        }




        /*@Serializer(forClass = SerializablePath::class)
        companion object :KSerializer<SerializablePath>
        {
            override val descriptor: SerialDescriptor =
                StringDescriptor.withName("SerializablePath")

            override fun deserialize(decoder: Decoder): SerializablePath {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun serialize(encoder: Encoder, obj: SerializablePath) {
                //encoder.
            }

        }*/

        /*private class SerializablePathSerializer : JsonSerializer<SerializablePath> {
            override fun serialize(src: SerializablePath, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {

                val types = JSONArray(src.movesTypes)
                val xy = JSONArray(src.xyArray)
                val o = JsonObject()

                o.add("types", types as JsonElement)
               // o.add("movesTypes", types.)


                return o
            }
        }

        private inner class SerializablePathDeserializer : JsonDeserializer<SerializablePath> {
            @Throws(JsonParseException::class)
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SerializablePath {
                val p = SerializablePath()
                //p.xyArray = json.asJsonObject.getAsJsonArray()

                json.asJsonPrimitive.asString
                return SerializablePath()
            }
        }*/


    }

    /*@Serializer(forClass = Pair::class)
    object PairSerializer: KSerializer<Pair<Float, Float>> {

        override val descriptor: SerialDescriptor =
            StringDescriptor.withName("PairSerializer")

        override fun serialize(output: Encoder, obj: Pair<Float,Float>) {
            output.encodeFloat(obj.first)
            output.encodeFloat(obj.second)
        }

        override fun deserialize(input: Decoder): Pair<Float,Float> {
            val first = input.decodeFloat()
            val second = input.decodeFloat()
            return Pair(first,second)
        }
    }*/


}

