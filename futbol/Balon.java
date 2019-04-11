/*
Copyright 2006 by Daniel Kuebrich
Licensed under the Academic Free License version 3.0
See the file "LICENSE" for more information
 */
// Class Ball
package futbol;

import java.awt.*;
import sim.util.*;
import sim.engine.*;

public class Balon extends Entity implements Steppable {
    // used to determine if the ball is stuck

    public MutableDouble2D stillPos;                           // last position
    public double dt;                                   // delta time--how many steps it has been still

    public Balon(final double x, final double y) {
        super(x, y, 1, Color.white);

        cap = 0.70;
        radius =2;


        mass= .5;
        bump = new MutableDouble2D(0, 0);
        stillPos = new MutableDouble2D(0, 0);
        dt = 0;
    }

    public MutableDouble2D getForces(final FutbolEGC keepaway) {
        sumVector.setTo(0, 0);
        Bag objs = keepaway.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 100);

        double dist = 0;

        for (int x = 0; x < objs.numObjs; x++) {
            if (objs.objs[x] != this && !(objs.objs[x] instanceof LineaBandas) && !(objs.objs[x] instanceof Porteria) && !(objs.objs[x] instanceof LineaMedioCampo) && !(objs.objs[x] instanceof LineasAreas) && !(objs.objs[x] instanceof PorteriaRival)) {
                dist = ((Entity) objs.objs[x]).loc.distance(loc);

                if ((((Entity) objs.objs[x]).radius + radius) > dist) // collision!
                {
                    if (objs.objs[x] instanceof Balon) {
                        // ball
                        // actually this is not possible with current settings
                    } else // if(objs.objs[x] instanceof Ball)
                    {
                        // bot
                        // and this is handled by the bots themselves
                    }
                }
            }
        }

        // add bump vector
        sumVector = sumVector.addIn(bump);
        bump.x = 0;
        bump.y = 0;
        return sumVector;
    }
    MutableDouble2D friction = new MutableDouble2D();
    MutableDouble2D stuckPos = new MutableDouble2D();

    public void step(final SimState state) {
        FutbolEGC keepaway = (FutbolEGC) state;

        // get force
        final MutableDouble2D force = getForces(keepaway);

        // acceleration = f/m
        accel.multiply(force, 1 / mass); // resets accel

        // hacked friction
        friction.multiply(velocity, -0.02);  // resets friction

        // v = v + a
        velocity.addIn(accel);
        velocity.addIn(friction);
        capVelocity();

        // L = L + v
        newLoc.add(loc, velocity);  // resets newLoc

        // is new location valid?
        if (isValidMove(keepaway, newLoc)) {
            loc = newLoc;
        }


        if (loc.distanceSq(stuckPos) < (0.1 * 0.1)) {
            dt++;
        } else {
            dt = 0;
            stuckPos.setTo(loc);
        }

        if (dt > 500000) {
            System.out.println("reiniciando sacque");
            dt = 0;
            stuckPos.setTo(loc);
            loc.x = 125;
            loc.y = 40;
        }

        keepaway.fieldEnvironment.setObjectLocation(this, new Double2D(loc));


        checaGol(keepaway);
        checaGolContra(keepaway);
    }

    void checaGol(FutbolEGC entorno) {

        Bag objs = entorno.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 20);
        double inicioPorteria = 110;
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa instanceof PorteriaRival) {

                //System.out.println("Viendo porteria contraria");
                for (; inicioPorteria < 133; inicioPorteria++) {

                    Double2D sitio = new Double2D(inicioPorteria, 135);
                  //  System.out.println(" sitio 1 " + sitio.toCoordinates());

                    if (1 > this.loc.distance(sitio)) {
                        System.out.println(" GOOOOOOL a favor");
                        entorno.registraGol(true );
                        entorno.centraBalon();
                    }

                }
            }

        }

    }

    void checaGolContra(FutbolEGC entorno) {

        Bag objs = entorno.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 20);

        double inicioPorteria = 110;
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa instanceof Porteria) {

              //  System.out.println("Viendo porteria");

                for (; inicioPorteria < 133; inicioPorteria++) {

                    Double2D sitio = new Double2D(inicioPorteria, 135);
              //      System.out.println(" sitio 1 " + sitio.toCoordinates());
                    Double2D sitio1 = new Double2D(inicioPorteria, 4);
              //      System.out.println(" sitio 2 " + sitio1.toCoordinates());
                    if (3 > this.loc.distance(sitio1)) {

                        System.out.println(" GOOOOOOL en contra");
                        entorno.registraGol(false);
                       entorno.centraBalon();

                    }

                }
            }

        }

    }
    
    public String toString(){
        return "balon";
    }
}

  