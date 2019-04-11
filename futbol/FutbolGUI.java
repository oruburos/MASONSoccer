/*
  Copyright 2006 by Daniel Kuebrich
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package futbol;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.continuous.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import sim.field.continuous.Continuous2D;
import sim.portrayal.LocationWrapper;
import sim.portrayal.Portrayal;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.util.Bag;
import sim.util.Double2D;

public class FutbolGUI extends GUIState
    {
    public Display2D display;
    public JFrame displayFrame;
    public JTextField marcador;

    ContinuousPortrayal2D entityPortrayal = new ContinuousPortrayal2D();

    public static void main(String[] args)
        {
        FutbolGUI botball = new FutbolGUI();
        Console c = new Console(botball);
        c.setVisible(true);
        }
    
    public FutbolGUI() { super(new FutbolEGC(System.currentTimeMillis())); }
    
    public FutbolGUI(SimState state) { super(state); }
    
    public static String getName() { return "Futbol"; }
    
    public void start()
        {
        super.start();
        // set up our portrayals
        setupPortrayals();
        }
    
    public void load(SimState state)
        {
        super.load(state);
        // we now have new grids.  Set up the portrayals to reflect that
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

    public void setupPortrayals()
        {
        entityPortrayal.setField(((FutbolEGC)state).fieldEnvironment);
      //  entityPortrayal.setPortrayalForClass(ContinuousPortrayal2D.class, new ImagePortrayal2D(createImageIcon("campo.png", "campo").getImage(), 4));
        entityPortrayal.setPortrayalForClass(Futbolista.class, new sim.portrayal.simple.OvalPortrayal2D(Color.red));
        entityPortrayal.setPortrayalForClass(Futbolista.class, new sim.portrayal.simple.OvalPortrayal2D(Color.blue));
       // entityPortrayal.setPortrayalForClass(Balon.class, new sim.portrayal.simple.OvalPortrayal2D(Color.white));
        Portrayal infoDisplay = new LabelledPortrayal2D(new sim.portrayal.simple.OvalPortrayal2D(Color.white), "balon");
         entityPortrayal.setPortrayalForClass(Balon.class, infoDisplay);
        entityPortrayal.setPortrayalForClass(LineaBandas.class, new sim.portrayal.simple.RectanglePortrayal2D(Color.white,1));
        entityPortrayal.setPortrayalForClass(LineaMedioCampo.class, new sim.portrayal.simple.RectanglePortrayal2D(Color.white,1));
        entityPortrayal.setPortrayalForClass(LineasAreas.class, new sim.portrayal.simple.RectanglePortrayal2D(Color.white,1));

        entityPortrayal.setPortrayalForClass(Porteria.class, new ImagePortrayal2D(createImageIcon("arcocorto.png", "porteria").getImage(), 6));
        entityPortrayal.setPortrayalForClass(PorteriaRival.class, new ImagePortrayal2D(createImageIcon("arcocorto.png", "porteria").getImage(), 6));
        display.reset();
        display.repaint();
        }
    
    public void init(final Controller c)
        {
        super.init(c);
        
        display = new Display2D(500,600,this,1); // at 400x400, we've got 4x4 per array position
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach(entityPortrayal,"Futbol");

        display.setBackdrop(new Color(0,80,0));  // a dark green

      MouseInputAdapter adapter = new MouseInputAdapter() {

            Entity bug = null;                 // Lo que estamos moviendo

            public void mousePressed(java.awt.event.MouseEvent e) {


                try {
                    int tipo = e.getButton();
                    if (tipo == 1) {
                        final Point point = e.getPoint();
                        Continuous2D field = (Continuous2D) (entityPortrayal.getField());
                        if (field == null) {
                            return;
                        }
                        bug = null;

                        Rectangle2D.Double rect = new Rectangle2D.Double(point.x, point.y, 1, 1);

                        Bag hit = new Bag();
                        entityPortrayal.hitObjects(display.getDrawInfo2D(entityPortrayal, rect), hit);
                        if (hit.numObjs > 0) //
                        {
                            bug =(Entity)((LocationWrapper) (hit.objs[hit.numObjs - 1])).getObject();
                       //     System.out.println( " clase de loq ue agarre " + bug.getClass());
                           if (bug instanceof Entity){
                            bug = (Balon) (((LocationWrapper) (hit.objs[hit.numObjs - 1])).getObject());
                            }
                         
                        }
                    }
                } catch (ClassCastException ex) {
                  //  System.out.println("Excepcion, lo que agarre no era de la clase adecuada" + ex.toString());

                }
            }

            public void mouseReleased(java.awt.event.MouseEvent e) {
                bug = null;
            }

            // We move the ball in our Field, adding in the computed difference as necessary
            public void mouseDragged(java.awt.event.MouseEvent e) {
                final Point point = e.getPoint();
                Continuous2D field = (Continuous2D) (entityPortrayal.getField());
                if (bug == null || field == null) {
                    return;
                }


                Double2D doble2d = entityPortrayal.getLocation(display.getDrawInfo2D(entityPortrayal, point));
                   bug.setX(doble2d.x);
                   bug.setY(doble2d.y);
                   bug.setVelocityY(0);
                   bug.setVelocityX(0);
                   field.setObjectLocation(bug, doble2d);


                c.refresh();
            }
        };



        display.insideDisplay.addMouseListener(adapter);
        display.insideDisplay.addMouseMotionListener(adapter);
        }
        
    public void quit()
        {
        super.quit();
        
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;  // let gc
        display = null;       // let gc
        }
    }
    
    
    
    
    
