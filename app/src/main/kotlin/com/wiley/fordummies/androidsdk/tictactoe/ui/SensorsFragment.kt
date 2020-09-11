package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Fragment for viewing device sensors.
 *
 * Created by adamcchampion on 2017/08/14.
 */
class SensorsFragment : Fragment(), SensorEventListener {
    private lateinit var mSensorRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var mAdapter: SensorAdapter
    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensorList: List<Sensor>
    private var lastSensorValues = Hashtable<String, FloatArray>()

    private val TOLERANCE = 10.0.toFloat()
    // private val TAG = SensorsFragment::class.java.simpleName


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_sensor_list, container, false)

		val activity = requireActivity()
        mSensorRecyclerView = v.findViewById(R.id.sensor_recycler_view)
        mSensorRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        mSensorManager = activity.getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL)
        mAdapter = SensorAdapter(mSensorList)
        mSensorRecyclerView.adapter = mAdapter
        mSensorRecyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        return v
    }

    override fun onResume() {
        super.onResume()
		val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            subtitle = resources.getString(R.string.sensors)
        }

        // Start listening to sensor updates
        for (sensor in mSensorList) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        Timber.d("Entering onPause")
        super.onPause()
        // Stop updates when paused
        mSensorManager.unregisterListener(this)
        Timber.d("Leaving onPause")
    }


    fun getSensorDescription(sensor: Sensor): String {
        return "Sensor: " + sensor.name + "; Ver :" + sensor.version + "; Range: " +
                sensor.maximumRange + "; Power: " + sensor.power + "; Res: " + sensor.resolution
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val sensorName = sensorEvent.sensor.name
        var lastValueString = "No previous value"
        val sensorEventString = sensorEventToString(sensorEvent)
        var percentageChange = 1000.0.toFloat() + TOLERANCE // Some value greater than tolerance
        var distanceOfLastValue = 0.0.toFloat()
        var distanceOfThisValue = 0.0.toFloat()
        var change = 0.0.toFloat()

        val lastValue = lastSensorValues[sensorName]
        lastSensorValues.remove(sensorName) // Hash table is "open" and can store multiple entries for the same key
        lastSensorValues[sensorName] = sensorEvent.values.clone() // update the value
        if (lastValue != null) {
            // Compute distance of new value, change and percentage change
            val builder = StringBuilder()
            distanceOfLastValue = 0.0.toFloat()
            for (i in sensorEvent.values.indices) {
                distanceOfLastValue += lastValue[i].toDouble().pow(2.0).toFloat()
                distanceOfThisValue += sensorEvent.values[i].toDouble().pow(2.0).toFloat()
                change += (sensorEvent.values[i] - lastValue[i]).toDouble().pow(2.0).toFloat()
                builder.append("   [")
                builder.append(i)
                builder.append("] = ")
                builder.append(lastValue[i])
            }
            lastValueString = builder.toString()
            change = sqrt(change.toDouble()).toFloat()
            distanceOfLastValue = sqrt(distanceOfLastValue.toDouble()).toFloat()
            distanceOfThisValue = sqrt(distanceOfThisValue.toDouble()).toFloat()

            percentageChange = when {
                distanceOfLastValue.toDouble() != 0.0 -> change * 100.0.toFloat() / distanceOfLastValue
                distanceOfThisValue.toDouble() != 0.0 -> change * 100.0.toFloat() / distanceOfThisValue
                else -> 0.0.toFloat()
            } // both distances are zero

        }
        val sensorTimberStr = "--- EVENT Raw Values ---\n" + sensorName + "\nDistance  Last= >" +
                distanceOfLastValue + "<\n" + "Distance  This= >" + distanceOfThisValue + "<\n" +
                "Change = >" + change + "<\n" + "Percent = >" + percentageChange + "%\n" +
                "Last value = " + lastValueString + "<\n" + sensorEventString
        Timber.d(sensorTimberStr)
        if (lastValue == null || percentageChange > TOLERANCE) {
            val percentTimberStr = sensorName + "\n--- Event Changed --- \nChange = >" + change + "<\n" +
                    "Percent = >" + percentageChange + "%\n" + sensorEventString
            Timber.d(percentTimberStr)
        }
    }

    private fun sensorEventToString(event: SensorEvent): String {
        val builder = StringBuilder()
        builder.append("Sensor: ")
        builder.append(event.sensor.name)
        builder.append("\nAccuracy: ")
        builder.append(event.accuracy)
        builder.append("\nTimestamp: ")
        builder.append(event.timestamp)
        builder.append("\nValues:\n")
        for (i in event.values.indices) {
            builder.append("   [")
            builder.append(i)
            builder.append("] = ")
            builder.append(event.values[i])
        }
        builder.append("\n")
        return builder.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {

    }

    private inner class SensorHolder(inflater: LayoutInflater, parent: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_sensor, parent, false)) {
        private lateinit var mSensor: Sensor
        private lateinit var mDescriptionStr: String
        private val mSensorInfoTextView: TextView = itemView.findViewById(R.id.sensor_data)

        fun bind(sensor: Sensor) {
            mSensor = sensor
            mDescriptionStr = getSensorDescription(sensor)
            mSensorInfoTextView.text = mDescriptionStr
        }
    }

    private inner class SensorAdapter(private val mSensorList: List<Sensor>) : androidx.recyclerview.widget.RecyclerView.Adapter<SensorHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorHolder {
            val inflater = LayoutInflater.from(activity)
            return SensorHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: SensorHolder, position: Int) {
            val sensor = this@SensorsFragment.mSensorList[position]
            holder.bind(sensor)
        }

        override fun getItemCount(): Int {
            return mSensorList.size
        }
    }
}
