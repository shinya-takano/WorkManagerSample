package sample.my.workmanagersample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            Log.d("MainActivity", "button.setOnClickListener")

            // Create a Constraints object that defines when the task should run
            val constraints = Constraints.Builder()
                // .setRequiresDeviceIdle(true)
                // .setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val data = Data.Builder().putString("input_key_1", "input_data").build()

            // ...then create a OneTimeWorkRequest that uses those constraints
            val work = OneTimeWorkRequestBuilder<PrintlnWorker>()
                .setConstraints(constraints)
                .setInputData(data)
                .build()
            WorkManager.getInstance(this).enqueue(work)

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(work.id)
                .observe(this, Observer { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        textView.text = "Work finished!"
                    }
                })
        }

        button2.setOnClickListener {

            Log.d("MainActivity", "button2.setOnClickListener")

            val data = Data.Builder().putString("input_key_1", "input_data").build()

            val constraints = Constraints.Builder()
                // .setRequiresCharging(true)
                .build()

            // ...then create a OneTimeWorkRequest that uses those constraints
            val work = PeriodicWorkRequestBuilder<PrintlnWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(data)
                .build()

            WorkManager.getInstance(this).enqueue(work)

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(work.id)
                .observe(this, Observer { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        textView.text = "PeriodicWork finished!"
                    }
                })
        }
    }
}

class PrintlnWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        // Get the input
        val inputKey1 = inputData.getString("input_key_1")

        // validate inputs.
        val checkedInput = checkNotNull(inputKey1) {
            Log.d("PrintlnWorker", "doWork input_key_1=$")
            return Result.failure()
        }

        // Do the work
        Log.d("PrintlnWorker", "doWork Hello!")

        // Create the output of the work

        val outputData = Data.Builder()
            .putInt("output_key_1", 1)
            .build()

        // Return the output
        return Result.success(outputData)

    }
}
