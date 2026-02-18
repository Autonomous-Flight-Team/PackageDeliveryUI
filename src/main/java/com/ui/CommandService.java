package com.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class CommandService {
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private Control control;
    private Data data;
    private Logging logging;

    public CommandService(Data data, Control control, Logging logging) {
        this.control = control;
        this.data = data;
        this.logging = logging;
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    // Log shutdown failure.
                    Thread.currentThread().interrupt();
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // --------------
    // CONNECTION
    // --------------

    public void connectToDroneAsync() {
        Task<Void> connectTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Connecting to drone");
                control.tryConnectToDrone();
                return null;
            }
        };

        connectTask.setOnSucceeded(e -> {
            logging.logInfo("Connected to drone");
        });

        connectTask.setOnFailed(e -> {
            logging.logError("Failed to connect to drone");
        });

        executor.submit(connectTask);
    }

    // --------------
    // COMMANDS
    // --------------

    public void tryArmDroneAsync() {
        Task<Void> armTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Arming drone");
                control.tryArmDrone();
                return null;
            }
        };

        armTask.setOnSucceeded(e -> {
            logging.logInfo("Drone armed");
        });

        armTask.setOnFailed(e -> {
            logging.logError("Drone failed to arm");
        });

        executor.submit(armTask);
    }

    public void tryDisarmDronAsync() {
        Task<Void> disarmTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Disarming drone");
                control.tryDisarmDrone();
                return null;
            }
        };

        disarmTask.setOnSucceeded(e -> {
            logging.logInfo("Drone disarmed");
        });

        disarmTask.setOnFailed(e -> {
            logging.logError("Drone failed to disarm");
        });

        executor.submit(disarmTask);
    }

    public void startFlightAsync() {
        Task<Void> startFlightTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Starting flight");
                control.tryStartFlight();
                return null;
            }
        };

        startFlightTask.setOnSucceeded(e -> {
            logging.logInfo("Flight started");
        });

        startFlightTask.setOnFailed(e -> {
            logging.logError("Failed to start flight");
        });

        executor.submit(startFlightTask);
    }

    public void returnHomeAsync() {
        Task<Void> returnTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Returning drone to home");
                control.tryReturnToHome();
                return null;
            }
        };

        returnTask.setOnSucceeded(e -> {
            logging.logInfo("Drone returning to home");
        });

        returnTask.setOnFailed(e -> {
            logging.logError("Drone failed to initate return to home");
        });

        executor.submit(returnTask);
    }

    public void landAsync() {
        Task<Void> landTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Drone landing");
                control.tryLand();
                return null;
            }
        };

        landTask.setOnSucceeded(e -> {
            logging.logInfo("Drone initiated landing");
        });

        landTask.setOnFailed(e -> {
            logging.logError("Drone failed to initiate landing");
        });

        executor.submit(landTask);
    }

    public void killAsync() {
        Task<Void> killTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Killing drone");
                control.tryKill();
                return null;
            }
        };

        killTask.setOnSucceeded(e -> {
            logging.logInfo("Drone killed");
        });

        killTask.setOnFailed(e -> {
            logging.logError("Failed to kill drone");
        });

        executor.submit(killTask);
    }


    // --------------
    // TELEMETRY
    // --------------

    public void updateFlightsAsync() {
        Task<Void> killTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Updating flight registry");
                Platform.runLater(() -> {data.setAvailableFlights(control.getAvailableFlights());});
                Platform.runLater(() -> {data.setSelectedFlight(0);});
                return null;
            }
        };

        killTask.setOnSucceeded(e -> {
            logging.logInfo("Flights updated");
        });

        killTask.setOnFailed(e -> {
            logging.logError("Failed to update flights");
        });

        executor.submit(killTask);
    }


    // --------------
    // HEALTH
    // --------------

    public void runAllHealthChecksAsync() {
        Task<Void> healthCheckTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logging.logCommand("Running all health checks");
                control.checkHealthAll();
                return null;
            }
        };

        healthCheckTask.setOnSucceeded(e -> {
            logging.logInfo("Health checks started");
        });
        
        healthCheckTask.setOnFailed(e -> {
            logging.logError("Failed to start health checks");
        });

        executor.submit(healthCheckTask);
    }
}
