# Drone GUI Control Station - Complete Project Guide

## Project Overview

**Goal:** Build a Java GUI application that sends commands to a Jetson running Python MAVSDK scripts, which communicate with a Pixhawk to control an autonomous drone.

**Tech Stack:**
- **GUI:** Java 17+ with JavaFX
- **Backend API:** Python Flask + MAVSDK
- **Testing:** ArduPilot SITL + Gazebo
- **Target Deployment:** Jetson Nano/Xavier
- **Communication:** REST API over WiFi/Ethernet

## System Architecture

```
┌─────────────────┐      REST API       ┌──────────────────┐     MAVLink      ┌───────────────┐
│   Java GUI App  │ ◄─────────────────► │ Jetson/Computer  │ ◄──────────────► │   Pixhawk/    │
│  (Ground Ctrl)  │   (WiFi/Ethernet)   │  Python Flask    │   (Serial/USB)   │    SITL       │
└─────────────────┘                     │  MAVSDK Scripts  │                  └───────────────┘
                                        └──────────────────┘
                                                 │
                                                 ▼
                                        ┌──────────────────┐
                                        │ Gazebo Simulator │
                                        │   (for SITL)     │
                                        └──────────────────┘
```

## Team Member Roles

- **Member A:** DevOps/Infrastructure - Environment setup, testing, deployment
- **Member B:** Frontend/GUI Lead - JavaFX application, UI/UX
- **Member C:** Backend/Python Lead - Flask API, MAVSDK integration
- **Member D:** Integration Specialist - Services layer, telemetry, architecture

---

## PHASE 1: Foundation & Setup (Week 1-2)

### Task 1.1: Development Environment Setup (Member A)
**Duration:** 3 days

**Deliverables:**
1. Git repository with proper .gitignore
2. Development environments for all team members
3. README.md with setup procedures

**Key Steps:**
- Install ArduPilot SITL
- Install Gazebo Garden/Harmonic
- Install MAVSDK Python: `pip install mavsdk`
- Install Java 17+, Maven 3.8+, JavaFX SDK
- Test SITL: `sim_vehicle.py -v ArduCopter -f gazebo-iris`

---

### Task 1.2: Java GUI Project Structure (Member B)
**Duration:** 4 days

**Deliverables:**
1. Maven project with proper structure
2. Basic JavaFX application skeleton
3. UI mockups/wireframes

**Project Structure:**
```
drone-gui/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/dronecontrol/
│   │   │       ├── MainApp.java
│   │   │       ├── controllers/
│   │   │       │   ├── MainController.java
│   │   │       │   ├── ConnectionController.java
│   │   │       │   ├── TelemetryController.java
│   │   │       │   └── CommandController.java
│   │   │       ├── models/
│   │   │       │   ├── DroneState.java
│   │   │       │   ├── TelemetryData.java
│   │   │       │   └── ConnectionConfig.java
│   │   │       ├── services/
│   │   │       │   ├── DroneConnectionService.java
│   │   │       │   ├── CommandService.java
│   │   │       │   └── TelemetryService.java
│   │   │       └── utils/
│   │   │           ├── JsonParser.java
│   │   │           └── Logger.java
│   │   └── resources/
│   │       ├── fxml/
│   │       │   ├── main.fxml
│   │       │   ├── connection.fxml
│   │       │   ├── telemetry.fxml
│   │       │   └── commands.fxml
│   │       ├── css/
│   │       │   └── styles.css
│   │       └── config.properties
│   └── test/
│       └── java/
│           └── com/dronecontrol/
└── README.md
```

**Key Maven Dependencies:**
```xml
<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.1</version>
</dependency>

<!-- HTTP Client -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<!-- JSON -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

---

### Task 1.3: Python Flask API Skeleton (Member C)
**Duration:** 3 days

**Deliverables:**
1. Flask API server with basic endpoints
2. Mock responses (no MAVSDK yet)
3. API documentation

**Project Structure:**
```
jetson-api/
├── requirements.txt
├── config.py
├── api_server.py
├── mavsdk_scripts/
│   ├── __init__.py
│   ├── connect.py
│   ├── takeoff.py
│   ├── land.py
│   ├── arm_disarm.py
│   └── telemetry_stream.py
├── utils/
│   ├── logger.py
│   └── validators.py
└── tests/
    └── test_endpoints.py
