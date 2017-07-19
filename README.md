# Digital Twin for a Pick and Place Unit

Experience the concept of digital twins in industrial production, first hand, in your web browser. The project lets you simulate a 3D model of a 6-DOF robotic arm in a pick and place unit with Blender GE and control it interactively, either with your keyboard or by talking to the MQTT API. You can monitor the robot in near real-time on the web dashboard with various sensors and cameras deployed on the unit.

## Getting Started

### Install dependencies
You will need to install these dependencies in order to get started:
* Docker: API version 1.12+
* Blender: 2.78c

### Setup Blender
Blender requires a few extra libraries in order to be able to publish and subscribe to MQTT messages. This can be done by installing the Python package manager `pip` and using it to install further libraries.

TBD

Finally, open `Pick-and-Place-Simulation.blend` with Blender. Make sure to select **"Blender Game"** in the engine selection on the top - it might be set to **"Blender Render"**. Hitting `p` will start the simulation.

### Startup Services

In the project home directory, run `docker-compose up`. This will start (and install) all required services to control and monitor the robot in your browser. After all services started, visit [http://localhost:8080](http://localhost:8080) for the dashboard.

## Documentation

TBD

## Clone and Build

TBD

## License

TBD

## Notes

This research project is part of the [CDL-MINT](https://cdl-mint.big.tuwien.ac.at/) laboratory for model-integrated smart production.
