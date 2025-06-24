package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.util.Hashtable
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

@Keep
class SensorsFragment : Fragment(), SensorEventListener {
	private lateinit var mSensorManager: SensorManager
	private lateinit var mSensorList: List<Sensor>
	private val lastSensorValues = Hashtable<String, FloatArray>()
	private val classTag = javaClass.simpleName

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val v = inflater.inflate(R.layout.fragment_sensor_table_list, container, false)
		val activity: Activity = requireActivity()
		val sensorRecyclerView = v.findViewById<RecyclerView>(R.id.sensor_recyclerview_table)
		sensorRecyclerView.layoutManager = LinearLayoutManager(activity)
		mSensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
		mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL)
		val adapter = SensorAdapter(mSensorList)
		sensorRecyclerView.adapter = adapter
		sensorRecyclerView.itemAnimator = DefaultItemAnimator()
		return v
	}

	override fun onResume() {
		super.onResume()
		Timber.tag(classTag).d("onResume()")
		try {
			val activity = requireActivity() as AppCompatActivity
			val actionBar = activity.supportActionBar
			if (actionBar != null) {
				actionBar.subtitle = resources.getString(R.string.sensors)
			}
		} catch (npe: NullPointerException) {
			Timber.tag(classTag).e("Could not set subtitle")
		}

		// Start listening to sensor updates
		for (sensor in mSensorList) {
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
		}
	}

	override fun onPause() {
		Timber.tag(classTag).d("Entering onPause()")
		super.onPause()
		// Stop updates when paused
		for (sensor in mSensorList) {
			mSensorManager.unregisterListener(this, sensor)
		}
		Timber.tag(classTag).d("Leaving onPause()")
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
			percentageChange = if (distanceOfLastValue.toDouble() != 0.0) {
				change * 100.0.toFloat() / distanceOfLastValue
			} else if (distanceOfThisValue.toDouble() != 0.0) {
				change * 100.0.toFloat() / distanceOfThisValue
			} else {
				0.0.toFloat() // both distances are zero
			}
		}
		Timber.tag(classTag).d(
			"EVENT: Raw Values: %s; Distance Last: %f; Distance This: %f; Change: %f; Percent: %f%%; Last value: %s; %s",
			sensorName, distanceOfLastValue, distanceOfThisValue, change, percentageChange,
			lastValueString, sensorEventString
		)
		if (lastValue == null || percentageChange > TOLERANCE) {
			Timber.tag(classTag).d(
				"--- Event Changed --- : Change: %f; Percent: %f%%; %s",
				change, percentageChange, sensorEventString
			)
		}
	}

	private fun sensorEventToString(event: SensorEvent): String {
		val builder = StringBuilder()
		builder.append("Sensor: ")
		builder.append(event.sensor.name)
		builder.append("; Accuracy: ")
		builder.append(event.accuracy)
		builder.append("; Timestamp: ")
		builder.append(event.timestamp)
		builder.append("; Values: ")
		for (i in event.values.indices) {
			builder.append("   [")
			builder.append(i)
			builder.append("] = ")
			builder.append(event.values[i])
		}
		return builder.toString()
	}

	override fun onAccuracyChanged(sensor: Sensor, i: Int) {
		Timber.tag(classTag).d("onAccuracyChanged(): Accuracy of %s changed to %d", sensor.toString(), i)
	}

	private class SensorHolder(inflater: LayoutInflater, parent: ViewGroup?) :
		RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_sensor, parent, false)) {
		private val mSensorNameTv: TextView = itemView.findViewById(R.id.sensor_name)
		private val mSensorVersionTv: TextView = itemView.findViewById(R.id.sensor_version)
		private val mSensorRangeTv: TextView = itemView.findViewById(R.id.sensor_range)
		private val mSensorPowerTv: TextView = itemView.findViewById(R.id.sensor_power)
		private val mSensorResTv: TextView = itemView.findViewById(R.id.sensor_res)

		fun bind(sensor: Sensor) {
			val name = String.format(Locale.getDefault(), "%-34s", sensor.name.trim { it <= ' ' })
			mSensorNameTv.text = name
			mSensorVersionTv.text = String.format(Locale.getDefault(), "%,d", sensor.version)
			mSensorRangeTv.text = String.format(Locale.getDefault(), "%.6g", sensor.maximumRange)
			mSensorPowerTv.text = String.format(Locale.getDefault(), "%.6f", sensor.power)
			mSensorResTv.text = String.format(Locale.getDefault(), "%.6f", sensor.resolution)
		}

	}

	private inner class SensorAdapter(private val mSensorList: List<Sensor>) :
		RecyclerView.Adapter<SensorHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorHolder {
			val inflater = LayoutInflater.from(requireActivity())
			return SensorHolder(inflater, parent)
		}

		override fun onBindViewHolder(holder: SensorHolder, position: Int) {
			val sensor = mSensorList[position]
			holder.bind(sensor)
		}

		override fun getItemCount(): Int {
			return mSensorList.size
		}
	}

	companion object {
		private const val TOLERANCE = 10.0.toFloat()
	}
}
