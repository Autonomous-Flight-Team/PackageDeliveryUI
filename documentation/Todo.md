Features that need to be added
- TestMode should select the control interface that is created. Add once proper drone interfacing is working.
- Update flight data when a flight is selected, and when the flights are loaded. Maybe placeholder for loading single flight?

Immediate tasks - 
Add flight selection to the calibration page (top option).
Update flight system to have payload location options.
Telemetry saving and review.
Overhaul systems to make sure that data updating and logic is ready for drone connection.

System for handling pages that have UI elements that need to be created/destroyed based on temporal data - (available flights, waypoint list, etc.) Just create an update function, and re-set the BorderPane.setLeft(examplePane()); to recreate the page with the new data. Have a list
of pages that depend on temporal data, collate that data, and run the appropriate update functions when the data changes.

Current work -
- Update the flight system properly with REST calling.