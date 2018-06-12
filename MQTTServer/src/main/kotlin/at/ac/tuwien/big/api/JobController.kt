package at.ac.tuwien.big.api

import at.ac.tuwien.big.sm.Job
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

typealias ID = String

class JobController {

    private val jobs: MutableMap<ID, Job>

    init {
        val jobsText = this::class.java.classLoader.getResource("jobs.json").readText()
        val jobsType = object : TypeToken<List<Job>>() {}.type
        jobs = Gson().fromJson<List<Job>>(jobsText, jobsType).map { Pair(it.id, it) }.toMap().toMutableMap()
    }

    fun getJobs(): List<Job> {
        return jobs.values.toList()
    }

    fun getJob(id: String): Job? {
        return jobs[id]
    }

    fun setJob(job: Job) {
        jobs[job.id] = job
    }

    fun addJob(job: Job): ID {
        val uuid = generateID()
        jobs[uuid] = job
        return uuid
    }

    private fun generateID() = UUID.randomUUID().toString().take(16)
}
