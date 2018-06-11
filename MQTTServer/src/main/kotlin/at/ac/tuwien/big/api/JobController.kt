package at.ac.tuwien.big.api

import at.ac.tuwien.big.sm.Job
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JobController {

    private val jobs: MutableList<Job>

    init {
        val jobsText = this::class.java.classLoader.getResource("jobs.json").readText()
        val jobsType = object : TypeToken<List<Job>>() {}.type
        jobs = Gson().fromJson<List<Job>>(jobsText, jobsType).toMutableList()
    }

    fun getJobs(): List<Job> {
        return jobs
    }
}
