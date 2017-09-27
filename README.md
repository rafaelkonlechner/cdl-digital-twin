# Digital Twin for a Pick and Place Unit

Experience the concept of digital twins in industrial production first hand, in your web browser. The project lets you simulate a 3D model of a 6-DOF robotic arm in a pick and place unit with Blender GE and control it interactively, either with your keyboard or by talking to the MQTT API. You can monitor the robot in real-time on the web dashboard with various sensors and cameras deployed on the unit.

## Getting Started

This guide will help you set up the project to run the simulation locally.

### Install dependencies

You will need to install these dependencies in order to get started:
* Docker: API version 1.12+
* Blender: 2.78c

### Setup Blender

Blender requires a few extra libraries in order to be able to publish and subscribe to MQTT messages and render images. This can be done by adding scripts and libraries to your *User Preferences*. Go to ***File > User Preferences > File > Scripts*** and select the `blender-scripts/` directory from the project. Save the selection by hitting ***Save User Settings***.

Finally, open the simulation file `Pick-and-Place-Simulation.blend` with Blender. Make sure to select **"Blender Game"** in the engine selection on the top &mdash; it might be set to **"Blender Render"**. Hitting the `p` key will start the simulation.

### Start Docker Services

In the project home directory, run `docker-compose up`. This will install and run all required services to control and monitor the robot in your browser. These services are:

* InfluxDB (`Port 8086`)
* RabbitMQ and MQTT Plugin (`Port 1883`)

### Run the Spring Boot Server

The server uses the *Spring Boot* framework for backend development and *Gradle* as a build tool. For building and running the server, use the Gradle wrapper provided in the project by navigating to the `MQTTServer/` directory and typing:

`./gradlew bootRun` on Linux and macOS

`gradlew bootRun`
on Windows

After all services started, visit [http://localhost:8080](http://localhost:8080) for the dashboard.

### Optional: Chronograf InfluxDB Client

For visualizing time series that are recorded in InfluxDB, you can use Chronograf. There are multiple versions for different systems available on the [download page](https://portal.influxdata.com/downloads).


## Documentation

Currently, the project documentation is available in German [here](https://github.com/rafaelkonlechner/cdl-digital-twin/blob/master/docs/index.md).

## License

TBD

## Notes

This research project is part of the [CDL-MINT](https://cdl-mint.big.tuwien.ac.at/) laboratory for model-integrated smart production.
