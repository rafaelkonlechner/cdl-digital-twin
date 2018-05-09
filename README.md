# Simulating a Pick and Place Unit

This project explores model-integrated control and orchestration mechanisms of internet-enabled production robots in a simulated pick and place unit. It lets you control a 6-DOF robotic arm, simulated in Blender GE, and monitor sensor and camera signals over MQTT.

<div style="text-align: center;">
<div style="float: left; display: inline-block;">
    <img src="docs/Simulation.gif" height="230"/>
    <p>*Pick and Place Unit in Blender GE*</p>
</div>
<div style="display: inline-block;">
    <img src="docs/Timeline.gif" style="width: 230px; height: 90px;"/><br/>
    <p>*Sensor Timeline in [Chronograf](https://portal.influxdata.com/downloads)*</p>
    <img src="docs/Pickup.gif" style="height: 100px;"/>
    <img src="docs/Detection.gif" style="margin-left: 20px; height: 100px;"/>
    <p>*Cameras for Conveyor and QR-Codes*</p>
</div>
</div>

## Getting Started

**Install Dependencies**

You need these dependencies to get started:
* Docker & Docker Compose ([download](https://www.docker.com/community-edition#/download))
* Blender: 2.78c ([download](https://www.blender.org/download/))

**Setup Blender**

Blender requires a few extra libraries in order to be able to publish and subscribe to MQTT queues and render images. To add the necessary scripts and libraries in your installation, go to **File > User Preferences > File > Scripts** and select the `blender-scripts/` directory from the project. Save the selection by hitting ***Save User Settings***.

Finally, open the simulation file `Pick-and-Place-Simulation.blend` with Blender. Make sure to select **Blender Game** in the engine selection on the top &mdash; it might be set to **Blender Render**.

**Start Docker Services**

In the project root directory, run:

```
docker-compose up
```

This will install and run all additional services to control and monitor the robot. After all services started, the dashboard is available at  [http://localhost:8080](http://localhost:8080). In detail, the services are:

* Control Server (`Port 8080`): Collects sensor data and executes the control procedure via MQTT.
* RabbitMQ with MQTT Plugin (`Port 1883`): MQTT message queue
* InfluxDB (`Port 8086`): Time series database for recording sensor measurements
* Camera Object Tracker (`Port 3000`): Image recognition service that identifies and locates objects in images

### Running Simulations

 Hit `p` in Blender to start a simulation (exit with `Esc`). Now, you can control the robot either manually, by using the arrow keys and `WASD`, or by starting the automatic control procedure. For this, go to the [web dashboard](http://localhost:8080) and hit ***Start*** in the Controls section. This will run the control procedure described below. Clicking ***Record*** in the dashboard toggles the recording of sensor measurements in InfluxDB. For visualizing these time series, you can use [Chronograf](https://portal.influxdata.com/downloads) or [Grafana](http://grafana.com).

#### Control Procedure

If you hit *"Start"*, the unit will loop through this sequence of commands:

1. Push item onto conveyor
2. Adjust item to be in pickup window
3. Lift item from conveyor
4. Place item on the platform
5. Scan the QR-code for item class (*Class 1* or *Class 2*)
6. Heat the platform to 120°C for *Class 1* or 150°C for *Class 2*
7. Grab item and drop it over the left slide for *Class 1* or the right slide for *Class 2*
8. Repeat Step 1

## Documentation

A more thorough documentation is available in German [on GitHub](https://github.com/rafaelkonlechner/cdl-digital-twin/blob/master/docs/index.md).

### Notes

This research project is part of the [CDL-MINT](https://cdl-mint.big.tuwien.ac.at/) laboratory for model-integrated smart production.
