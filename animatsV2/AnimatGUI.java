package animatsV2;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.continuous.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.event.MouseInputAdapter;
import sim.field.continuous.Continuous2D;
import sim.portrayal.LocationWrapper;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Int2D;

public class AnimatGUI extends GUIState {

    public Display2D display;
    public JFrame displayFrame;

    public static void main(String[] args) {
        AnimatGUI mav = new AnimatGUI();
        Console c = new Console(mav);
        c.setVisible(true);
    }

    public Object getSimulationInspectedObject() {

        return state;
    }
    ContinuousPortrayal2D agentsPortrayal = new ContinuousPortrayal2D();

    public AnimatGUI() {
        super(new SimulacionAnimats(System.currentTimeMillis()));
    }

    public AnimatGUI(SimState state) {
        super(state);
    }

    public static String getName() {
        return "Simulacion de Animats";
    }

    public void start() {
        super.start();
        setupPortrayals();
    }

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    protected ImageIcon createImageIcon(String path,
            String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            //System.out.println(" path : " + path);

          //  System.out.println("url: " + imgURL);
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("no se encontro la ruta : " + path);
            return null;
        }
    }

    public void setupPortrayals() {
        SimulacionAnimats swarm = (SimulacionAnimats) state;
        agentsPortrayal.setField(swarm.ambiente);
        /*
        Appearance appearance = new Appearance();
        appearance.setColoringAttributes(
        new ColoringAttributes(new Color3f(new Color(0,0,255)), ColoringAttributes.SHADE_GOURAUD));
        Material m= new Material();
        m.setDiffuseColor(new Color3f(new Color(255,255,0)));
        m.setSpecularColor(0.5f,0.5f,0.5f);
        m.setShininess(64f);
        appearance.setMaterial(m);
        agentsPortrayal.setPortrayalForClass( Animat.class ,new Shape3DPortrayal3D(new GullCG(),                appearance));

         */
        agentsPortrayal.setPortrayalForClass(Animat.class, new ImagePortrayal2D(createImageIcon("spider bot.png", "animat").getImage(), 4));
        /*
        agentsPortrayal.setPortrayalForClass(Animat.class,
        new CircledPortrayal2D ( new LabelledPortrayal2D(new OvalPortrayal2D(Color.yellow,2) ,"Animat")));
         */





//        agentsPortrayal.setPortrayalForClass(Agua.class, new CircledPortrayal2D ( new LabelledPortrayal2D(new OvalPortrayal2D(Color.blue,3) ,"Agua")));

        agentsPortrayal.setPortrayalForClass(Agua.class, new ImagePortrayal2D(createImageIcon("agua.png", "agua").getImage(), 5));


        agentsPortrayal.setPortrayalForClass(Obstaculo.class, new ImagePortrayal2D(createImageIcon("stone.png", "obstaculo").getImage(),3));

//         agentsPortrayal.setPortrayalForClass(Obstaculo.class, new RectanglePortrayal2D(8, true));
        //agentsPortrayal.setPortrayalForClass(Obstaculo.class, new CircledPortrayal2D ( new LabelledPortrayal2D(new OvalPortrayal2D(Color.black,3) ,"Piedra")));
        //agentsPortrayal.setPortrayalForClass(Comida.class, new LabelledPortrayal2D(new OvalPortrayal2D(Color.red,3) ,"Comida"));
        agentsPortrayal.setPortrayalForClass(Comida.class, new ImagePortrayal2D(createImageIcon("apple.png", "comida").getImage(), 5));

        agentsPortrayal.setPortrayalForClass(Depredador.class, new ImagePortrayal2D(createImageIcon("scorpion.png", "depredador").getImage(), 10));

        display.reset();
        Color pasto = new Color(75, 140, 75);
        display.setBackdrop(pasto);


        display.repaint();
    }

    public void init(final Controller c) {
        super.init(c);

        display = new Display2D(500, 350, this, 1);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Animats");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(agentsPortrayal, "Animats!");















        MouseInputAdapter adapter = new MouseInputAdapter() {

            //Animat bug = null;                 // Lo que estamos moviendo
            Object bug = null;                 // Lo que estamos moviendo

            public void mousePressed(java.awt.event.MouseEvent e) {


                try {
                    int tipo = e.getButton();
                    if (tipo == 1) {
                        final Point point = e.getPoint();
                        Continuous2D field = (Continuous2D) (agentsPortrayal.getField());
                        if (field == null) {
                            return;
                        }
                        bug = null;

                        // go through all the objects at the clicked point.  The objectsHitBy method
                        // doesn't return objects: it returns LocationWrappers.  You can extract the object
                        // by calling getObject() on the LocationWrapper.

                        Rectangle2D.Double rect = new Rectangle2D.Double(point.x, point.y, 1, 1);

                        Bag hit = new Bag();
                        agentsPortrayal.hitObjects(display.getDrawInfo2D(agentsPortrayal, rect), hit);
                        if (hit.numObjs > 0) //
                        {
                            bug =((LocationWrapper) (hit.objs[hit.numObjs - 1])).getObject();
                       //     System.out.println( " clase de loq ue agarre " + bug.getClass());
                             if (bug instanceof Animat){
                            bug = (Animat) (((LocationWrapper) (hit.objs[hit.numObjs - 1])).getObject());
                            }
                            if( bug instanceof Depredador){
                            bug = (Depredador) (((LocationWrapper) (hit.objs[hit.numObjs - 1])).getObject());
                            }
                        }
                    } else {

                      //  System.out.println(" Boton depredador");

                        SimulacionAnimats swarm = (SimulacionAnimats) state;


                        final Point point = e.getPoint();
                        Continuous2D field = (Continuous2D) (agentsPortrayal.getField());

                        Double2D doble2d = agentsPortrayal.getLocation(display.getDrawInfo2D(agentsPortrayal, point));
                        Depredador depredador = new Depredador(doble2d);
                        swarm.schedule.scheduleRepeating(depredador);
                        field.setObjectLocation(depredador, doble2d);
                        agentsPortrayal.setField(field);
                        c.refresh();
                    }

                } catch (ClassCastException ex) {
                    System.out.println("No estas seleccionando un animat");

                }
            }

            public void mouseReleased(java.awt.event.MouseEvent e) {
                bug = null;
            }

            // We move the ball in our Field, adding in the computed difference as necessary
            public void mouseDragged(java.awt.event.MouseEvent e) {
                final Point point = e.getPoint();
                Continuous2D field = (Continuous2D) (agentsPortrayal.getField());
                if (bug == null || field == null) {
                    return;
                }

                Double2D doble2d = agentsPortrayal.getLocation(display.getDrawInfo2D(agentsPortrayal, point));
                field.setObjectLocation(bug, doble2d);
                c.refresh();
            }
        };



        display.insideDisplay.addMouseListener(adapter);
        display.insideDisplay.addMouseMotionListener(adapter);

















    }

    public void quit() {
        super.quit();

        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;
        display = null;
    }
}
