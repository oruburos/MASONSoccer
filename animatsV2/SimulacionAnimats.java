package animatsV2;

import java.util.Vector;
import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;

public class SimulacionAnimats extends SimState {

    public Continuous2D ambiente;
    public double width = 100;
    public double height = 100;
    public int numPasos;
    public int limiteProximidad;
    public float desgastoXPaso;
    public int numAnimats;

    /* Parametros propagables
     *
     *
     */
    public SimulacionAnimats(long seed) {
        super(seed);
        numAnimats = 10;
        limiteProximidad = 10;
        desgastoXPaso = 0.001f;
    }

    public void start() {
        super.start();

        numPasos = 0;
        ambiente = new Continuous2D(width, width, height);

        Vector<Double2D> ubicaciones = new Vector<Double2D>();

        for (int i = 0; i <= 50; i++) {
            ubicaciones.add(new Double2D(random.nextDouble() * width, random.nextDouble() * height));
        }



        Obstaculo obstaculo = new Obstaculo();

        for (int x = 0; x < this.numAnimats; x++) {
            Animat animat = new Animat();
            ambiente.setObjectLocation(animat, ubicaciones.get(x));
            schedule.scheduleRepeating(animat);

        }


        for (int x = 10; x < 32; x++) {
            Agua agua = new Agua();
            ambiente.setObjectLocation(agua, ubicaciones.get(x));


        }
                for (int x = 45; x < 50; x++) {
        obstaculo = new Obstaculo();
        ambiente.setObjectLocation(obstaculo, ubicaciones.get(x));

        }
        
        for (int x = 32; x < 50; x++) {
            Comida comida = new Comida();
            ambiente.setObjectLocation(comida, ubicaciones.get(x));

        }

    }

    public static void main(String[] args) {
        doLoop(SimulacionAnimats.class, args);
        System.exit(0);
    }
}
