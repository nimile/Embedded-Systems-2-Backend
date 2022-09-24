package de.hrw.xilab.lora;

import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public final class LoRaWan implements AutoCloseable {
    private final LoraSpringGlue glue = LoraSpringGlue.getInstance();


    public void begin(String[] args) {

        // Start LoRa Communication
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Random rnd = new Random(System.currentTimeMillis());
                var bat = rnd.nextInt(0, 100);
                var wl = rnd.nextInt(0, 100);
                update("688b7fb2-92d1-49bb-0011-4212979ad6cf", Optional.of(bat), Optional.of(wl));
            }
        };
        new Timer().scheduleAtFixedRate(task, 0, TimeUnit.SECONDS.toMillis(10));
    }

    public void update(String uuid, Optional<Integer> battery, Optional<Integer> waterLevel){
        glue.updateSpringDatabase(uuid, battery, waterLevel);
    }


    @Override
    public void close() {

    }
}
