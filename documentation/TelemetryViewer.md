# Telemetry Viewer Specs
The Telemetry Viewer provides an interface for replaying telemetry data from a flight. It uses a json telemetry file to replay
the flight.
Options for user control - pause/play. A slider for position in flight, on the bottom of the panel. A playback direction checkbox.
Speed settings. A selection of what file to play back.

The map display and telemetry display will update, based on the time and poll rate, by caching in advance the next 15 or so seconds
of data, and will accordingly display it on the bindings of the controller. A replay service will load and store the bindings, and load
the data values + logic.