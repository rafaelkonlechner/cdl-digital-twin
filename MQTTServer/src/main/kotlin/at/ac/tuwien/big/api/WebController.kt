package at.ac.tuwien.big.api

import at.ac.tuwien.big.*
import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.*
import at.ac.tuwien.big.sm.BasicState
import at.ac.tuwien.big.sm.ChoiceState
import at.ac.tuwien.big.sm.Job
import at.ac.tuwien.big.sm.StateBase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.javalin.ApiBuilder.*
import io.javalin.Javalin
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.runBlocking
import org.apache.commons.io.IOUtils
import org.eclipse.jetty.websocket.api.Session
import java.io.StringWriter

/**
 * Controller for web-based APIs: Static frontend, HTTP API and WebSocket
 */
class WebController(private val mqtt: MQTT,
                    private val messageController: MessageController,
                    private val jobController: JobController,
                    private val timeSeriesDatabase: TimeSeriesDatabase) {

    data class TopicMessage(val topic: String, val message: String)

    private val channel = Channel<TopicMessage>(Channel.UNLIMITED)
    private var app: Javalin = Javalin.create()

    private var session: Session? = null
    private val gson: Gson = GsonBuilder()
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory.of(StateBase::class.java, "type")
                            .registerSubtype(BasicState::class.java, "BasicState")
                            .registerSubtype(ChoiceState::class.java, "ChoiceState")
            )
            .create()

    init {
        messageController.subscribe(this::pushToWebSocket)
        app.enableCorsForOrigin("http://127.0.0.1:8081", "http://localhost:8081")
                .port(8080)
                .enableStaticFiles("/static")
        app.ws("/websocket") { ws ->
            ws.onConnect { session ->
                this.session = session
                println("Opened $session")
            }
            ws.onMessage { _, message ->
                println("Received: $message")
                if (message != null) {
                    println("Message: $message")
                    mqtt.send(message)
                }
            }
            ws.onClose { session, statusCode, reason -> println("Closed $session, $statusCode: $reason") }
            ws.onError { session, throwable -> println("Error: $session, $throwable") }
        }
        setupRoutes()
    }

    /**
     * Start forwarding messages to clients that arrive via [pushToWebSocket]
     */
    fun start() {
        app.start()
        while (true) {
            runBlocking {
                val data = channel.receive()
                if (session?.isOpen == true) {
                    session?.remote?.sendString(gson.toJson(data))
                }
            }
        }
    }

    /**
     * Stop all web-based services services
     */
    fun stop() {
        app.stop()
    }

    /**
     * Push message to WebSocket
     */
    private fun pushToWebSocket(topic: String, message: String) {
        runBlocking {
            channel.send(TopicMessage(topic, message))
        }
    }

    /**
     * Definition of HTTP API
     */
    private fun setupRoutes() {
        app.routes {
            /**
             * Set the message rate per second. The message rate defines, how many sensor updates the controllers
             * (i.e. Blender, Hedgehog) send per second.
             */
            put("/messageRate") { ctx -> messageController.messageRate = ctx.body().toInt() }

            /**
             * Get the message rate in messages per second
             */
            get("/messageRate") { messageController.messageRate }

            /**
             * Returns, whether the current job is running or not.
             */
            get("/autoPlay") { ctx -> ctx.json(messageController.autoPlay) }

            /**
             * Start or stop the selected job at its current position (`true` for start, `false` for stop)
             */
            put("/autoPlay") { ctx -> messageController.autoPlay = ctx.body().toBoolean() }

            /**
             * Returns, whether recording sensor data in the time-series database is activated
             */
            get("/recording") { ctx -> ctx.json(messageController.recording) }

            /**
             * Start or stop recording sensor data in the time-series database (`true` for start, `false` for stop)
             */
            put("/recording") { ctx -> messageController.recording = ctx.body().toBoolean() }

            /**
             * Reset the time-series database
             */
            put("/resetData") { timeSeriesDatabase.resetDatabase() }

            /**
             * Move the system to the position defined in the provided environment
             */
            post("/moveEnvironment") { ctx ->
                run {
                    val env = gson.fromJson(ctx.body(), Environment::class.java)
                    val commands = mutableListOf<String>();
                    if (env.roboticArmState != null) {
                        commands.addAll(StateObserver.transform(RoboticArmTransition(RoboticArmState(), env.roboticArmState)))
                    }
                    if (env.conveyorState != null) {
                        commands.addAll(StateObserver.transform(ConveyorTransition(ConveyorState(), env.conveyorState)))
                    }
                    if (env.sliderState != null) {
                        commands.addAll(StateObserver.transform(SliderTransition(SliderState(), env.sliderState)))
                    }
                    if (env.testingRigState != null) {
                        commands.addAll(StateObserver.transform(TestingRigTransition(TestingRigState(), env.testingRigState)))
                    }
                    for (c in commands) {
                        mqtt.send(c)
                    }
                }
            }

            /**
             * Get all states
             */
            get("/all") { ctx -> ctx.json(StateObserver.stateMachine?.states!!) }

            /**
             * Get the current robotic arm state
             */
            get("/roboticArmState") { ctx -> ctx.json(StateObserver.latestMatch.first.environment.roboticArmState!!) }

            /**
             * Get the current slider state
             */
            get("/sliderState") { ctx -> ctx.json(StateObserver.latestMatch.first.environment.sliderState!!) }

            /**
             * Get the current conveyor state
             */
            get("/conveyorState") { ctx -> ctx.json(StateObserver.latestMatch.first.environment.conveyorState!!) }

            /**
             * Get the current testing rig state
             */
            get("/testingRigState") { ctx -> ctx.json(StateObserver.latestMatch.first.environment.testingRigState!!) }

            /**
             * Set the current robotic arm state
             */
            put("/roboticArmState") { ctx ->
                run {
                    val match = StateObserver.stateMachine?.all()?.find { it.name == ctx.body() }
                    if (match != null)
                        send(RoboticArmTransition(RoboticArmState(), match.environment.roboticArmState!!))
                }
            }

            /**
             * Set the current slider state
             */
            put("/sliderState") { ctx ->
                run {
                    val match = StateObserver.stateMachine?.all()?.find { it.name == ctx.body() }
                    if (match != null)
                        send(SliderTransition(SliderState(), match.environment.sliderState!!))
                }
            }

            /**
             * Set the current conveyor state
             */
            put("/conveyorState") { ctx ->
                run {
                    val match = StateObserver.stateMachine?.all()?.find { it.name == ctx.body() }
                    if (match != null)
                        send(ConveyorTransition(ConveyorState(), match.environment.conveyorState!!))
                }
            }

            /**
             * Set urn the current testing rig state
             */
            put("/testingRigState") { ctx ->
                run {
                    val match = StateObserver.stateMachine?.all()?.find { it.name == ctx.body() }
                    if (match != null)
                        send(TestingRigTransition(TestingRigState(), match.environment.testingRigState!!))
                }
            }

            /**
             * Return the list of jobs
             */
            get("/jobs") { ctx -> ctx.json(jobController.getJobs()) }

            /**
             * Return a job, given its job id
             */
            get("/jobs/:id") { ctx ->
                run {
                    val id = ctx.param("id") ?: ""
                    ctx.json(jobController.getJob(id) ?: Any())
                }
            }

            /**
             * Update an existing job
             */
            put("/jobs/:id") { ctx ->
                run {
                    try {
                        val id = ctx.param("id") ?: ""
                        val job = jobController.getJob(id)
                        if (job != null) {
                            val newJob = gson.fromJson(ctx.body(), Job::class.java)
                            jobController.setJob(newJob)
                            StateObserver.stateMachine = StateMachine(newJob.states)
                            ctx.status(200)
                        } else {
                            ctx.status(404)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            /**
             * Upload a new job
             * @return a generated id for the job
             */
            post("/jobs") { ctx ->
                run {
                    val newJob = gson.fromJson(ctx.body(), Job::class.java)
                    val id = jobController.addJob(newJob)
                    StateObserver.stateMachine = StateMachine(newJob.states)
                    ctx.json(id)
                }
            }

            /**
             * Upload a job file in JSON format
             */
            post("/jobFile") { ctx ->
                run {
                    val writer = StringWriter()
                    IOUtils.copy(ctx.uploadedFile("job")?.content, writer)
                    val newJob = gson.fromJson(writer.toString(), Job::class.java)
                    val id = jobController.addJob(newJob)
                    StateObserver.stateMachine = StateMachine(newJob.states)
                    ctx.json(id)
                }
            }

            /**
             * Set a new currently selected job
             */
            put("/selectedJob") { ctx ->
                run {
                    jobController.setSelectedJob(ctx.body())
                    messageController.autoPlay = false
                    StateObserver.stateMachine = StateMachine(jobController.selected.states)
                }
            }

            /**
             * Return the currently selected job
             */
            get("/selectedJob") { ctx ->
                ctx.json(jobController.selected)
            }

            /**
             * Reset the system to initial state
             */
            put("/reset") { ctx ->
                run {
                    messageController.reset()
                    ctx.status(200)
                }
            }
        }
    }

    private fun send(match: Transition?) {
        if (match != null) {
            val commands = StateObserver.transform(match)
            for (c in commands) {
                mqtt.send(c)
            }
        }
    }
}