```

**Key API Endpoints:**
```
POST   /api/connect          - Connect to drone
POST   /api/disconnect       - Disconnect from drone
GET    /api/status           - Get connection status
POST   /api/command/arm      - Arm drone
POST   /api/command/disarm   - Disarm drone
POST   /api/command/takeoff  - Takeoff to altitude
POST   /api/command/land     - Land drone
POST   /api/command/rtl      - Return to launch
GET    /api/telemetry        - Get telemetry data
GET    /api/health           - Health check
```

---

## PHASE 2: Core Communication Layer (Week 3)

### Task 2.1: Java HTTP Client Service (Member B)
**Duration:** 4 days

**Deliverables:**
1. DroneConnectionService class
2. CommandService class
3. Unit tests for services

**Key Classes:**
- `ConnectionConfig` - Server address, port, connection string
- `DroneState` - Enum: DISCONNECTED, CONNECTING, CONNECTED, ARMED, FLYING, etc.
- `TelemetryData` - Battery, altitude, GPS, speed, mode
- `DroneConnectionService` - Connection management, observable properties
- `CommandService` - Send commands via REST API

**Important Patterns:**
- Use CompletableFuture for async operations
- JavaFX Properties for UI binding
- OkHttp for HTTP client
- Gson for JSON serialization

---

### Task 2.2: Build Basic GUI Components (Member B)
**Duration:** 3 days

**Deliverables:**
1. Connection panel
2. Status display
3. Command buttons with state management

**UI Components:**
- Connection panel (IP, port, connection string, connect/disconnect buttons)
- Command panel (ARM, DISARM, TAKEOFF with altitude, LAND, RTL)
- Emergency stop button (always visible, red)
- Telemetry display (battery, altitude, GPS, speed, mode)
- Console log (TextArea for messages)
- Status bar

**State Management:**
- Disable buttons based on DroneState
- Example: TAKEOFF only enabled when state = ARMED
- Update connection status label with color (green/red)

---

## PHASE 3: MAVSDK Integration (Week 4)

### Task 3.1: MAVSDK Python Scripts (Member C)
**Duration:** 5 days

**Deliverables:**
1. Reusable MAVSDK connection module
2. Individual scripts for each command
3. Telemetry streaming script

**Key Scripts:**
- `connect.py` - Singleton connection manager
- `arm_disarm.py` - Arm/disarm functions
- `takeoff.py` - Takeoff to altitude
- `land.py` - Land and RTL functions
- `telemetry_stream.py` - Continuous telemetry streaming

**Testing:**
```bash
# Terminal 1: Start SITL
sim_vehicle.py -v ArduCopter -f gazebo-iris --console --map

