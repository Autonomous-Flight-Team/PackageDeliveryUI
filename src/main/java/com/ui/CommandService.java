package com.ui;

import com.ui.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

import org.json.simple.JSONObject;

import javafx.concurrent.Task;

public class CommandService {
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private Control control;
    private Data data;

    public CommandService(Data data, Control control) {
        this.control = control;
        this.data = data;
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
                control.tryConnectToDrone();
                return null;
            }
        };

        connectTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        connectTask.setOnFailed(e -> {
            // TODO: Logging function
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
                control.tryArmDrone();
                return null;
            }
        };

        armTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        armTask.setOnFailed(e -> {
            // TODO: Logging function
        });

        executor.submit(armTask);
    }

    public void tryDisarmDronAsync() {
        Task<Void> disarmTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                control.tryDisarmDrone();
                return null;
            }
        };

        disarmTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        disarmTask.setOnFailed(e -> {
            // TODO: Logging function
        });

        executor.submit(disarmTask);
    }

    public void startFlightAsync() {
        Task<Void> startFlightTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                control.tryStartFlight();
                return null;
            }
        };

        startFlightTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        startFlightTask.setOnFailed(e -> {
            // TODO: Logging function
        });

        executor.submit(startFlightTask);
    }

    public void returnHomeAsync() {
        Task<Void> returnTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                control.tryReturnToHome();
                return null;
            }
        };

        returnTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        returnTask.setOnFailed(e -> {
            // TODO: Logging function
        });

        executor.submit(returnTask);
    }

    public void landAsync() {
        Task<Void> landTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                control.tryLand();
                return null;
            }
        };

        landTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        landTask.setOnFailed(e -> {
            // TODO: Logging function
        });

        executor.submit(landTask);
    }

    public void killAsync() {
        Task<Void> killTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                control.tryKill();
                return null;
            }
        };

        killTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        killTask.setOnFailed(e -> {
            // TODO: Logging function
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
                Platform.runLater(() -> {data.setAvailableFlights(control.getAvailableFlights());} );
                return null;
            }
        };

        killTask.setOnSucceeded(e -> {
            // TODO: Logging
        });

        killTask.setOnFailed(e -> {
            // TODO: Logging function
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
                control.checkHealthAll();
                return null;
            }
        };

        healthCheckTask.setOnSucceeded(e -> {
            // TODO: Logging function
        });
        
        healthCheckTask.setOnFailed(e -> {
            // TODO: Logging function
        });

        executor.submit(healthCheckTask);
    }
}
