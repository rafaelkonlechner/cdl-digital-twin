package at.ac.tuwien.big.api

import at.ac.tuwien.big.RuntimeTypeAdapterFactory
import at.ac.tuwien.big.sm.BasicState
import at.ac.tuwien.big.sm.ChoiceState
import at.ac.tuwien.big.sm.Job
import at.ac.tuwien.big.sm.StateBase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*


typealias ID = String

class JobController {

    private val jobs: MutableMap<ID, Job>
    var selected: Job
        private set

    init {
        val jobsText = this::class.java.classLoader.getResource("jobs.json").readText()
        val jobsType = object : TypeToken<List<Job>>() {}.type
        val gson = GsonBuilder()
                .registerTypeAdapterFactory(
                        RuntimeTypeAdapterFactory.of(StateBase::class.java, "type")
                                .registerSubtype(BasicState::class.java, "BasicState")
                                .registerSubtype(ChoiceState::class.java, "ChoiceState")
                )
                .create()
        jobs = gson.fromJson<List<Job>>(jobsText, jobsType).map { Pair(it.id, it) }.toMap().toMutableMap()
        selected = jobs.values.first()
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
        jobs[uuid] = job.copy(id = uuid)
        return uuid
    }

    fun setSelectedJob(id: String) {
        selected = jobs[id]!!
    }

    private fun generateID() = UUID.randomUUID().toString().take(8)
}
