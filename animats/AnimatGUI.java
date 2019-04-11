/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package animats;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.continuous.*;
import javax.swing.*;
import java.awt.*;
import sim.portrayal.simple.ImagePortrayal2D;

public class AnimatGUI extends GUIState 
    {
    public Display2D display;
    public JFrame displayFrame;

    public static void main(String[] args)
        {
        AnimatGUI mav = new AnimatGUI();  // randomizes by currentTimeMillis
        Console c = new Console(mav);
        c.setVisible(true);
        }

    public Object getSimulationInspectedObject() {

        return state; }  // non-volatile

    ContinuousPortrayal2D agentsPortrayal = new ContinuousPortrayal2D();
    
    public AnimatGUI()
        {
        super(new SimulacionAnimats(System.currentTimeMillis()));
        }
    
    public AnimatGUI(SimState state)
        {
        super(state);
        }

    public static String getName() { return "Simulacion de Animats"; }

    public void start()
        {
        super.start();
        setupPortrayals();
        }

    public void load(SimState state)
        {
        super.load(state);
        setupPortrayals();
        }
      /** Returns an ImageIcon, or null if the path was invalid. */
protected ImageIcon createImageIcon(String path,
                                           String description) {
    java.net.URL imgURL = getClass().getResource(path);
    if (imgURL != null) {
        System.out.println("todo chido path : " + path);

        System.out.println("url: " + imgURL);
        return new ImageIcon(imgURL, description);
    } else {
        System.err.println("no se encontro la ruta : " + path);
        return null;
    }
}
    public void setupPortrayals()
        {
        SimulacionAnimats swarm = (SimulacionAnimats)state;
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

          agentsPortrayal.setPortrayalForClass(Agua.class, new ImagePortrayal2D(createImageIcon("agua.png", "agua").getImage(), 4));


            agentsPortrayal.setPortrayalForClass(Obstaculo.class, new ImagePortrayal2D(createImageIcon("stone.png", "obstaculo").getImage(), 4));

        //agentsPortrayal.setPortrayalForClass(Obstaculo.class, new CircledPortrayal2D ( new LabelledPortrayal2D(new OvalPortrayal2D(Color.black,3) ,"Piedra")));
        //agentsPortrayal.setPortrayalForClass(Comida.class, new LabelledPortrayal2D(new OvalPortrayal2D(Color.red,3) ,"Comida"));
          agentsPortrayal.setPortrayalForClass(Comida.class, new ImagePortrayal2D(createImageIcon("apple.png", "comida").getImage(), 4));

        // reschedule the displayer
        display.reset();
        Color pasto = new Color(100, 250, 100);
        display.setBackdrop( pasto);
                
        // redraw the display
        display.repaint();
        }

    public void init(Controller c)
        {
        super.init(c);

        // make the displayer
        display = new Display2D(500,350,this,1);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Animats");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach( agentsPortrayal, "Animats!" );
        }
        
    public void quit()
        {
        super.quit();
        
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
        }

    }