# Terminal 2: Test scripts
cd mavsdk_scripts
python arm_disarm.py arm
python takeoff.py 10
python land.py
```

---

### Task 3.2: Integrate MAVSDK into Flask API (Member C)
**Duration:** 3 days

**Deliverables:**
1. Updated Flask endpoints that call MAVSDK scripts
2. Background thread for telemetry streaming
3. Proper async handling

**Key Implementation:**
- Run asyncio event loop in background thread
- Use `asyncio.run_coroutine_threadsafe()` to call MAVSDK from Flask
- Start telemetry streaming on connect
- Stop telemetry on disconnect

---

### Task 3.3: Java TelemetryService (Member D)
**Duration:** 4 days

**Deliverables:**
1. TelemetryService class with polling
2. UI telemetry display updates
3. Performance optimization

**Key Features:**
- Poll telemetry every 500ms
- Update JavaFX properties on Platform thread
- Bind properties to UI labels
- Color coding (battery: green/red, GPS: green/red)

---

## PHASE 4: Testing & Documentation (Week 5-6)

### Task 4.1: SITL + Gazebo Testing (Members A + C)
**Duration:** 5 days

**Testing Checklist:**
- [ ] Connect/disconnect
- [ ] Arm/disarm
- [ ] Takeoff to various altitudes (5m, 10m, 20m)
- [ ] Land from flight
- [ ] Return to launch
- [ ] Emergency stop
- [ ] Telemetry accuracy
- [ ] Handle connection loss
- [ ] UI responsiveness
- [ ] No freezing or crashes

---

### Task 4.2: Scalable Architecture (Member D)
**Duration:** 4 days

**Deliverables:**
1. Command pattern implementation
2. Mission planner interface stub
3. Architecture documentation

**New Structure:**
```
drone-gui/src/main/java/com/dronecontrol/
├── commands/          # NEW: Command pattern
│   ├── DroneCommand.java (interface)
│   ├── ArmCommand.java
│   ├── TakeoffCommand.java
│   └── LandCommand.java
├── mission/           # NEW: Mission planning (for future)
│   ├── Mission.java
│   ├── Waypoint.java
│   └── MissionPlanner.java (stub)
```

**Benefits:**
- Easy to add new commands
- Mission planning ready
- Undo/redo capability
- Command validation

---

### Task 4.3: Documentation & Deployment (Member A)
**Duration:** 3 days

**Documents to Create:**
1. README.md - Quick start guide
2. API.md - Complete API reference
3. DEPLOYMENT.md - Jetson deployment guide
4. USER_MANUAL.md - End-user instructions

**Jetson Deployment Topics:**
- Flash JetPack
- Install dependencies
- Set up systemd service
- Configure WiFi hotspot
- Set static IP
- Serial connection to Pixhawk

---

## Critical Safety Considerations

1. **Always test with SITL first** - Never test new code on real hardware
2. **Emergency stop** - Always available, bypasses all logic
3. **Command validation** - Check preconditions before executing
4. **Connection monitoring** - Heartbeat every 1-2 seconds
5. **Geofencing** - Implement software boundaries
6. **RC transmitter priority** - Physical RC should override software
7. **Auto-land on connection loss** - Failsafe behavior

---

## Network Configuration Options

### WiFi Direct (Field Operations)
- Jetson creates WiFi hotspot
- GUI laptop connects to Jetson's network
- Static IP on Jetson (e.g., 192.168.4.1)
- No internet required

### Infrastructure WiFi (Lab Testing)
- Both connect to same router
- Use mDNS/Zeroconf for discovery
- Good for development

### Ethernet (Bench Testing)
- Direct connection via cable
- Fastest, most reliable
- Good for stationary testing

---

## Connection Strings

**For SITL:**
```
udp://:14540
```

**For Real Pixhawk (on Jetson):**
```
serial:///dev/ttyTHS0:921600    # Jetson UART
serial:///dev/ttyUSB0:921600    # USB connection
```

---

## Key Technologies & Libraries

### Java
- JavaFX 21+ (UI framework)
- OkHttp 4.12+ (HTTP client)
- Gson 2.10+ (JSON parsing)
- Maven (build tool)

### Python
- Flask 3.0+ (web framework)
- MAVSDK 2.0+ (drone communication)
- asyncio (async operations)

### Tools
- Git (version control)
- ArduPilot SITL (simulator)
- Gazebo (3D visualization)
- MAVProxy (debugging)

---

## Task Assignment Matrix

| Phase | Task | Assignee | Duration | Dependencies |
|-------|------|----------|----------|--------------|
| 1.1 | Dev Environment Setup | Member A | 3 days | None |
| 1.2 | Java GUI Structure | Member B | 4 days | 1.1 |
| 1.3 | Flask API Skeleton | Member C | 3 days | 1.1 |
| 2.1 | Java HTTP Client | Member B | 4 days | 1.2, 1.3 |
| 2.2 | GUI Components | Member B | 3 days | 2.1 |
| 3.1 | MAVSDK Scripts | Member C | 5 days | 1.3 |
| 3.2 | Integrate MAVSDK | Member C | 3 days | 3.1 |
| 3.3 | Telemetry Service | Member D | 4 days | 2.1, 3.2 |
| 4.1 | SITL Testing | A + C | 5 days | All above |
| 4.2 | Scalable Arch | Member D | 4 days | All above |
| 4.3 | Documentation | Member A | 3 days | All above |

**Total Timeline: ~6 weeks**

---

## Future Enhancements (Post-MVP)

1. **Mission Planning**
   - Waypoint editor
   - Route visualization
   - Mission upload/download
   - Mission simulation

2. **Advanced Features**
   - Live video feed
   - 3D map display
   - Flight path recording
   - Geofencing editor
   - Multi-drone support

3. **Logging & Analytics**
   - Flight logs
   - Telemetry recording
   - Performance metrics
   - Crash analysis

---

## Common Issues & Solutions

### SITL won't start
```bash
# Clean and rebuild
cd ardupilot
./waf clean
./waf configure --board sitl
./waf copter
```

### Flask API connection refused
```bash
# Check if Flask is running
ps aux | grep api_server.py

# Check firewall
sudo ufw status
sudo ufw allow 8000
```

### JavaFX GUI won't launch
```bash
# Verify Java version
java --version  # Should be 17+

# Check JavaFX modules
mvn dependency:tree | grep javafx
```

### MAVSDK connection timeout
```bash
# Verify SITL is listening
netstat -an | grep 14540

# Check connection string
# Should be: udp://:14540 for SITL
```

---

## Resources & Documentation

### Official Documentation
- ArduPilot: https://ardupilot.org/copter/
- MAVSDK Python: https://mavsdk-python-docs.jonasvautherin.com/
- JavaFX: https://openjfx.io/
- Flask: https://flask.palletsprojects.com/

### Tutorials
- ArduPilot SITL: https://ardupilot.org/dev/docs/sitl-simulator-software-in-the-loop.html
- Gazebo: https://gazebosim.org/docs
- MAVSDK Examples: https://github.com/mavlink/MAVSDK-Python/tree/main/examples

---

## Contact & Support

**For questions on specific layers:**
- **Java GUI:** Start chat with "I'm working on the Java GUI layer..."
- **Python API:** Start chat with "I'm working on the Flask API layer..."
- **MAVSDK:** Start chat with "I'm working on MAVSDK integration..."
- **Testing:** Start chat with "I'm testing with SITL and Gazebo..."

**Include in new chat:**
- Layer you're working on
- Specific task from this guide
- Any error messages or issues

---

## Version History

- v1.0 - Initial project structure and task breakdown
- Created: 2026-02-06

---

**END OF MASTER GUIDE**
